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
import org.springframework.data.couchbase.repository.Collection;

/**
 * User entity
 *
 * @author Michael Reiche
 */
@Document
@TypeAlias("user")
// scope is tenant name
// collection is specified in repository interface
public class User {
	@Id
  public String name;
	public String type;
  public String password;
	//Address[] addresses;
	String driving_licence;
	String passport;
	String preferred_email;
	String preferred_phone;
	String preferred_airline;
	String preferred_airport;
	//CreditCard[] credit_cards;
	long created;
	long updated;
	String[] flightIds;

	public User() {
	}

	public User(String username, String password) {
		this.name = username;
		this.password = password;
	}

	// try-cb uses Map as common data structure.
	public Map<String,Object> toMap(){
		Map<String,Object> map= new HashMap<>(6);
		map.put("name",name);
		map.put("password", password);
		map.put("driving_licence", driving_licence);
		map.put("passport", passport);
		map.put("preferred_email", preferred_email);
		map.put("preferred_phone", preferred_phone);
		map.put("preferred_airline", preferred_airline);
		map.put("preferred_airport", preferred_airport);
		map.put( "flights", flightIds);
		return map;
	}

	public String[] getFlightIds(){
		return flightIds;
	}

	public void setFlightIds(String[] flightIds){
		this.flightIds = flightIds;
	}
}
