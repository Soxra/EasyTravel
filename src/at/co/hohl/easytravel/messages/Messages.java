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

package at.co.hohl.easytravel.messages;

import org.bukkit.util.config.Configuration;

import java.util.Map;

/**
 * Static container for the localized strings. Sadly, Bukkit doesn't support ResourceBundle inside the ports directory.
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
        configuration.load();
    }

    /**
     * Gets the localized string with the passed message id.
     *
     * @param messageId the id of the message to get.
     * @return the localized string.
     */
    public static String get(String messageId) {
        String missTranslation;
        if (messageId != "format") {
            missTranslation = String.format("Miss translation for '%s'", messageId);
        } else {
            missTranslation = "%s";
        }

        if (configuration != null) {
            return configuration.getString(messageId, missTranslation);
        } else {
            return missTranslation;
        }
    }

    /**
     * Like get(String), but replaces variables like <amount> with it's values.
     *
     * @param messageId the id of the message to get.
     * @param variables the variables to replace.
     * @return the localized string.
     */
    public static String get(String messageId, Map<String, String> variables) {
        String missTranslation;
        if (messageId != "format") {
            missTranslation = String.format("Miss translation for '%s'", messageId);
        } else {
            missTranslation = "%s";
        }

        if (configuration != null) {
            String localizedString = configuration.getString(messageId, missTranslation);

            for (String variable : variables.keySet()) {
                localizedString = localizedString.replaceAll("<" + variable + ">", variables.get(variable));
            }

            return localizedString;
        } else {
            return missTranslation;
        }
    }


    /** Hidden constructor. */
    private Messages() {
    }
}
