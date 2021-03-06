/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.xcontent;

import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.XContentParser.Token;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * A set of static methods to get {@link Token} from {@link XContentParser}
 * while checking for their types and throw {@link ParsingException} if needed.
 */
public final class XContentParserUtils {

    private XContentParserUtils() {
    }

    /**
     * Makes sure that current token is of type {@link XContentParser.Token#FIELD_NAME}
     *
     * @return the token
     * @throws ParsingException if the token is not of type {@link XContentParser.Token#FIELD_NAME}
     */
    public static Token ensureFieldName(Token token, Supplier<XContentLocation> location) throws IOException {
        return ensureType(Token.FIELD_NAME, token, location);
    }

    /**
     * Makes sure that current token is of type {@link XContentParser.Token#FIELD_NAME} and the the field name is equal to the provided one
     *
     * @return the token
     * @throws ParsingException if the token is not of type {@link XContentParser.Token#FIELD_NAME} or is not equal to the given
     *                          field name
     */
    public static Token ensureFieldName(XContentParser parser, Token token, String fieldName) throws IOException {
        Token t = ensureType(Token.FIELD_NAME, token, parser::getTokenLocation);

        String current = parser.currentName() != null ? parser.currentName() : "<null>";
        if (current.equals(fieldName) == false) {
            String message = "Failed to parse object: expecting field with name [%s] but found [%s]";
            throw new ParsingException(parser.getTokenLocation(), String.format(Locale.ROOT, message, fieldName, current));
        }
        return t;
    }

    /**
     * @throws ParsingException with a "unknown field found" reason
     */
    public static void throwUnknownField(String field, XContentLocation location) {
        String message = "Failed to parse object: unknown field [%s] found";
        throw new ParsingException(location, String.format(Locale.ROOT, message, field));
    }

    /**
     * Makes sure that current token is of the expected type
     *
     * @return the token
     * @throws ParsingException if the token is not equal to the expected type
     */
    private static Token ensureType(Token expected, Token current, Supplier<XContentLocation> location) {
        if (current != expected) {
            String message = "Failed to parse object: expecting token of type [%s] but found [%s]";
            throw new ParsingException(location.get(), String.format(Locale.ROOT, message, expected, current));
        }
        return current;
    }
}
