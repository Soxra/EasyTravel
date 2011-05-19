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

import at.co.hohl.Permissions.Permission;
import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** Abstract class for all subclasses. */
public abstract class SubCommandExecutor implements CommandExecutor {
    /** Plugin which holds the instance of the subcommand. */
    protected TravelPlugin plugin;

    /** Parent CommandExecutor which holds this SubCommandExecutor. */
    protected CommandExecutor parent;

    /** The permissions handler of the plugin. */
    protected PermissionsHandler permissionsHandler;

    /** Number of arguments which are allowed. */
    private int minimumNumberOfArguments, maximumNumberOfArguments;

    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public SubCommandExecutor(TravelPlugin plugin, CommandExecutor parent, int minimumNumberOfArguments,
                              int maximumNumberOfArguments) {
        this.plugin = plugin;
        this.parent = parent;
        this.minimumNumberOfArguments = minimumNumberOfArguments;
        this.maximumNumberOfArguments = maximumNumberOfArguments;
        this.permissionsHandler = plugin.getPermissionsHandler();
    }

    /**
     * Returns true, if the number of passed arguments is valid.
     *
     * @param numberOfArguments the number of arguments passed.
     * @return true, if the passed number of arguments valid is.
     */
    public boolean isValidNumberOfArguments(int numberOfArguments) {
        return minimumNumberOfArguments <= numberOfArguments &&
                (numberOfArguments <= maximumNumberOfArguments || maximumNumberOfArguments == -1);
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
    public abstract boolean onCommand(CommandSender sender, Command parentCommand, String label, String[] args);

    /** @return string which describes the valid usage. */
    public abstract String getUsage();

    /** @return description of the command. */
    public abstract String getDescription();

    /** @return required permission for executing this command. */
    public abstract Permission getRequiredPermission();
}
