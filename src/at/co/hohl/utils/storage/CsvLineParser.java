package at.co.hohl.utils.storage;

import com.sun.istack.internal.Nullable;

/**
 * Implementation of a parser for a CSV line.
 *
 * @author Michael Hohl
 */
public class CsvLineParser {
    /** Parsed columns. */
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
    public double getDouble(int index) throws NumberFormatException {
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
    public int getInt(int index) throws NumberFormatException {
        if (index < parsedColumns.length) {
            return Integer.parseInt(parsedColumns[index]);
        } else {
            return 0;
        }
    }

    /** @return number of parsed columns. */
    public int columns() {
        return parsedColumns.length;
    }
}
