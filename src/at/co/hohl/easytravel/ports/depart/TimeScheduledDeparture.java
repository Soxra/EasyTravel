package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.PlayerInformation;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.TravelPort;
import at.co.hohl.utils.BukkitTime;
import at.co.hohl.utils.ChatHelper;
import com.sun.istack.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Departure which is controlled by a scheduler.
 *
 * @author Michael Hohl
 */
public class TimeScheduledDeparture implements Departure {
    /** Time when departure. */
    private final List<BukkitTime> departureTimes;

    /** All players inside the port. */
    private final List<Player> playerInside = new LinkedList<Player>();

    /** TravelPort which holds the port. */
    private final TravelPort port;

    /** Next departure time. */
    private BukkitTime nextDeparture;

    /**
     * Creates a new TimeScheduledDeparture.
     *
     * @param port           the port which holds this departure.
     * @param departureTimes the times to departure.
     */
    public TimeScheduledDeparture(TravelPort port, List<BukkitTime> departureTimes) {
        this.departureTimes = departureTimes;
        this.port = port;
        Collections.sort(departureTimes);
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
        variables.put("time", nextDeparture.getDayTime12());
        ChatHelper.sendMessage(player, Messages.get("messages.next-departure", variables));
    }

    /**
     * Periodic called, when the player is inside a TravelPort.
     *
     * @param daytime current time of day.
     */
    @Override
    public void onPlayersInside(long daytime) {
        // Check, if it's time to depart.
        if (nextDeparture != null && nextDeparture.getTicks() < daytime) {
            port.depart(playerInside);

            nextDeparture = getNextDeparture(daytime);
        }
    }

    /**
     * Called when player entered the TravelPort.
     *
     * @param player the player who enters the port.
     */
    @Override
    public void onPlayerEntered(Player player) {
        playerInside.add(player);

        // Get next departure if needed!
        if (nextDeparture == null) {
            nextDeparture = getNextDeparture(player.getWorld().getTime());
        }
    }

    /**
     * Called when player left the TravelPort.
     *
     * @param player the player who lefts the port.
     */
    @Override
    public void onPlayerLeft(Player player) {
        playerInside.add(player);

        // Remove next departure, when there isn't any inside.
        if (playerInside.size() == 0) {
            nextDeparture = null;
        }
    }

    /**
     * Returns nearest departure to the passed time.
     *
     * @param currentTime the current time.
     * @return the nearest time.
     */
    @Nullable
    public BukkitTime getNextDeparture(long currentTime) {
        for (BukkitTime time : departureTimes) {
            if (time.getTicks() > currentTime) {
                return time;
            }
        }

        // Return the first on the next day, if there isn't anyone today anymore.
        if (departureTimes.size() > 0) {
            return departureTimes.get(0);
            // Or null if there isn't anyone at all!
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int departureTimesSize = departureTimes.size();
        for (int index = 0; index < departureTimesSize; index++) {
            builder.append(departureTimes.get(index));

            if (index < departureTimesSize - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }
}
