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

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.couchbase.core.mapping.Document;

/**
 * Airport entity
 *
 * @author Michael Reiche
 */
@Document
@TypeAlias("airport")
// @Scope and @Collection could be here; they are specified in repository interface
public class Airport {
  @Id String id;
  String airportname;
  String icao;
  String faa;
  String city;
  String country;

  @PersistenceConstructor
  public Airport(String id, String airportname, String faa, String city, String country, String icao) {
    this.id = id;
    this.airportname = airportname;
    this.faa = faa;
    this.city = city;
    this.country = country;
    this.icao = icao;
  }

  // try-cb uses Map as common data structure.
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>(6);
    map.put("id", id);
    map.put("airportname", airportname);
    map.put("faa", faa);
    map.put("city", city);
    map.put("country", country);
    map.put("icao", icao);
    return map;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append(" id : ").append(id);
    sb.append(", airportname :").append(airportname);
    sb.append(", faa :").append(faa);
    sb.append(", city :").append(city);
    sb.append(", country :").append(country);
    sb.append(", icao: ").append(icao);
    sb.append(" }");
    return sb.toString();
  }
}
