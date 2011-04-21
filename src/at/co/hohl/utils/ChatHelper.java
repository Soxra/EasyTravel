/*
 * Copyright (c) 2011, Michael Hohl
 *
 * All rights reserved.
 */

package at.co.hohl.utils;

import at.co.hohl.easytravel.LocalizedStrings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Helper class for using color codes.
 *
 * @author Michael Hohl
 */
public class ChatHelper {
    /**
     * Sends the passed player the message.
     *
     * @param player  the player.
     * @param message the message to send.
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(replaceColorCodes(String.format(LocalizedStrings.MESSAGE_FORMAT, message)));
    }

    /**
     * Replace the &x with the color code of color x.
     *
     * @param message the string to format
     * @return the string ready to output.
     */
    public static String replaceColorCodes(String message) {
        for (ChatColor color : ChatColor.values()) {
            message = message.replaceAll(String.format("&%x", color.getCode()), color.toString());
        }

        return message;
    }
}
