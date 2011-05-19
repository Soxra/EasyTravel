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

package at.co.hohl.utils.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser used for parsing information out of a description string. A valid information string should look like:
 * CuboidArea{a=1, b=test, xyz=true}.
 *
 * @author Michael Hohl
 */
public class PropertiesParser {
    /** Contains the type of the parsed ports. (Like in the example it is "CuboidArea") */
    private final String type;

    /** Parsed properties. */
    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * Creates a new parser.
     *
     * @param lineToParse the string to parse.
     * @throws SyntaxException thrown when the passed line isn't formatted valid.
     */
    public PropertiesParser(String lineToParse) throws SyntaxException {
        if (lineToParse.matches("[A-Za-z0-9]+\\{.*\\}")) {
            type = lineToParse.split("\\{")[0];
            String[] propertyStrings =
                    lineToParse.substring(type.length() + 1, lineToParse.length() - 1).split(",");

            for (String propertyString : propertyStrings) {
                String[] splittedLine = propertyString.split("=");

                if (splittedLine.length == 2) {
                    properties.put(splittedLine[0].trim(), splittedLine[1].trim());
                } else {
                    throw new SyntaxException("Invalid formatted properties!");
                }
            }
        } else {
            throw new SyntaxException("Invalid formatted string passed!");
        }
    }

    /** @return type of the parsed ports. */
    public String getType() {
        return type;
    }

    /**
     * Returns the value of the passed key.
     *
     * @param key the key to look up.
     * @return the value.
     */
    public String getString(String key) {
        return properties.get(key);
    }

    /**
     * Returns the value of the passed key.
     *
     * @param key the key to look up.
     * @return the value.
     */
    public double getDouble(String key) {
        String property = properties.get(key);

        if (property != null) {
            return Double.parseDouble(property);
        } else {
            return 0;
        }
    }

    /**
     * Returns the value of the passed key.
     *
     * @param key the key to look up.
     * @return the value.
     */
    public float getFloat(String key) {
        String property = properties.get(key);

        if (property != null) {
            return Float.parseFloat(property);
        } else {
            return 0;
        }
    }

    /**
     * Returns the value of the passed key.
     *
     * @param key the key to look up.
     * @return the value.
     */
    public int getInt(String key) {
        String property = properties.get(key);

        if (property != null) {
            return Integer.parseInt(property);
        } else {
            return 0;
        }
    }
}
