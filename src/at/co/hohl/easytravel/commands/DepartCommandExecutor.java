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
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.utils.ChatHelper;
import at.co.hohl.utils.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandExecutor for the /depart command.
 *
 * @author Michael Hohl
 */
public class DepartCommandExecutor implements CommandExecutor {
    /** The plugin, which holds this instance. */
    private final TravelPlugin plugin;

    /**
     * Creates a new CommandExecutor for the /depart-Command.
     *
     * @param plugin the plugin which holds the instance.
     */
    public DepartCommandExecutor(TravelPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the /depart-Command gets called.
     *
     * @param sender  the sender of the command.
     * @param command the command itself.
     * @param label   the label used for calling the command.
     * @param args    the arguments passed to the command.
     * @return true, if the Executor could handle the command.
     */
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Is sender a players?
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only use the /depart command as players! Doesn't make sense anyway for you ;)");
            return true;
        }
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);

        // Is players inside TravelPort?
        if (!playerInformation.isInsideTravelPort()) {
            ChatHelper.sendMessage(player, Messages.get("problem.not-inside"));
            return true;
        }

        // Passed any password?
        if (args.length > 0) {
            playerInformation.addPassword(StringHelper.toSingleString(args, " ", 0));
        }

        // Check if port has a target.
        TravelPort port = playerInformation.getCurrentPort();
        if (port.getTargetId() != null) {
            port.getDeparture().onDepartCommand(player, playerInformation);
        } else {
            ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
        }

        return true;
    }
}
