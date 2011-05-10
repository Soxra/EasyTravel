package at.co.hohl.easytravel.ports.depart;

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
     * @param player   the player which called the depart command.
     * @param password the password entered by the user.
     */
    void onDepartCommand(final Player player, final String password);

    /** Periodic called, when the player is inside a TravelPort. */
    void onPlayersInside();

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
