package at.co.hohl.easytravel.ports;

import at.co.hohl.easytravel.TravelException;
import at.co.hohl.easytravel.TravelPlugin;
import org.bukkit.Server;

import java.util.Collection;

/**
 * Interface for the TravelPortContainer.
 *
 * @author Michael Hohl
 */
public interface TravelPortContainer {
    /** @return the server which holds the container. */
    Server getServer();

    /** @return the plugin, which holds the container. */
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
     *
     * @throws TravelPortNotFound thrown when there isn't any port with the passed id.
     */
    TravelPort get(Integer id) throws TravelPortNotFound;

    /**
     * Searches a TravelPort.
     *
     * @param keyword could be a part of the name or the id.
     * @return the founded TravelPort.
     *
     * @throws TravelPortNotFound thrown when there is no match for the id.
     */
    TravelPort search(String keyword) throws TravelPortNotFound;

    /** @return list of TravelPorts. */
    Collection<TravelPort> getAll();

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

    /** @return number of available entries. */
    int size();

    /** Exception for invalid links. */
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
