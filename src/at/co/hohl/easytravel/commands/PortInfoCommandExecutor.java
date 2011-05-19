package at.co.hohl.easytravel.commands;

import at.co.hohl.Permissions.Permission;
import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.Destination;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.utils.ChatHelper;
import at.co.hohl.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * SubCommandExecutor for the port info command.
 *
 * @author Michael Hohl
 */
public class PortInfoCommandExecutor extends SubCommandExecutor {
    /**
     * Creates a new SubCommandExecutor.
     *
     * @param plugin the plugin which holds this command.
     * @param parent the parent of this CommandExecutor.
     */
    public PortInfoCommandExecutor(TravelPlugin plugin, CommandExecutor parent) {
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
        PlayerInformation playerInformation = plugin.getPlayerInformation((Player) sender);
        TravelPortContainer ports = plugin.getTravelPorts();
        TravelPort currentPort = playerInformation.getCurrentPort();

        if (currentPort != null) {
            sender.sendMessage("= = = About Current TravelPort = = =");
            sender.sendMessage(String.format("ID: %s%s", ChatColor.GRAY, currentPort.getId()));
            sender.sendMessage(String.format("Name: %s%s", ChatColor.GRAY, currentPort.getName()));
            sender.sendMessage(String.format("Owner: %s%s", ChatColor.GRAY, currentPort.getOwner()));
            sender.sendMessage(String.format("Target ID: %s%d", ChatColor.GRAY, currentPort.getTargetId()));
            try {
                sender.sendMessage(
                        String.format("Target Name: %s%s", ChatColor.GRAY, ports.get(currentPort.getTargetId())));
            } catch (Exception exception) {
                // Ignore!
            }
            Destination destination = currentPort.getDestination();
            sender.sendMessage(String.format("Location: %.1f, %.1f, %.1f", destination.getX(), destination.getY(),
                    destination.getZ()));
            sender.sendMessage(String.format("Departure-Mode: %s%s", ChatColor.GRAY, currentPort.getDeparture()));
            sender.sendMessage(String.format("Price: %s%.2f", ChatColor.GRAY, currentPort.getPrice()));
            sender.sendMessage(String.format("Password: %s%s", ChatColor.GRAY, currentPort.getPassword()));
            if (currentPort.isAllowedToEverybody()) {
                sender.sendMessage("Allowed: " + ChatColor.GRAY + " EVERYBODY");
            } else {
                sender.sendMessage(String.format("Allowed: %s%s", ChatColor.GRAY, StringHelper.encode(
                        currentPort.getAllowed())));
            }
            sender.sendMessage("");
        } else {
            ChatHelper.sendMessage(sender, Messages.get("moderator.problem.not-inside"));
        }

        return true;
    }

    /** @return string which describes the valid usage. */
    @Override
    public String getUsage() {
        return "/<command> info";
    }

    /** @return description of the command. */
    @Override
    public String getDescription() {
        return "Information about your current TravelPort.";
    }

    /** @return required permission for executing this command. */
    @Override
    public Permission getRequiredPermission() {
        return TravelPermissions.INFO;
    }
}
