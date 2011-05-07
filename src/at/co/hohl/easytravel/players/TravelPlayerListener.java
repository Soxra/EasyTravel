package at.co.hohl.easytravel.players;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.storage.TravelPortNotFound;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.Configuration;

import java.util.Collection;

/**
 * Handle events for all Player related events
 *
 * @author Michael Hohl
 */
public class TravelPlayerListener extends PlayerListener {
    /** Plugin which holds the instance. */
    private final TravelPlugin plugin;

    /**
     * Creates a new instance of the players listener.
     *
     * @param instance the instance of the plugin, which holds the listener.
     */
    public TravelPlayerListener(TravelPlugin instance) {
        plugin = instance;
    }

    /**
     * Called when player left the game.
     *
     * @param event details of the event.
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.removePlayerInformation(event.getPlayer());
    }

    /** Updates the information if the players is inside of a TravelPort. */
    public void onPlayerLocationUpdate() {
        Player[] players = plugin.getServer().getOnlinePlayers();

        for (Player player : players) {
            PlayerInformation playerInformation = plugin.getPlayerInformation(player);
            TravelPort currentTravelPort = playerInformation.getCurrentPort();

            if (currentTravelPort != null) {
                // Check if players is now in TravelPort too!
                if (!currentTravelPort.getArea().contains(player.getLocation())) {
                    onPlayerLeavedTravelPort(player, currentTravelPort);
                    playerInformation.setCurrentPort(null);
                }
            } else {
                // Check if players now has entered one.
                Collection<TravelPort> ports = plugin.getTravelPorts().getAll();
                for (TravelPort port : ports) {
                    if (port.getArea().contains(player.getLocation())) {
                        playerInformation.setCurrentPort(port);
                        onPlayerEnteredTravelPort(player, port);
                    }
                }
            }
        }
    }

    /**
     * Called when the players enters a TravelPort.
     *
     * @param player the players which moves.
     * @param port   the port which the players entered.
     */
    public void onPlayerEnteredTravelPort(Player player, TravelPort port) {
        if (port.getPrice() > 0) {
            if (plugin.getEconomyHandler() != null) {
                ChatHelper.sendMessage(player, String.format(Messages.get("greeting.paid"), port.getPrice(),
                        plugin.getEconomyHandler().getCurrency()));
            } else {
                ChatHelper.sendMessage(player, Messages.get("problem.miss-economy"));
                plugin.getLogger().warning("Player tried to use paid TravelPort, but can't use it, because of " +
                        "missing Economy System!");
            }
        } else {
            ChatHelper.sendMessage(player, Messages.get("greeting.free"));
        }
    }

    /**
     * Called when the players leaves a TravelPort.
     *
     * @param player the players which moves.
     * @param port   the port which the players leaved.
     */
    public void onPlayerLeavedTravelPort(Player player, TravelPort port) {
        //plugin.getLogger().info("Player leaved port!");
    }

    /**
     * Called when players wants to depart and is ready for it.
     *
     * @param player the players who wants to depart.
     * @param port   the port where the players is currently inside.
     */
    public void onPlayerDeparting(final Player player, final TravelPort port) {
        Runnable departRunnable = new Runnable() {
            public void run() {
                try {
                    TravelPort targetPort = plugin.getTravelPorts().get(port.getTargetId());
                    targetPort.getDestination().teleport(player);

                    PlayerInformation playerInformation = plugin.getPlayerInformation(player);
                    playerInformation.setCurrentPort(targetPort);

                    onPlayerTraveled(player, port, targetPort);
                } catch (TravelPortNotFound exception) {
                    ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                    plugin.getLogger()
                            .info(String.format("TravelPort '%s' isn't linked correctly!", port.getName()));
                }
            }
        };

        Configuration config = plugin.getConfiguration();
        long departDelay = config.getInt("depart-delay", 0);

        if (departDelay > 0)

        {
            ChatHelper.sendMessage(player, Messages.get("event.departing"));
        }

        plugin.getServer().

                getScheduler()

                .

                        scheduleAsyncDelayedTask(plugin, departRunnable, departDelay);
    }

    /**
     * Called when the players travels to a TravelPort.
     *
     * @param player the players which travels.
     * @param from   the travel port where to players comes from.
     * @param to     the target travel port where to players goes to.
     */

    public void onPlayerTraveled(final Player player, final TravelPort from, final TravelPort to) {
        Runnable notifyRunnable = new Runnable() {
            public void run() {
                ChatHelper.sendMessage(player, String.format(Messages.get("event.arrived"), to.getName()));
            }
        };

        long notificationDelay = plugin.getConfiguration().getInt("arrived-notification-delay", 10);

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, notifyRunnable, notificationDelay);
    }

    /**
     * Called when the players paid for using a TravelPort.
     *
     * @param player the players which paid.
     * @param amount the amount of money the players paid.
     */
    public void onPlayerPaidForTravelling(Player player, double amount) {
        String currency = plugin.getEconomyHandler().getCurrency();
        ChatHelper.sendMessage(player, String.format(Messages.get("event.money-paid"), amount, currency));
    }

    /**
     * Called when players tries to depart, when not inside TravelPort
     *
     * @param player the players on which depends this event.
     */
    public void onNotInsideTravelPort(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.not-inside"));
    }

    /**
     * Called when players is not allowed to travel.
     *
     * @param player the players on which depends this event.
     */
    public void onNotAllowedToDepart(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
    }

    /**
     * Called when players password is wrong.
     *
     * @param player the players on which depends this event.
     */
    public void onInvalidPassword(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.invalid-password"));
    }

    /**
     * Called when the players has to little money to depart.
     *
     * @param player the players on which depends this event.
     */
    public void onLittleMoney(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
    }
}

