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

package at.co.hohl.easytravel.ports;

import at.co.hohl.easytravel.ports.depart.Departure;
import at.co.hohl.permissions.PermissionHandler;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a TravelPort
 *
 * @author Michael Hohl
 */
public interface TravelPort {
    /**
     * Sets who is allowed to use this TravelPort. If set to null, everybody is allowed to use.
     *
     * @param allowed list of Strings with names of groups and players.
     */
    void setAllowed(List<String> allowed);

    /**
     * @return true, if this TravelPort is allowed to everybody.
     */
    boolean isAllowedToEverybody();

    /**
     * Checks if the players is allowed to use the TravelPort.
     *
     * @param permissions needed to check if the user is in group.
     * @param player      the players to check.
     * @return true, if the players is allowed to.
     */
    boolean isAllowed(PermissionHandler permissions, Player player);

    /**
     * Allows this TravelPort to everybody.
     */
    void setAllowedToEverybody();

    /**
     * Adds somebody to the allowed list.
     *
     * @param allowed the one to add.
     */
    void addAllowed(String allowed);

    /**
     * Removes someone from the allowed list. If the allowed list is empty, everybody is allowed to use!
     *
     * @param allowed the one to remove.
     */
    void removeAllowed(String allowed);

    /**
     * @return the unique id of the travel port.
     */
    Integer getId();

    /**
     * @return the name of the travel point.
     */
    String getName();

    /**
     * Sets the name of the TravelPoint.
     *
     * @param name the name to set.
     */
    void setName(String name);

    /**
     * @return the id of the target.
     */
    Integer getTargetId();

    /**
     * @return the target or null if not linked to any.
     */
    TravelPort getTarget();

    /**
     * Sets the id of the target.
     *
     * @param targetId the id of the target.
     */
    void setTargetId(Integer targetId);

    /**
     * @return area of the port.
     */
    Area getArea();

    /**
     * Sets the area of the port.
     *
     * @param area the area to set.
     */
    void setArea(Area area);

    /**
     * @return the destination
     */
    Destination getDestination();

    /**
     * Sets the destination.
     *
     * @param destination the destination to set.
     */
    void setDestination(Destination destination);

    /**
     * @return the departure.
     */
    Departure getDeparture();

    /**
     * Sets the departure.
     *
     * @param departure the departure to set.
     */
    void setDeparture(Departure departure);

    /**
     * @return the price it costs to travel.
     */
    double getPrice();

    /**
     * Sets the price it costs to travel.
     *
     * @param price the price it costs to travel
     */
    void setPrice(double price);

    /**
     * @return the password needed to travel with this port.
     */
    String getPassword();

    /**
     * @return true if the TravelPort is locked with a password.
     */
    boolean isPasswordLocked();

    /**
     * @param password sets the password needed to travel
     */
    void setPassword(String password);

    /**
     * @return a list of allowed groups and players. If null everybody is allowed to use that TravelPort.
     */
    List<String> getAllowed();

    /**
     * @return owner of the port.
     */
    String getOwner();

    /**
     * Sets the owner of the port.
     *
     * @param owner the name of the owner to set.
     */
    void setOwner(String owner);

    /**
     * Called when departing all passed players is needed.
     *
     * @param players the player to depart.
     */
    void depart(List<Player> players);

    /**
     * Called when a player arrives at the TravelPort.
     *
     * @param player the player which arrives at the TravelPort.
     */
    void onPlayerArrived(Player player);

    /**
     * Called when a player entered the TravelPort.
     *
     * @param player the player which enters the TravelPort.
     */
    void onPlayerEntered(Player player);

    /**
     * Called when a player left the TravelPort.
     *
     * @param player the player, which left the TravelPort.
     */
    void onPlayerLeft(Player player);
}
