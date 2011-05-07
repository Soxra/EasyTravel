package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.easytravel.data.TravelPortNotFound;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import at.co.hohl.utils.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Executor for the command to warp you to a port.
 *
 * @author Michael Hohl
 */
public class PortWarpCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortWarpCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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
        Player player = (Player) sender;
        TravelPortContainer travelPorts = plugin.getTravelPorts();
        String targetName = StringHelper.toSingleString(args, " ", 1);

        try {
            TravelPort target = travelPorts.search(targetName);
            target.getDestination().teleport(player);
        } catch (TravelPortNotFound travelPortNotFound) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> to <id>";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Warps you to the TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.PORT;
    }
}
