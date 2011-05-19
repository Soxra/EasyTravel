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
