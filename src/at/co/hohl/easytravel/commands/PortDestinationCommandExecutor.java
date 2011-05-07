package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.Destination;
import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Adds a command to change the destination of a TravelPort.
 *
 * @author Michael Hohl
 */
public class PortDestinationCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortDestinationCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 0, 0);
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
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);

        TravelPort currentPort = playerInformation.getCurrentPort();

        boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(currentPort.getOwner());
        if (isModerator || isOwner) {
            currentPort.setDestination(new Destination(player.getLocation()));
            ChatHelper.sendMessage(sender, Messages.get("moderator.success.change-destination"));
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> destination";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Sets the destination of a TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
