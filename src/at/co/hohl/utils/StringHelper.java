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
        String[] stringParts = encodedList.split(",");
        List<String> decodedList = new LinkedList<String>();

        for (String part : stringParts) {
            decodedList.add(part.trim());
        }

        return decodedList;
    }

    /**
     * Encodes the passed String to a list of Strings.
     *
     * @param decodedList the decoded list.
     * @return encoded strings.
     */
    public static String encode(List<String> decodedList) {
        StringBuilder builder = new StringBuilder();

        int listSize = decodedList.size();
        for (int index = 0; index < listSize; ++index) {
            builder.append(decodedList.get(index));

            if (index != listSize - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
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

    /** Hidden constructor */
    private StringHelper() {
    }
}
