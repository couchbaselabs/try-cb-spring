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

package trycb.model;

/**
 * A standardized result format for successful responses, that the frontend
 * application can interpret for all endpoints. Allows to contain user-facing
 * data and an array of context strings, eg. N1QL queries, to be displayed in a
 * "learn more" or console kind of UI element on the front end.
 *
 */
public class Result<T> implements IValue {

    private final T data;
    private final String[] context;

    private Result(T data, String... contexts) {
        this.data = data;
        this.context = contexts;
    }

    public static <T> Result<T> of(T data, String... contexts) {
        return new Result<T>(data, contexts);
    }

    public T getData() {
        return data;
    }

    public String[] getContext() {
        return context;
    }
}
