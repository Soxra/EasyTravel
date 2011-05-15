package at.co.hohl.easytravel.listener;

import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.ports.TravelPort;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

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
                    currentTravelPort.onPlayerLeft(player);
                    playerInformation.setCurrentPort(null);
                } else {
                    currentTravelPort.getDeparture().onPlayersInside();
                }
            } else {
                // Check if players now has entered one.
                Collection<TravelPort> ports = plugin.getTravelPorts().getAll();
                for (TravelPort port : ports) {
                    if (port.getArea().contains(player.getLocation())) {
                        playerInformation.setCurrentPort(port);
                        port.onPlayerEntered(player);
                    }
                }
            }
        }
    }
}

