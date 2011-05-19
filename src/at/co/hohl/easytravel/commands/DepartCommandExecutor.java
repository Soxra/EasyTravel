package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.PlayerInformation;
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
 * CommandExecutor for the /depart command.
 *
 * @author Michael Hohl
 */
public class DepartCommandExecutor implements CommandExecutor {
    /** The plugin, which holds this instance. */
    private final TravelPlugin plugin;

    /**
     * Creates a new CommandExecutor for the /depart-Command.
     *
     * @param plugin the plugin which holds the instance.
     */
    public DepartCommandExecutor(TravelPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the /depart-Command gets called.
     *
     * @param sender  the sender of the command.
     * @param command the command itself.
     * @param label   the label used for calling the command.
     * @param args    the arguments passed to the command.
     * @return true, if the Executor could handle the command.
     */
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Is sender a players?
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only use the /depart command as players! Doesn't make sense anyway for you ;)");
            return true;
        }
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);

        // Is players inside TravelPort?
        if (!playerInformation.isInsideTravelPort()) {
            ChatHelper.sendMessage(player, Messages.get("problem.not-inside"));
            return true;
        }

        // Passed any password?
        if (args.length > 0) {
            playerInformation.addPassword(StringHelper.toSingleString(args, " ", 0));
        }

        // Check if port has a target.
        TravelPort port = playerInformation.getCurrentPort();
        if (port.getTargetId() != null) {
            port.getDeparture().onDepartCommand(player, playerInformation);
        } else {
            ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
        }
        return true;
    }
}
