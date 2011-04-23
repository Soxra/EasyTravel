/*
 * Copyright (c) 2011, Michael Hohl
 *
 * All rights reserved.
 */

package at.co.hohl.utils;

import at.co.hohl.easytravel.messages.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Helper class for using color codes.
 *
 * @author Michael Hohl
 */
public final class ChatHelper {
    /**
     * Sends the passed receiver the message.
     *
     * @param receiver the receiver.
     * @param message  the message to send.
     */
    public static void sendMessage(CommandSender receiver, String message) {
        receiver.sendMessage(replaceColorCodes(String.format(Messages.get("format"), message)));
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

    /** Hidden constructor. */
    private ChatHelper() {
    }
}
