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

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Cluster;

import trycb.config.FlightPathRepository;
import trycb.model.Result;

@Service
public class FlightPath {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlightPath.class);

  private final FlightPathRepository flightPathRepository;

  @Autowired
  public FlightPath(FlightPathRepository flightPathRepository) {
    this.flightPathRepository = flightPathRepository;
  }

  /**
   * Find all flight paths.
   */
  public Result<List<Map<String, Object>>> findAll(String from,
      String to, Calendar leave) {
    String query = "flightPathRepository.findFlights(" + from + ", " + to + ", " + leave.get(Calendar.DAY_OF_WEEK)
        + ")";
    logQuery(query);

    List<trycb.config.FlightPath> flightPaths = flightPathRepository.findFlights(from, to,
        leave.get(Calendar.DAY_OF_WEEK));
    Random rand = new Random();
    List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
    for (trycb.config.FlightPath f : flightPaths) {
      Map<String, Object> row = f.toMap();
      row.put("flighttime", rand.nextInt(8000));
      row.put("price", Math.ceil((Integer) row.get("flighttime") / 8 * 100) / 100);
      row.put("date", leave.getTime());
      data.add(row);
    }

    String querytype = "N1QL query - scoped to inventory: ";
    return Result.of(data, querytype, query);
  }

  /**
   * Helper method to log the executing query.
   */
  private static void logQuery(String query) {
    LOGGER.info("Executing Query: {}", query);
  }
}
