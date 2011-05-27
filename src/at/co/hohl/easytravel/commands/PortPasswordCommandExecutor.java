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

package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.permissions.Permission;
import at.co.hohl.utils.ChatHelper;
import at.co.hohl.utils.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for the command to change the password.
 *
 * @author Michael Hohl
 */
public class PortPasswordCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortPasswordCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 0, -1);
    }

    /**
     * Called when the players uses the parentCommand.
     *
     * @param sender        the sender of the command.
     * @param parentCommand the parent command.
     * @param label         the label of the parent command.
     * @param args          the arguments passed to the parentCommand. (Index 0 = the label of the sub parentCommand
     *                      itself!)
     * @return true, if the SubCommandExecutor could handle the parentCommand.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command parentCommand, String label, String[] args) {
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);

        TravelPort travelPortToChangePassword = playerInformation.getCurrentPort();
        String passwordToSet = StringHelper.toSingleString(args, " ", 1);

        if (travelPortToChangePassword == null) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
            return true;
        }

        boolean isModerator = permissionHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(travelPortToChangePassword.getOwner());
        if (isModerator || isOwner) {
            if (passwordToSet.length() < 1) {
                travelPortToChangePassword.setPassword(null);
            } else {
                travelPortToChangePassword.setPassword(passwordToSet);
            }
            ChatHelper.sendMessage(sender, Messages.get("moderator.success.change-password"));
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> password [<text>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Sets a password for the port.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
