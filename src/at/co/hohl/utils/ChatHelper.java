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
            message = message.replace(String.format("&%s", color.getChar()), color.toString());
        }

        return message;
    }

    /**
     * Hidden constructor.
     */
    private ChatHelper() {
    }
}
