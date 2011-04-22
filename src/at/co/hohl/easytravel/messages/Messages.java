package at.co.hohl.easytravel.messages;

import org.bukkit.util.config.Configuration;

/**
 * Static container for the localized strings. Sadly, Bukkit doesn't support ResourceBundle inside the data directory.
 *
 * @author Michael Hohl
 */
public final class Messages {
    // TRAVEL
    public static String portCreatedSuccessfully = "&2Port created successfully!";

    public static String linkedSuccessfully = "&2Ports linked successfully!";

    public static String unlinkedSuccessfully = "&2Port unlinked successfully!";

    public static String removedSuccessfully = "&2Port removed successfully!";

    public static String needToPassNameForPort = "&cYou need to pass a name, when creating a TravelPort!";

    public static String needToSelectArea = "&cYou need to select an area with you WorldEdit wand!";

    public static String notInsideTravelPort = "&cYou are not inside a TravelPort!";

    public static String portAlreadyLinked = "&cAt least one of the port is already linked! Do unlink before!";

    public static String portNotLinked = "&cPort isn't linked to another!";

    public static String invalidUseOfCommand =
            "&cInvalid use of this command! Use /travel help for information how to use it.";

    public static String invalidPortId = "&cPassed ID isn't a valid port id!";

    /** Configuration which stores the messages. */
    private static Configuration configuration;

    /**
     * Loads the messages out of the passed Configuration.
     *
     * @param configuration the configuration used for loading.
     */
    public static void load(Configuration configuration) {
        Messages.configuration = configuration;
    }

    /**
     * Gets the localized string with the passed message id.
     *
     * @param messageId the id of the message to get.
     */
    public static String get(String messageId) {
        String missTranslation = String.format("Miss translation for '%s'", messageId);

        if (configuration != null) {
            return configuration.getString(messageId, missTranslation);
        } else {
            return missTranslation;
        }
    }


    /** Hidden constructor. */
    private Messages() {
    }
}
