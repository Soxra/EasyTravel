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

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.permissions.Permission;
import at.co.hohl.permissions.PermissionHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SubCommandExecutor for the port help command.
 *
 * @author Michael Hohl
 */
public class PortHelpCommandExecutor extends SubCommandExecutor {
    /**
     * Number of help entries per page.
     */
    private static final int ENTRIES_PER_PAGE = 7;

    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortHelpCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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
        PermissionHandler permissionsHandler = plugin.getPermissionsHandler();

        Map<String, SubCommandExecutor> commandMap = ((PortCommandExecutor) this.parent).getSubCommands();
        List<String> commands = new LinkedList<String>(commandMap.keySet());
        Collections.sort(commands);

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(ChatColor.RED + "Invalid Page!");
            }
        }

        sender.sendMessage(ChatColor.GREEN + String.format("= = = %s [Page %d/%d] = = =",
            plugin.getDescription().getName(), page, (commands.size() / ENTRIES_PER_PAGE) + 1));

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(page * ENTRIES_PER_PAGE, commands.size());
        for (int index = start; index < end; ++index) {
            SubCommandExecutor commandExecutor = commandMap.get(commands.get(index));
            Permission permission = commandExecutor.getRequiredPermission();
            if (permission == null || permissionsHandler.hasPermission(sender, permission)) {
                String helpLine = String.format("%s%s%s - %s", ChatColor.GRAY,
                    commandExecutor.getUsage().replace("<command>", label), ChatColor.WHITE,
                    commandExecutor.getDescription());
                sender.sendMessage(helpLine);
            }
        }

        return true;
    }

    /**
     * @return string which describes the valid usage.
     */
    @Override
    public String getUsage() {
        return "/<command> help [<page>]";
    }

    /**
     * @return description of the command.
     */
    @Override
    public String getDescription() {
        return "Shows this help.";
    }

    /**
     * @return required permission for executing this command.
     */
    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}
