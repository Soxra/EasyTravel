package at.co.hohl.easytravel.data;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.exceptions.InvalidLinkException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Represents a container for the TravelPorts.
 *
 * @author Michael Hohl
 */
public class TravelPortContainer {
    /** The plugin which holds this instance. */
    private final Server server;

    /** The file used for storing the TravelPorts. */
    private final File csvFile;

    /** Logger used for outputing debug information. */
    private final Logger logger;

    /** Contains all travel ports. */
    private final Hashtable<Integer, TravelPort> travelPorts = new Hashtable<Integer, TravelPort>();

    /** The travel port, where a player is inside. */
    private final Hashtable<Player, TravelPort> playerInsideTravelPort = new Hashtable<Player, TravelPort>();

    /** true, if the passed player traveled recently. */
    private final Hashtable<Player, Boolean> playerTraveledRecently = new Hashtable<Player, Boolean>();


    /**
     * Creates a new container for TravelPorts.
     *
     * @param plugin  the plugin which holds the instance.
     * @param csvFile the fle used for storing the TravelPorts.
     */
    public TravelPortContainer(TravelPlugin plugin, File csvFile) {
        this.csvFile = csvFile;
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
    }

    /** Loads the TravelPorts. */
    public void load() {
        if (csvFile.exists()) {
            try {
                TravelPortStorage.loadPorts(server, travelPorts, csvFile);
            } catch (IOException exception) {
                logger.severe("Error occurred when loading TravelPorts!");
                logger.severe(exception.getMessage());
            }
        } else {
            logger.warning("TravelPorts file didn't exist! Create new one...");
            travelPorts.clear();
        }
    }

    /** Saves the TravelPorts. */
    public void save() {
        try {
            TravelPortStorage.savePorts(travelPorts, csvFile);
        } catch (IOException exception) {
            logger.severe("Error occurred when saving TravelPorts!");
            logger.severe(exception.getMessage());
        }
    }

    /**
     * Gets the travel port with the passed id.
     *
     * @param id the id of the travel port to get.
     * @return the travel port.
     *
     * @throws at.co.hohl.easytravel.data.InvalidPortIdException
     *          thrown when there isn't any port with the passed id.
     */
    public TravelPort get(Integer id) {
        if (travelPorts.contains(id)) {
            return travelPorts.get(id);
        } else {
            throw new InvalidPortIdException();
        }
    }

    /** @return list of TravelPorts. */
    public Collection<TravelPort> getAll() {
        return travelPorts.values();
    }

    /**
     * Adds the passed travel port to the port list.
     *
     * @param port the port to add.
     */
    public void add(TravelPort port) {
        travelPorts.put(port.getId(), port);
    }

    /**
     * Removes the travel port, and unlink it before, when necessary.
     *
     * @param port the port to remove.
     */
    public void remove(TravelPort port) {
        if (port.getTarget() != null) {
            try {
                unlink(port);
            } catch (InvalidLinkException exception) {
                logger.severe("Internal unexpected error occurred! Seems to be a multithreading problem");
            }
        }

        travelPorts.remove(port);
    }

    /**
     * Links the passed TravelPorts.
     *
     * @param port1 the first port to link.
     * @param port2 another port to link.
     * @throws InvalidLinkException thrown when the ports are already linked.
     */
    public void link(TravelPort port1, TravelPort port2) throws InvalidLinkException {
        if (port1.getTarget() == null && port2.getTarget() == null) {
            port1.setTarget(port2.getId());
            port2.setTarget(port1.getId());
        } else {
            throw new InvalidLinkException("Ports are already linked!");
        }
    }

    /**
     * Unlink the passed TravelPort
     *
     * @param port
     * @throws InvalidLinkException
     */
    public void unlink(TravelPort port) throws InvalidLinkException {
        if (port.getTarget() != null) {
            TravelPort anotherPort = get(port.getTarget());

            port.setTarget(null);
            anotherPort.setTarget(null);
        } else {
            throw new InvalidLinkException("Can't unlinke ports, which aren't linked!");
        }
    }

    /** @return the next free travel port id. */
    public Integer getFreeTravelPortId() {
        Integer currentId = new Integer(0);

        for (TravelPort travelPort : travelPorts.values()) {
            if (travelPort.getId() > currentId) {
                currentId = travelPort.getId();
            }
        }

        return currentId + 1;
    }

    /**
     * @param player the player to check.
     * @return true, if the player is already in a TravelPort.
     */
    public boolean isInsideTravelPort(Player player) {
        TravelPort insideTravelPort = playerInsideTravelPort.get(player);
        if (insideTravelPort == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the players current TravelPort
     *
     * @param player the player to look up.
     * @return the players current TravelPort.
     */
    public TravelPort getPlayerCurrentTravelPort(Player player) {
        return playerInsideTravelPort.get(player);
    }

    /**
     * Sets the port the player currently is.
     *
     * @param player the player
     * @param port   the port to set
     */
    public void setPlayerCurrentTravelPort(Player player, TravelPort port) {
        if (port != null) {
            playerInsideTravelPort.put(player, port);
        } else {
            playerInsideTravelPort.remove(player);
        }
    }

    /**
     * Checks if the player is traveled recently.
     *
     * @param player the player to check.
     * @return true, if the player is traveled recently.
     */
    public boolean isTraveledRecently(Player player) {
        Boolean traveledRecently = playerTraveledRecently.get(player);
        if (traveledRecently == null || traveledRecently.equals(Boolean.FALSE)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets if the player is traveled recently.
     *
     * @param player the player.
     * @param value  the value to set.
     */
    public void setTraveledRecently(Player player, boolean value) {
        playerTraveledRecently.put(player, Boolean.valueOf(value));
    }
}
