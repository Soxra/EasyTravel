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
     * @param player            the player which called the depart command.
     * @param playerInformation information about the player.
     */
    public void onDepartCommand(final Player player, final PlayerInformation playerInformation) {
        // Is password set and is it valid?
        if (port.isPasswordLocked() && !playerInformation.hasPassword(port.getPassword())) {
            ChatHelper.sendMessage(player, Messages.get("problem.invalid-password"));
        } else {
            List<Player> playerList = new LinkedList<Player>();
            playerList.add(player);

            port.depart(playerList);
        }
    }

    /**
     * Periodic called, when the player is inside a TravelPort.
     *
     * @param daytime current time of day.
     */
    @Override
    public void onPlayersInside(long daytime) {
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
