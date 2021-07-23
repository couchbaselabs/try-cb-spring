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

import com.couchbase.client.core.deps.io.netty.util.CharsetUtil;
import com.couchbase.client.java.json.JsonObject;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

@Service
public class TokenService {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.enabled}")
    private boolean useJwt;

    /**
     * @throws IllegalStateException when the Authorization header couldn't be verified or didn't match the expected
     * username.
     */
    public void verifyAuthenticationHeader(String authorization, String expectedUsername) {
        String token = authorization.replaceFirst("Bearer ", "");
        String tokenName;
        if (useJwt) {
            tokenName = verifyJwt(token);
        } else {
            tokenName = verifySimple(token);
        }
        if (!expectedUsername.equals(tokenName)) {
            throw new IllegalStateException("Token and username don't match");
        }
    }

    private String verifyJwt(String token) {
        try {
            String username = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("user", String.class);
            return username;
        } catch (JwtException e) {
            throw new IllegalStateException("Could not verify JWT token", e);
        }
    }

    private String verifySimple(String token) {
        try {
            return new String(Base64Utils.decodeFromString(token));
        } catch (Exception e) {
            throw new IllegalStateException("Could not verify simple token", e);
        }
    }

    public String buildToken(String username) {
        if (useJwt) {
            return buildJwtToken(username);
        } else {
            return buildSimpleToken(username);
        }
    }

    private String buildJwtToken(String username) {
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS512, secret)
                .setPayload(JsonObject.create()
                    .put("user", username)
                    .toString())
                .compact();
        return token;
    }

    private String buildSimpleToken(String username) {
        return Base64Utils.encodeToString(username.getBytes(CharsetUtil.UTF_8));
    }
}
