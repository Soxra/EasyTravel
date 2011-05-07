package at.co.hohl.easytravel;

import at.co.hohl.easytravel.data.PlayerInformation;
import at.co.hohl.easytravel.data.TravelPort;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
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
     * Creates a new instance of the player listener.
     *
     * @param instance the instance of the plugin, which holds the listener.
     */
    public TravelPlayerListener(TravelPlugin instance) {
        plugin = instance;
    }

    /**
     * Called when the player moves. Here: Check if he moves inside a TravelPort.
     *
     * @param event detailed information about the event.
     */
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerInformation playerInformation = plugin.getPlayerInformation(player);
        TravelPort currentTravelPort = playerInformation.getCurrentPort();

        if (currentTravelPort != null) {
            // Check if player is now in TravelPort too!
            if (!currentTravelPort.getArea().contains(player.getLocation())) {
                boolean traveledRecently = playerInformation.isAlreadyTravelled();
                onPlayerLeavedTravelPort(player, currentTravelPort, traveledRecently);
                playerInformation.setAlreadyTravelled(false);
                playerInformation.setCurrentPort(null);
            }
        } else {
            // Check if player now has entered one.

            Collection<TravelPort> ports = plugin.getTravelPorts().getAll();
            for (TravelPort port : ports) {
                if (port.getArea().contains(player.getLocation())) {
                    playerInformation.setCurrentPort(port);
                    onPlayerEnteredTravelPort(player, port);
                }
            }
        }
    }

    /**
     * Called when the player enters a TravelPort.
     *
     * @param player the player which moves.
     * @param port   the port which the player entered.
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
     * Called when the player leaves a TravelPort.
     *
     * @param player          the player which moves.
     * @param port            the port which the player leaved.
     * @param alreadyTraveled true, if the player leaves the travel port, after traveling.
     */
    public void onPlayerLeavedTravelPort(Player player, TravelPort port, boolean alreadyTraveled) {
        //plugin.getLogger().info("Player leaved port!");
    }

    /**
     * Called when player wants to depart and is ready for it.
     *
     * @param player the player who wants to depart.
     * @param port   the port where the player is currently inside.
     */
    public void onPlayerDeparting(final Player player, final TravelPort port) {
        Runnable departRunnable = new Runnable() {
            public void run() {
                try {
                    plugin.teleportPlayer(player, port);
                } catch (WarpException exception) {
                    ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                    plugin.getLogger().info(String.format("TravelPort '%s' isn't linked correctly!", port.getName()));
                }
            }
        };

        Configuration config = plugin.getConfiguration();
        long departDelay = config.getInt("depart-delay", 0);

        if (departDelay > 0) {
            ChatHelper.sendMessage(player, Messages.get("event.departing"));
        }

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, departRunnable, departDelay);
    }

    /**
     * Called when the player travels to a TravelPort.
     *
     * @param player the player which travels.
     * @param from   the travel port where to player comes from.
     * @param to     the target travel port where to player goes to.
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
     * Called when the player paid for using a TravelPort.
     *
     * @param player the player which paid.
     * @param amount the amount of money the player paid.
     */
    public void onPlayerPaidForTravelling(Player player, double amount) {
        String currency = plugin.getEconomyHandler().getCurrency();
        ChatHelper.sendMessage(player, String.format(Messages.get("event.money-paid"), amount, currency));
    }

    /**
     * Called when player tries to depart, when not inside TravelPort
     *
     * @param player the player on which depends this event.
     */
    public void onNotInsideTravelPort(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.not-inside"));
    }

    /**
     * Called when player is not allowed to travel.
     *
     * @param player the player on which depends this event.
     */
    public void onNotAllowedToDepart(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
    }

    /**
     * Called when players password is wrong.
     *
     * @param player the player on which depends this event.
     */
    public void onInvalidPassword(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.invalid-password"));
    }

    /**
     * Called when the player has to little money to depart.
     *
     * @param player the player on which depends this event.
     */
    public void onLittleMoney(Player player) {
        ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
    }
}

