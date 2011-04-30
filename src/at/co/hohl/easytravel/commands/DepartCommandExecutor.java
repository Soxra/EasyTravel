package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPlayerListener;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.economy.EconomyHandler;
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
        TravelPlayerListener playerListener = plugin.getPlayerListener();

        // Is sender a player?
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only use the /depart command as player! Doesn't make sense anyway for you ;)");
            return true;
        }
        Player player = (Player) sender;
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);

        // Is player inside TravelPort?
        TravelPort port = playerInformation.getCurrentPort();
        if (port == null) {
            playerListener.onNotInsideTravelPort(player);
            return true;
        }

        // Is player allowed to use TravelPort?
        if (!port.isAllowed(plugin.getPermissionsHandler(), player)) {
            playerListener.onNotAllowedToDepart(player);
            return true;
        }

        // Is password set and is it valid?
        if (port.isPasswordLocked()) {
            if (!port.getPassword().equals(playerInformation.getEnteredPassword())) {
                playerListener.onInvalidPassword(player);
                return true;
            }
        }

        // Is TravelPort paid? Pay money than!
        double price = port.getPrice();
        if (price > 0) {
            EconomyHandler economyHandler = plugin.getEconomyHandler();
            if (economyHandler != null && economyHandler.pay(player.getName(), price)) {
                playerListener.onPlayerPaidForTravelling(player, price);
            } else {
                playerListener.onLittleMoney(player);
                return true;
            }
        }

        // Everything checked? Then depart the user now.
        playerListener.onPlayerDeparting(player, port);
        return true;
    }
}
