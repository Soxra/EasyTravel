package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.PlayerInformation;
import org.bukkit.entity.Player;

/**
 * Interface for all implementation of departs.
 *
 * @author Michael Hohl
 */
public interface Departure {
    /**
     * Called when player uses depart command, when inside the TravelPort.
     *
     * @param player            the player which called the depart command.
     * @param playerInformation information about the player.
     */
    void onDepartCommand(final Player player, final PlayerInformation playerInformation);

    /**
     * Periodic called, when the player is inside a TravelPort.
     *
     * @param daytime current time of day.
     */
    void onPlayersInside(long daytime);

    /**
     * Called when player entered the TravelPort.
     *
     * @param player the player who enters the port.
     */
    void onPlayerEntered(final Player player);

    /**
     * Called when player left the TravelPort.
     *
     * @param player the player who lefts the port.
     */
    void onPlayerLeft(final Player player);
}
