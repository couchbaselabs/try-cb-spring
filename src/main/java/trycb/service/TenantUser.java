/**
 * Copyright (C) 2021 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package trycb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.UpsertOptions;

import trycb.config.Booking;
import trycb.config.BookingRepository;
import trycb.config.User;
import trycb.config.UserRepository;
import trycb.model.Result;

@Service
public class TenantUser {

  private final TokenService jwtService;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;

  public TenantUser(TokenService tokenService, UserRepository userRepository, BookingRepository bookingRepository) {
    this.jwtService = tokenService;
    this.userRepository = userRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Try to log the given tenant user in.
   */
  public Result<Map<String, Object>> login(final String tenant, final String username, final String password) {
    UserRepository userRepository = this.userRepository.withScope(tenant);
    String queryType = String.format("KV get - scoped to %s.users: for password field in document %s", tenant,
        username);
    Optional<User> userHolder;
    try {
      userHolder = userRepository.findById(username);
    } catch (DocumentNotFoundException ex) {
      throw new AuthenticationCredentialsNotFoundException("Bad Username or Password");
    }
    User res = userHolder.get();
    if (BCrypt.checkpw(password, res.password)) {
      Map<String, Object> data = JsonObject.create().put("token", jwtService.buildToken(username)).toMap();
      return Result.of(data, queryType);
    } else {
      throw new AuthenticationCredentialsNotFoundException("Bad Username or Password");
    }
  }

  /**
   * Create a tenant user.
   */
  public Result<Map<String, Object>> createLogin(final String tenant, final String username, final String password,
      DurabilityLevel expiry) {
    UserRepository userRepository = this.userRepository.withScope(tenant);
    String passHash = BCrypt.hashpw(password, BCrypt.gensalt());
    User user = new User(username, passHash);
    UpsertOptions options = UpsertOptions.upsertOptions();
    if (expiry.ordinal() > 0) {
      options.durability(expiry);
    }
    String queryType = String.format("KV insert - scoped to %s.users: document %s", tenant, username);
    try {
      userRepository.withOptions(options).save(user);
      Map<String, Object> data = JsonObject.create().put("token", jwtService.buildToken(username)).toMap();
      return Result.of(data, queryType);
    } catch (Exception e) {
      throw new AuthenticationServiceException("There was an error creating account");
    }
  }

  /*
   * Register a flight (or flights) for the given tenant user.
   */
  public Result<Map<String, Object>> registerFlightForUser(final String tenant, final String username,
      final JsonArray newFlights) {
    UserRepository userRepository = this.userRepository.withScope(tenant);
    BookingRepository bookingRepository = this.bookingRepository.withScope(tenant);
    Optional<User> userDataFetch;
    try {
      userDataFetch = userRepository.findById(username);
    } catch (DocumentNotFoundException ex) {
      throw new IllegalStateException();
    }
    User userData = userDataFetch.get();

    if (newFlights == null) {
      throw new IllegalArgumentException("No flights in payload");
    }

    JsonArray added = JsonArray.create();
    ArrayList<String> allBookedFlights = null;
    if (userData.getFlightIds() != null) {
      allBookedFlights = new ArrayList(Arrays.asList(userData.getFlightIds())); // ArrayList(Arrays.asList(newFlights));
    } else {
      allBookedFlights = new ArrayList<>(newFlights.size());
    }

    for (Object newFlight : newFlights) {
      checkFlight(newFlight);
      JsonObject t = ((JsonObject) newFlight);
      t.put("bookedon", "try-cb-spring");
      Booking booking = new Booking(UUID.randomUUID().toString());
      booking.name = t.getString("name");
      booking.sourceairport = t.getString("sourceairport");
      booking.destinationairport = t.getString("destinationairport");
      booking.flight = t.getString("flight");
      booking.utc = t.getString("utc");
      booking.airlineid = t.getString("airlineid");
      booking.date = t.getString("date");
      booking.price = t.getInt("price");
      booking.day = t.getInt("day");
      bookingRepository.save(booking);
      allBookedFlights.add(booking.bookingId);
      added.add(t);
    }

    userData.setFlightIds(allBookedFlights.toArray(new String[] {}));
    userRepository.save(userData);

    JsonObject responseData = JsonObject.create().put("added", added);

    String queryType = String.format("KV update - scoped to %s.user: for bookings field in document %s", tenant,
        username);
    return Result.of(responseData.toMap(), queryType);
  }

  private static void checkFlight(Object f) {
    if (f == null || !(f instanceof JsonObject)) {
      throw new IllegalArgumentException("Each flight must be a non-null object");
    }
    JsonObject flight = (JsonObject) f;
    if (!flight.containsKey("name") || !flight.containsKey("date") || !flight.containsKey("sourceairport")
        || !flight.containsKey("destinationairport")) {
      throw new IllegalArgumentException("Malformed flight inside flights payload");
    }
  }

  public Result<List<Map<String, Object>>> getFlightsForUser(final String tenant, final String username) {
    UserRepository userRepository = this.userRepository.withScope(tenant);
    BookingRepository bookingRepository = this.bookingRepository.withScope(tenant);
    Optional<User> userDoc;

    try {
      userDoc = userRepository.findById(username);
    } catch (DocumentNotFoundException ex) {
      return Result.of(Collections.emptyList());
    }
    User userData = userDoc.get();
    String[] flights = userData.getFlightIds();
    if (flights == null) {
      return Result.of(Collections.emptyList());
    }

    // The "flights" array contains flight ids. Convert them to actual objects.
    List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    for (String flightId : flights) {
      Optional<Booking> res;
      try {
        res = bookingRepository.findById(flightId);
      } catch (DocumentNotFoundException ex) {
        throw new RuntimeException("Unable to retrieve flight id " + flightId);
      }
      Map<String, Object> flight = res.get().toMap();
      results.add(flight);
    }

    String queryType = String.format("KV get - scoped to %s.user: for %d bookings in document %s", tenant,
        results.size(), username);
    return Result.of(results, queryType);
  }

}
