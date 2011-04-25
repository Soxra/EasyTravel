package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.easytravel.data.TravelPortNotFound;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        TravelPortContainer travelPorts = plugin.getTravelPorts();

        int page = 0;
        if (args.length == 2) {
            try {
                page = Integer.valueOf(args[1]);
            } catch (NumberFormatException exception) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-page"));
            }
        }

        int numberOfEntries = travelPorts.size();
        int startEntry = page * ENTRIES_PER_PAGE;
        int endEntry = Math.min(startEntry + ENTRIES_PER_PAGE, travelPorts.size()) - 1;

        sender.sendMessage(ChatColor.GREEN +
                String.format("= = = Travel Ports [Page %d/%d] = = =", page, numberOfEntries / ENTRIES_PER_PAGE));
        for (int current = startEntry; current <= endEntry; ++current) {
            try {
                TravelPort port = travelPorts.get(Integer.valueOf(current));
                sender.sendMessage(String.format("[%s] %s (%s)", port.getId(), port.getName(), port.getOwner()));
            } catch (TravelPortNotFound travelPortNotFound) {
                plugin.getLogger().fine("Leak in IDs of TravelPorts detected!");
                sender.sendMessage(String.format("[%d] ---", current));
            }
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
