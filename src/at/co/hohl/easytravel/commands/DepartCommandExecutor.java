package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.WarpException;
import at.co.hohl.easytravel.data.InvalidPortIdException;
import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInformation playerInformation = plugin.getPlayerInformation(player);

            try {
                TravelPort port = playerInformation.getCurrentPort();

                if (port != null) {
                    if (port.isAllowed(plugin.getPermissionsHandler(), player)) {
                        String password = port.getPassword();
                        if (password != null && !password.equals(playerInformation.getEnteredPassword())) {
                            ChatHelper.sendMessage(player, Messages.get("problem.invalid-password"));
                        } else {
                            try {
                                double price = port.getPrice();
                                if (price > 0) {
                                    EconomyHandler economyHandler = plugin.getEconomyHandler();
                                    if (economyHandler != null && economyHandler.pay(player, price)) {
                                        plugin.getPlayerListener().onPlayerPaidForTravelling(player, price);
                                        plugin.teleportPlayer(player, port);
                                    } else {
                                        ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
                                    }
                                } else {
                                    plugin.teleportPlayer(player, port);
                                }
                            } catch (WarpException exception) {
                                ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                                plugin.getLogger().severe(exception.getMessage());
                            } catch (InvalidPortIdException exception) {
                                ChatHelper.sendMessage(player, Messages.get("problem.internal-error"));
                            }
                        }
                    } else {
                        ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
                    }
                } else {
                    ChatHelper.sendMessage(player, Messages.get("problem.not-inside"));
                }

            } catch (CommandException exception) {
                ChatHelper.sendMessage(player, Messages.get("problem.internal-error"));

                if (player.isOp()) {
                    player.sendMessage(ChatColor.RED + "Error Message: " + exception.getMessage());
                }
            }

            return true;
        } else {
            sender.sendMessage("Only use the /depart command as player! Doesn't make sense anyway for you ;)");

            return true;
        }
    }
}
