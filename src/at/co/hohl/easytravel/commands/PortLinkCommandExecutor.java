package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.*;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for the port link command.
 *
 * @author Michael Hohl
 */
public class PortLinkCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortLinkCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 1, 2);
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
        TravelPortContainer travelPorts = plugin.getTravelPorts();

        try {
            TravelPort port1 = travelPorts.search(args[1]);
            TravelPort port2;
            if (args.length == 3) {
                port2 = travelPorts.search(args[2]);
            } else {
                port2 = playerInformation.getCurrentPort();

                if (port2 == null) {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
                    return true;
                }
            }

            boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
            boolean isOwner = player.getName().equals(port1.getOwner()) && player.getName().equals(port2.getOwner());
            if (isModerator || isOwner) {
                travelPorts.link(port1, port2);
                ChatHelper.sendMessage(sender, Messages.get("moderator.success.linked"));
            } else {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
            }
        } catch (TravelPortNotFound exception) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
        } catch (InvalidLinkException e) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.already-linked"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> link <id> [<id>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Links two TravelPorts.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
