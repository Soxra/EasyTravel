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

import at.co.hohl.easytravel.TravelException;
import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Collection;

/**
 * Interface for the TravelPortContainer.
 *
 * @author Michael Hohl
 */
public interface TravelPortContainer {
    /**
     * @return the server which holds the container.
     */
    Server getServer();

    /**
     * @return the plugin, which holds the container.
     */
    TravelPlugin getPlugin();

    /**
     * Creates a new TravelPort. (This will automatically creates an unique ID for it and adds it to implementation.)
     *
     * @return the created TravelPort
     */
    TravelPort create();

    /**
     * Gets the travel port with the passed id.
     *
     * @param id the id of the travel port to get.
     * @return the travel port.
     * @throws TravelPortNotFound thrown when there isn't any port with the passed id.
     */
    TravelPort get(Integer id) throws TravelPortNotFound;

    /**
     * @return list of TravelPorts.
     */
    Collection<TravelPort> getAll();

    /**
     * Searches a TravelPort.
     *
     * @param keyword could be a part of the name or the id.
     * @return the founded TravelPort.
     * @throws TravelPortNotFound thrown when there is no match for the id.
     */
    TravelPort search(String keyword) throws TravelPortNotFound;

    /**
     * Searches a TravelPort at the Location.
     *
     * @param location the location to search for.
     * @return search result.
     */
    Collection<TravelPort> search(Location location);

    /**
     * Searches the TravelPorts.
     *
     * @param keyword could be a part of the name of the TravelPort to search.
     * @return all TravelPorts matching the keyword.
     */
    Collection<TravelPort> searchAll(String keyword);

    /**
     * Adds the passed travel port to the port list.
     *
     * @param port the port to add.
     */
    void add(TravelPort port);

    /**
     * Removes the travel port, and unlink it before, when necessary.
     *
     * @param port the port to remove.
     */
    void remove(TravelPort port);

    /**
     * Links the passed TravelPorts.
     *
     * @param port1 the first port to link.
     * @param port2 another port to link.
     * @throws TravelPortContainer.InvalidLinkException
     *          thrown when the ports are already linked.
     */
    void link(TravelPort port1, TravelPort port2) throws InvalidLinkException;

    /**
     * Unlink the passed TravelPort
     *
     * @param port the port to unlink
     * @throws TravelPortContainer.InvalidLinkException
     *          thrown when port isn't linked to another.
     */
    void unlink(TravelPort port) throws InvalidLinkException;

    /**
     * @return number of available entries.
     */
    int size();

    /**
     * Exception for invalid links.
     */
    public class InvalidLinkException extends TravelException {
        /**
         * Creates a new InvalidLinkException.
         *
         * @param message details
         */
        public InvalidLinkException(String message) {
            super(message);
        }
    }
}
