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

import com.sun.istack.internal.Nullable;

/**
 * Implementation of a parser for a CSV line.
 *
 * @author Michael Hohl
 */
public class CsvLineParser {
    /**
     * Parsed columns.
     */
    private final String[] parsedColumns;

    /**
     * Creates a new CsvLineParser, and parses the passed line.
     *
     * @param csvLine the line to parse.
     */
    public CsvLineParser(String csvLine) {
        parsedColumns = csvLine.split(";");
    }

    /**
     * Returns the string at the passed index.
     *
     * @param index the index of the string.
     * @return the parsed string or null.
     */
    @Nullable
    public String getString(int index) {
        if (index < parsedColumns.length) {
            String column = parsedColumns[index];
            if ("null".equals(column)) {
                return null;
            } else {
                return column.replace("&#59", ";");
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the value at the passed index.
     *
     * @param index the index of the value.
     * @return the parsed value.
     */
    public double getDouble(int index) {
        if (index < parsedColumns.length) {
            return Double.parseDouble(parsedColumns[index]);
        } else {
            return 0;
        }
    }

    /**
     * Returns the value at the passed index.
     *
     * @param index the index of the value.
     * @return the parsed value.
     */
    public int getInt(int index) {
        if (index < parsedColumns.length) {
            return Integer.parseInt(parsedColumns[index]);
        } else {
            return 0;
        }
    }

    /**
     * Returns the integer value at the passed index.
     *
     * @param index the index of the value.
     * @return the parsed value.
     */
    public Integer getInteger(int index) {
        if (index < parsedColumns.length && !"null".equals(parsedColumns[index])) {
            return Integer.valueOf(parsedColumns[index]);
        } else {
            return null;
        }
    }

    /**
     * @return number of parsed columns.
     */
    public int columns() {
        return parsedColumns.length;
    }
}
