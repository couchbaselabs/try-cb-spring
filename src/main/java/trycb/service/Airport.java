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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import trycb.config.AirportRepository;
import trycb.model.Result;

@Service
@Component
public class Airport {

    private static final Logger LOGGER = LoggerFactory.getLogger(Airport.class);
    private final AirportRepository airportRepository;

    @Autowired
    public Airport(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * Find all airports.
     */
    public Result<List<Map<String, Object>>> findAll( String params) {
        boolean sameCase = (params.equals(params.toUpperCase()) || params.equals(params.toLowerCase()));
        List<trycb.config.Airport> airports = null;
        params = params.toUpperCase();
        String query=null;
        if (params.length() == 3 && sameCase) {
            query = "airportRepository.findByFaa("+params+")";
            try {
                airports = airportRepository.findByFaa(params);
            }catch(Exception e){
                e.printStackTrace();
                throw e;
            }
        } else if (params.length() == 4 && sameCase) {
            query = "airportRepository.findByIcao("+params+")";
            airports = airportRepository.findByIcao(params);
        } else {
            query = "airportRepository.findByAirportnameStartsWith("+params+")";
            airports = airportRepository.findByAirportnameStartsWith(params);
        }

        logQuery(query);

        List<Map<String,Object>> airportList= new ArrayList<>(airports.size());
        for(trycb.config.Airport a:airports){
            airportList.add(a.toMap());
        }
        String querytype = "N1QL query - scoped to inventory: ";
        return Result.of(airportList, querytype, query);
    }

    /**
     * Helper method to log the executing query.
     */
    private static void logQuery(String query) {
        LOGGER.info("Executing Query: {}", query);
    }

}
