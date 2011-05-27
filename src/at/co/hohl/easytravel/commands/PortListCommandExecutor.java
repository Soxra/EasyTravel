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

import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.Destination;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.permissions.Permission;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

/**
 * Command executor for showing a list of TravelPorts.
 *
 * @author Michael Hohl
 */
public class PortListCommandExecutor extends SubCommandExecutor {
    /** Numbers of entries displayed per page. */
    private static final int ENTRIES_PER_PAGE = 5;

    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortListCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 0, 1);
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
        TravelPortContainer travelPorts = plugin.getTravelPorts();

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.valueOf(args[1]);
            } catch (NumberFormatException exception) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-page"));
            }
        }

        Collection<TravelPort> ports = travelPorts.getAll();
        int numPages = ports.size() / ENTRIES_PER_PAGE + 1;
        int startEntry = (page - 1) * ENTRIES_PER_PAGE;
        int endEntry = Math.min(startEntry + ENTRIES_PER_PAGE, ports.size());

        sender.sendMessage(ChatColor.GREEN + String.format("= = = Travel Ports [Page %d/%d] = = =", page, numPages));
        int current = 0;
        for (TravelPort port : ports) {
            if (current >= startEntry && current < endEntry) {
                Destination destination = port.getDestination();
                sender.sendMessage(
                        String.format("[%s] %s (%s) - %.1f, %.1f, %.1f", port.getId(), port.getName(), port.getOwner(),
                                destination.getX(), destination.getY(), destination.getZ()));
            }
            ++current;
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> list [<page>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Lists available TravelPorts.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.LIST;
    }
}
