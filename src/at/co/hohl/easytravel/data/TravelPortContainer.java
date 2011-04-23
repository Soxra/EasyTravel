package at.co.hohl.easytravel.data;

import java.util.Collection;

/**
 * Interface for the TravelPortContainer.
 *
 * @author Michael Hohl
 */
public interface TravelPortContainer {
    /**
     * Creates a new TravelPort. (This will automatically creates an unique ID for it and adds it to storage.)
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
     * @param id could be a part of the name or the id.
     * @return the founded TravelPort.
     *
     * @throws TravelPortNotFound thrown when there is no match for the id.
     */
    TravelPort search(String id) throws TravelPortNotFound;

    /** @return list of TravelPorts. */
    Collection<TravelPort> getAll();

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
     * @throws InvalidLinkException thrown when the ports are already linked.
     */
    void link(TravelPort port1, TravelPort port2) throws InvalidLinkException;

    /**
     * Unlink the passed TravelPort
     *
     * @param port the port to unlink
     * @throws InvalidLinkException thrown when port isn't linked to another.
     */
    void unlink(TravelPort port) throws InvalidLinkException;
}
