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
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;

/**
 * Booking entity
 *
 * @author Michael Reiche
 */
@Document
@TypeAlias("booking")
// scope is tenant name, always specified by withScope()
// @Collection is specified in repository interface
public class Booking {
	@Id
  public String bookingId;
	public String name;
	public String sourceairport;
	public String destinationairport;
	public String flight;
	public String utc;
	public String airlineid;
  public String date;
	public Integer price;
	public Integer day;

	public Booking() {
	}

	public Booking(String bookingId) {
		this.bookingId = bookingId;
	}

	// try-cb uses Map as common data structure.
	public Map<String,Object> toMap(){
		Map<String,Object> map= new HashMap<>(6);
		map.put("name", name);
		map.put("bookingId",bookingId);
		map.put("sourceairport", sourceairport);
		map.put("destinationairport", destinationairport);
		map.put("airlineid", airlineid);
		map.put( "utc", utc);
		map.put("date", date);
		map.put("flight", flight);
		map.put("day", day);
		return map;
	}

}
