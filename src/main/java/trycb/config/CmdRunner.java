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

package trycb.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Can be used for sanity test on startup. Components of the type CommandLineRunner are called right after the
 * application start up. So the method run() is called as soon as the application starts.
 */
@Component
public class CmdRunner implements CommandLineRunner {

  @Autowired private AirportRepository airportRepository;

  @Override
  public void run(String... strings) throws Exception {
    AirportRepository repo = airportRepository.withScope("inventory").withCollection("airport");
    Optional<Airport> airport = airportRepository.findById("airport_3469"); // SFO
    System.out.println("got SFO: " + airport.get());
    try {
      List<Airport> airports = repo.findByAirportnameStartsWith("SAN ");
      System.out.println("airports that start with San :" + airports);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
