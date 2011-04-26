package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

/**
 * Command Executor for searching TravelPorts.
 *
 * @author Michael Hohl
 */
public class PortSearchCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortSearchCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 1, -1);
    }

    /**
     * Called when the player uses the parentCommand.
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
        String keyword = StringHelper.toSingleString(args, " ", 1);

        sender.sendMessage(ChatColor.GREEN + String.format("= = = Travel Ports [Search: %s] = = =", keyword));

        Collection<TravelPort> result = plugin.getTravelPorts().searchAll(keyword);
        for (TravelPort port : result) {
            Location destination = port.getDestination();
            sender.sendMessage(
                    String.format("[%s] %s (%s) - %.1f, %.1f, %.1f", port.getId(), port.getName(), port.getOwner(),
                            destination.getX(), destination.getY(), destination.getZ()));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> search <keyword>";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Searches TravelPorts.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.LIST;
    }
}
