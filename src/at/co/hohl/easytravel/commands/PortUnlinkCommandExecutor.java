package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.easytravel.data.TravelPortNotFound;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for port unlink command.
 *
 * @author Michael Hohl
 */
public class PortUnlinkCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortUnlinkCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);
        TravelPortContainer travelPorts = plugin.getTravelPorts();

        TravelPort travelPortToRemove;
        if (args.length == 2) {
            try {
                travelPortToRemove = travelPorts.search(args[1]);
            } catch (TravelPortNotFound travelPortNotFound) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.invalid-id"));
                return true;
            }
        } else {
            travelPortToRemove = playerInformation.getCurrentPort();

            if (travelPortToRemove == null) {
                ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
                return true;
            }
        }
        travelPorts.remove(travelPortToRemove);
        ChatHelper.sendMessage(sender, Messages.get("moderator.success.unlinked"));

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> unlink [<id>]";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Unlink the TravelPort";
    }
}
