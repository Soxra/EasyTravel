package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.easytravel.ports.TravelPortNotFound;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for the command to set the price.
 *
 * @author Michael Hohl
 */
public class PortPriceCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortPriceCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 1, 2);
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

        TravelPort travelPortToChangePrice;
        double priceToSet;
        try {
            if (args.length == 3) {
                try {
                    travelPortToChangePrice = travelPorts.search(args[1]);
                    priceToSet = Double.parseDouble(args[2]);
                } catch (TravelPortNotFound travelPortNotFound) {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
                    return true;
                }
            } else {
                travelPortToChangePrice = playerInformation.getCurrentPort();
                priceToSet = Double.parseDouble(args[1]);

                if (travelPortToChangePrice == null) {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
                    return true;
                }
            }
        } catch (NumberFormatException exception) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-use"));

            return true;
        }

        boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(travelPortToChangePrice.getOwner());
        if (isModerator || isOwner) {
            travelPortToChangePrice.setPrice(priceToSet);
            ChatHelper.sendMessage(sender, Messages.get("moderator.success.change-price"));
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> price [<id>] <price>";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Sets the price for a TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
