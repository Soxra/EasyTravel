package at.co.hohl.easytravel.commands;

import at.co.hohl.easytravel.LocalizedStrings;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.exceptions.WarpException;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.data.TravelPortContainer;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.utils.ChatHelper;
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TravelPortContainer container = plugin.getTravelPortContainer();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.getPermissionsHandler().hasPermission(player, TravelPermissions.DEPART_PERMISSION)) {
                if (container.isInsideTravelPort(player)) {
                    TravelPort port = container.getPlayerCurrentTravelPort(player);

                    try {
                        if (port.getPrice() > 0) {
                            if (plugin.getEconomyHandler() != null) {
                                EconomyHandler economyHandler = plugin.getEconomyHandler();

                                if (economyHandler.getBalance(player) >= port.getPrice()) {
                                    economyHandler.pay(player, port.getPrice());
                                    ChatHelper.sendMessage(player, LocalizedStrings.MONEY_PAYED);
                                    plugin.warpPlayer(player, port);
                                } else {
                                    ChatHelper.sendMessage(player, LocalizedStrings.NOT_ENOUGH_MONEY);
                                }
                            } else {
                                ChatHelper.sendMessage(player, LocalizedStrings.PROBLEM_WITH_ECONOMY);
                            }
                        } else {
                            plugin.warpPlayer(player, port);
                        }
                    } catch (WarpException exception) {
                        ChatHelper.sendMessage(player, LocalizedStrings.PORT_HAS_NO_TARGET);
                        plugin.getLogger().severe(exception.getMessage());
                    }
                } else {
                    ChatHelper.sendMessage(player, LocalizedStrings.NOT_INSIDE_TRAVEL_PORT);
                }
            } else {
                ChatHelper.sendMessage(player, LocalizedStrings.NO_DEPART_PERMISSIONS);
            }

            return true;
        } else {
            sender.sendMessage("Only use the /depart command as player!");

            return true;
        }
    }
}
