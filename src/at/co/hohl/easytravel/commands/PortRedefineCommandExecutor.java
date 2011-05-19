package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.Area;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.easytravel.ports.TravelPortNotFound;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to change the area which is used by the TravelPort.
 *
 * @author Michael Hohl
 */
public class PortRedefineCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortRedefineCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);
        TravelPortContainer travelPorts = plugin.getTravelPorts();

        TravelPort travelPortToRedefine;
        if (args.length == 2) {
            try {
                travelPortToRedefine = travelPorts.search(args[1]);
            } catch (TravelPortNotFound travelPortNotFound) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
                return true;
            }
        } else {
            travelPortToRedefine = playerInformation.getCurrentPort();

            if (travelPortToRedefine == null) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
                return true;
            }
        }

        boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(travelPortToRedefine.getOwner());
        if (isModerator || isOwner) {
            Area selectedArea = plugin.getSelectedArea(player);

            if (selectedArea != null) {
                travelPortToRedefine.setArea(plugin.getSelectedArea(player));
                ChatHelper.sendMessage(sender, Messages.get("moderator.success.redefine"));
            } else {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.select-area"));
            }

        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> redefine [<id>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Redefines the area of a TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
