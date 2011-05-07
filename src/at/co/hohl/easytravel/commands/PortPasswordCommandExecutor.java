package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.players.PlayerInformation;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.storage.TravelPortContainer;
import at.co.hohl.easytravel.ports.storage.TravelPortNotFound;
import at.co.hohl.utils.ChatHelper;
import at.co.hohl.utils.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for the command to change the password.
 *
 * @author Michael Hohl
 */
public class PortPasswordCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortPasswordCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
        super(plugin, parent, 1, -1);
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

        TravelPort travelPortToChangePassword;
        String passwordToSet;
        try {
            if (args.length == 3) {
                try {
                    travelPortToChangePassword = travelPorts.search(args[1]);
                    passwordToSet = StringHelper.toSingleString(args, " ", 2);
                } catch (TravelPortNotFound travelPortNotFound) {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
                    return true;
                }
            } else {
                travelPortToChangePassword = playerInformation.getCurrentPort();
                passwordToSet = StringHelper.toSingleString(args, " ", 1);

                if (travelPortToChangePassword == null) {
                    ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
                    return true;
                }
            }
        } catch (NumberFormatException exception) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-use"));

            return true;
        }

        boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(travelPortToChangePassword.getOwner());
        if (isModerator || isOwner) {
            if ("reset".equalsIgnoreCase(passwordToSet))
                travelPortToChangePassword.setPassword(passwordToSet);
            ChatHelper.sendMessage(sender, Messages.get("moderator.success.change-password"));
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> password <text>|RESET";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Sets a password for the TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
