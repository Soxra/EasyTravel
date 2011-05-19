package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPermissions;
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
 * SubCommandExecutor which implements a command, to rename a existing TravelPort.
 *
 * @author Michael Hohl
 */
public class PortRenameCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortRenameCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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

        if (!playerInformation.isInsideTravelPort()) {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
            return true;
        }

        TravelPort port = playerInformation.getCurrentPort();

        boolean isModerator = permissionsHandler.hasPermission(player, TravelPermissions.MODERATE);
        boolean isOwner = player.getName().equals(port.getOwner());
        if (isModerator || isOwner) {
            port.setName(StringHelper.toSingleString(args, " ", 1));
            ChatHelper.sendMessage(sender, Messages.get("moderator.success.change-name"));
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-own"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> rename <new name>";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Renames your current TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.CREATE;
    }
}
