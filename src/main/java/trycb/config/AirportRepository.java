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

import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.DynamicProxyable;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.couchbase.repository.Scope;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.query.QueryScanConsistency;

/**
 * Airport repository<br>
 * The DynamicProxyable interface exposes airportRepository.withScope(scope), withCollection() and withOptions() It's
 * necessary on the repository object itself because the withScope() etc methods need to return an object of type
 * AirportRepository so that one can code... airportRepository = airportRepository.withScope(scopeName) without having
 * to cast the result.
 *
 * @author Michael Reiche
 */
@Repository("airportRepository")
@Scope("inventory")
@Collection("airport")
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface AirportRepository extends CouchbaseRepository<Airport, String>, DynamicProxyable<AirportRepository> {

  @Override
  List<Airport> findAll();

  // n1ql.selectEntity will be expanded into the correct SELECT ...
  // n1ql.filter will be expanded into  ( class="airport" ).  "class" is specified by getType() in Database method
  // and "airport" is from @TypeAlias in Airport.java
  @Query("#{#n1ql.selectEntity} where #{#n1ql.filter} AND UPPER(airportname) LIKE ($1||'%')")
  List<Airport> findByAirportnameStartsWith(String name);

  List<Airport> findByFaa(String faa);

  List<Airport> findByIcao(String iata);

}
