package at.co.hohl.easytravel.ports.implementation;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.easytravel.ports.TravelPortContainer;
import at.co.hohl.utils.ChatHelper;
import com.nijikokun.register.payment.Method;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * @param playersToDepart the player to depart.
     */
    public final void depart(final List<Player> playersToDepart) {
        // Create copy, so list don't get manipulated from outside.
        // (Like when a player moved outside, during delay time!)
        final List<Player> players = new LinkedList<Player>(playersToDepart);

        // Own runnable, for delaying the teleport.
        Runnable departRunnable = new Runnable() {
            public void run() {
                // Teleport all players.
                for (Player player : players) {
                    TravelPort targetPort = getTarget();

                    if (targetPort != null) {
                        targetPort.getDestination().teleport(player);

                        onPlayerLeft(player);
                        targetPort.onPlayerArrived(player);
                    } else {
                        ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                    }
                }
            }
        };

        for (int index = 0; index < players.size(); ++index) {
            Player player = players.get(index);
            String playerAccount = player.getName();
            TravelPlugin plugin = container.getPlugin();

            // Is players allowed to use TravelPort?
            if (!isAllowed(plugin.getPermissionsHandler(), player)) {
                ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
                players.remove(player);
                --index;
                break;
            }

            // Has player enough money?
            if (getPrice() > 0) {
                if (plugin.hasPaymentMethods()) {
                    Method method = plugin.getPaymentMethod();

                    if (method.hasAccount(playerAccount) && method.getAccount(playerAccount).hasEnough(getPrice())) {
                        method.getAccount(playerAccount).subtract(getPrice());

                        Map<String, String> variables = new HashMap<String, String>();
                        variables.put("account", playerAccount);
                        variables.put("player", player.getName());
                        variables.put("amount", method.format(getPrice()));
                        ChatHelper.sendMessage(player, Messages.get("event.money-paid", variables));
                    } else {
                        ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
                        players.remove(player);
                        --index;
                        break;
                    }
                } else {
                    ChatHelper.sendMessage(player, Messages.get("problem.miss-economy"));
                    players.remove(player);
                    --index;
                    break;
                }
            }

            // Notify all players of departing.
            if (TravelPlugin.DEPART_DELAY > 0) {
                ChatHelper.sendMessage(player, Messages.get("event.departing"));
            }
        }

        container.getServer().getScheduler().scheduleAsyncDelayedTask(container.getPlugin(), departRunnable,
                TravelPlugin.DEPART_DELAY);
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
                Map<String, String> variables = new HashMap<String, String>();
                variables.put("target", getName());
                ChatHelper.sendMessage(player, Messages.get("event.arrived", variables));
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
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("player", player.getName());

        if (getPrice() > 0) {
            if (container.getPlugin().hasPaymentMethods()) {
                Method method = container.getPlugin().getPaymentMethod();


                variables.put("amount", method.format(getPrice()));
                ChatHelper.sendMessage(player, Messages.get("greeting.paid", variables));
            } else {
                ChatHelper.sendMessage(player, Messages.get("problem.miss-economy", variables));
            }
        } else {
            ChatHelper.sendMessage(player, Messages.get("greeting.free", variables));
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
