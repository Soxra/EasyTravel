package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.utils.BukkitTime;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Departures in a scheduler controlled interval.
 *
 * @author Michael Hohl
 */
public class IntervalScheduledDeparture implements Departure {
    /** Interval */
    private final long interval;

    /** Time of the last depart. */
    private long lastDepart;

    /** Port which holds this instance. */
    private final TravelPort port;

    /** All players inside the port. */
    private final List<Player> playerInside = new LinkedList<Player>();

    /**
     * Creates a new IntervalScheduledDeparture.
     *
     * @param port     the port to hold it.
     * @param interval the interval used for.
     */
    public IntervalScheduledDeparture(TravelPort port, BukkitTime interval) {
        this.port = port;
        this.interval = interval.getTicks();
    }

    /**
     * Called when player uses depart command, when inside the TravelPort.
     *
     * @param player            the player which called the depart command.
     * @param playerInformation information about the player.
     */
    @Override
    public void onDepartCommand(Player player, PlayerInformation playerInformation) {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("time", new BukkitTime((lastDepart + interval) % 24000).getDayTime12());
        ChatHelper.sendMessage(player, Messages.get("messages.next-departure", variables));
    }

    /**
     * Periodic called, when the player is inside a TravelPort.
     *
     * @param daytime current time of day.
     */
    @Override
    public void onPlayersInside(long daytime) {
        long nextDeparture = (lastDepart + interval);
        if (lastDepart > daytime) {
            nextDeparture = nextDeparture % 24000;
        }

        if (nextDeparture < daytime) {
            port.depart(playerInside);
            lastDepart = daytime;
        }
    }

    /**
     * Called when player entered the TravelPort.
     *
     * @param player the player who enters the port.
     */
    @Override
    public void onPlayerEntered(Player player) {
        // Send Greeting.
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("time", new BukkitTime((lastDepart + interval) % 24000).getDayTime12());
        ChatHelper.sendMessage(player, Messages.get("messages.next-departure", variables));

        // Add to player inside list.
        playerInside.add(player);
    }

    /**
     * Called when player left the TravelPort.
     *
     * @param player the player who lefts the port.
     */
    @Override
    public void onPlayerLeft(Player player) {
        // Don't forget to remove!
        playerInside.remove(player);
    }

    @Override
    public String toString() {
        return String.format("every %d", interval);
    }
}
