/*
 * EasyTravel
 * Copyright (C) 2011 Michael Hohl <http://www.hohl.co.at/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.co.hohl.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Helps to decode and encode lists of Strings into one String with comma separated values.
 *
 * @author Michael Hohl
 */
public final class StringHelper {
    /**
     * Decodes an encoded List of Strings.
     *
     * @param encodedList the encoded list of strings.
     * @return the decoded list of strings.
     */
    public static List<String> decode(String encodedList) {
        if (encodedList != null) {
            String[] stringParts = encodedList.split(",");
            List<String> decodedList = new LinkedList<String>();

            for (String part : stringParts) {
                decodedList.add(part.trim());
            }

            return decodedList;
        } else {
            return new LinkedList<String>();
        }
    }

    /**
     * Encodes the passed String to a list of Strings.
     *
     * @param decodedList the decoded list.
     * @return encoded strings.
     */
    public static String encode(List<String> decodedList) {
        if (decodedList != null) {
            StringBuilder builder = new StringBuilder();

            int listSize = decodedList.size();
            for (int index = 0; index < listSize; ++index) {
                builder.append(decodedList.get(index));

                if (index != listSize - 1) {
                    builder.append(",");
                }
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    /**
     * Converts the passed Array to a single String.
     *
     * @param string     the array to convert.
     * @param separator  the separator to set between the entries of the Array.
     * @param startIndex the start index of the array.
     * @return the created string.
     */
    public static String toSingleString(String[] string, String separator, int startIndex) {
        StringBuilder builder = new StringBuilder();

        for (int index = startIndex; index < string.length; ++index) {
            builder.append(string[index]);
            builder.append(separator);
        }

        return builder.toString().trim();
    }

    /**
     * Hidden constructor
     */
    private StringHelper() {
    }
}
