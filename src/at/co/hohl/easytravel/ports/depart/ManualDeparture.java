package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Departure implementation for manual departures.
 *
 * @author Michael Hohl
 */
public class ManualDeparture implements Departure {
    /** Port, which holds this departure instance. */
    private final TravelPort port;

    /**
     * Creates a new instance of ManualDeparture for the passed port.
     *
     * @param port the port
     */
    public ManualDeparture(TravelPort port) {
        this.port = port;
    }

    /**
     * Called when player uses depart command, when inside the TravelPort.
     *
     * @param player   the player which called the depart command.
     * @param password the password entered by the user.
     */
    public void onDepartCommand(final Player player, final String password) {
        // Is password set and is it valid?
        if (port.isPasswordLocked() && !port.getPassword().equals(password)) {
            ChatHelper.sendMessage(player, Messages.get("problem.invalid-password"));
        } else {
            List<Player> playerList = new LinkedList<Player>();
            playerList.add(player);

            port.depart(playerList);
        }
    }

    /** Periodic called, when the player is inside a TravelPort. */
    public void onPlayersInside() {
        return; // Do nothing...
    }

    /**
     * Called when player entered the TravelPort.
     *
     * @param player the player who enters the port.
     */
    public void onPlayerEntered(final Player player) {
        ChatHelper.sendMessage(player, Messages.get("greeting.departure"));
    }

    /**
     * Called when player left the TravelPort.
     *
     * @param player the player who lefts the port.
     */
    public void onPlayerLeft(final Player player) {
        return; // Do nothing...
    }

    @Override
    public String toString() {
        return "MANUAL";
    }
}
