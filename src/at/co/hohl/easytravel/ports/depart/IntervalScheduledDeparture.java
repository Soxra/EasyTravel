/*
 * EasyTravel
 * Copyright (C) 2011 Michael Hohl <http://www.hohl.co.at/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
    /**
     * Interval
     */
    private final long interval;

    /**
     * Time of the last depart.
     */
    private long lastDepart;

    /**
     * Port which holds this instance.
     */
    private final TravelPort port;

    /**
     * All players inside the port.
     */
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
        sendGreeting(player);
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
        sendGreeting(player);

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

    /**
     * Sends a greeting to the player.
     *
     * @param player the player to send to.
     */
    private void sendGreeting(Player player) {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("current", new BukkitTime(player.getWorld().getTime()).getDayTime12());
        variables.put("time", new BukkitTime((lastDepart + interval) % 24000).getDayTime12());
        ChatHelper.sendMessage(player, Messages.get("messages.next-departure", variables));
    }
}
