package at.co.hohl.easytravel.ports.implementation;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents the base class for all implementations of TravelPorts. Implement all storage independent stuff.
 *
 * @author Michael Hohl
 */
public abstract class AbstractTravelPort implements TravelPort {
    /** Container which holds the instance. */
    protected final TravelPortContainer container;

    /** @param container container, which holds this port. */
    public AbstractTravelPort(TravelPortContainer container) {
        this.container = container;
    }

    /**
     * Called when departing all passed players is needed.
     *
     * @param players the player to depart.
     */
    public final void depart(final List<Player> players) {
        // Own runnable, for delaying the teleport.
        Runnable departRunnable = new Runnable() {
            public void run() {
                // Teleport all players.
                for (Player player : players) {
                    TravelPort targetPort = getTarget();

                    if (targetPort != null) {
                        targetPort.getDestination().teleport(player);

                        targetPort.onPlayerArrived(player);
                    } else {
                        ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                    }
                }
            }
        };

        for (int index = 0; index < players.size(); ++index) {
            Player player = players.get(index);

            // Is players allowed to use TravelPort?
            if (!isAllowed(container.getPlugin().getPermissionsHandler(), player)) {
                ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
                players.remove(players);
                --index;
                break;
            }

            // Has player enough money?
            if (getPrice() > 0) {
                EconomyHandler economyHandler = container.getPlugin().getEconomyHandler();
                if (economyHandler != null && economyHandler.pay(player.getName(), getPrice())) {
                    ChatHelper.sendMessage(player,
                            String.format(Messages.get("event.money-paid"), getPrice(), economyHandler.getCurrency()));
                } else {
                    ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
                    players.remove(players);
                    --index;
                    break;
                }
            }

            // Notify all players of departing.
            if (TravelPlugin.DEPART_DELAY > 0) {
                ChatHelper.sendMessage(player, Messages.get("event.departing"));
            }
        }

        container.getServer().getScheduler()
                .scheduleAsyncDelayedTask(container.getPlugin(), departRunnable, TravelPlugin.DEPART_DELAY);
    }

    /**
     * Called when a player arrives at the TravelPort.
     *
     * @param player the player which arrives at the TravelPort.
     */
    public final void onPlayerArrived(final Player player) {
        // Save Players current location.
        container.getPlugin().getPlayerInformation(player).setCurrentPort(this);

        // Notify user about arriving.
        Runnable notifyRunnable = new Runnable() {
            public void run() {
                ChatHelper.sendMessage(player, String.format(Messages.get("event.arrived"), getName()));
            }
        };
        container.getServer().getScheduler().scheduleAsyncDelayedTask(container.getPlugin(), notifyRunnable,
                TravelPlugin.ARRIVED_NOTIFICATION_DELAY);
    }

    /**
     * Called when a player entered the TravelPort.
     *
     * @param player the player which enters the TravelPort.
     */
    public final void onPlayerEntered(final Player player) {
        if (getPrice() > 0) {
            EconomyHandler economyHandler = container.getPlugin().getEconomyHandler();
            if (economyHandler != null) {
                ChatHelper.sendMessage(player, String.format(Messages.get("greeting.paid"), getPrice(),
                        economyHandler.getCurrency()));
            } else {
                ChatHelper.sendMessage(player, Messages.get("problem.miss-economy"));
            }
        } else {
            ChatHelper.sendMessage(player, Messages.get("greeting.free"));
        }

        getDeparture().onPlayerEntered(player);
    }

    /**
     * Called when a player left the TravelPort.
     *
     * @param player the player, which left the TravelPort.
     */
    public final void onPlayerLeft(final Player player) {
        getDeparture().onPlayerLeft(player);
    }
}
