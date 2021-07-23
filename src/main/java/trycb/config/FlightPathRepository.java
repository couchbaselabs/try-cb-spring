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

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.DynamicProxyable;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.couchbase.repository.Scope;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.query.QueryScanConsistency;

/**
 * This is an odd repository in that it only uses @Query. It never uses any built-in (findById() etc) or derived (
 * findByAirportName() etc) queries.
 */
@Repository("flightPathRepository")
@Scope("inventory")
// this repository uses airport, route and airline collections that are hard-coded into the @query. It never uses
// @Collection
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FlightPathRepository
    extends CouchbaseRepository<FlightPath, String>, DynamicProxyable<FlightPathRepository> {

  // Similar to the query in try-cb-java, except this query also includes the joins to translate the
  // airport names to faa (to join on r.sourceairport and r.destinationairport)
  // __id and __cas must be projected even if they are not used
  @Query("SELECT meta(r).id as __id, meta(r).cas as __cas, a.name, a.id airlineid, s.flight, s.day, s.utc, "
      + "r.sourceairport, r.destinationairport, r.equipment " + "FROM " + "airport src "
      + "INNER JOIN route r on r.sourceairport = src.faa " + "INNER JOIN airline a on r.airlineid = meta(a).id "
      + "UNNEST r.schedule AS s " + "INNER JOIN airport dst on r.destinationairport = dst.faa "
      + "where src.airportname=$1 and dst.airportname=$2 and s.day=$3")
  List<FlightPath> findFlights(String sourceAirport, String destinationAirport, Number day);
}
