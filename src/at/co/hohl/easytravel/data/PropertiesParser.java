package at.co.hohl.easytravel.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser used for parsing information out of a description string. A valid information string should look like:
 * CuboidArea{a=1, b=test, xyz=true}.
 *
 * @author Michael Hohl
 */
public class PropertiesParser {
    /** Contains the type of the parsed data. (Like in the example it is "CuboidArea") */
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
                    lineToParse.substring(type.length() + 1, lineToParse.length() - (type.length() + 1)).split(",");

            for (String propertyString : propertyStrings) {
                String[] splittedLine = propertyString.split("=");

                if (splittedLine.length == 2) {
                    properties.put(splittedLine[0], splittedLine[1]);
                } else {
                    throw new SyntaxException("Invalid formatted properties!");
                }
            }
        } else {
            throw new SyntaxException("Invalid formatted string passed!");
        }
    }

    /** @return type of the parsed data. */
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
