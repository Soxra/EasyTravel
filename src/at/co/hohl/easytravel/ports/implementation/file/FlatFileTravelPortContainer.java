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

package at.co.hohl.easytravel.ports.implementation.file;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.ports.*;
import at.co.hohl.easytravel.ports.depart.DepartureHelper;
import at.co.hohl.utils.StringHelper;
import at.co.hohl.utils.storage.CsvLineParser;
import at.co.hohl.utils.storage.SyntaxException;
import org.bukkit.Location;
import org.bukkit.Server;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a container for the TravelPorts.
 *
 * @author Michael Hohl
 */
public class FlatFileTravelPortContainer implements TravelPortContainer {
    // Constants for loading/saving
    private static final int INDEX_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_TARGET = 2;
    private static final int INDEX_OWNER = 3;
    private static final int INDEX_ALLOWED = 4;
    private static final int INDEX_PASSWORD = 5;
    private static final int INDEX_PRICE = 6;
    private static final int INDEX_AREA = 7;
    private static final int INDEX_DESTINATION = 8;
    private static final int INDEX_DEPARTURE = 9;
    private static final int CSV_COLUMNS = 10;

    /** The plugin which holds this instance. */
    private final Server server;

    /** Plugin which holds the container. */
    private final TravelPlugin plugin;

    /** The file used for storing the TravelPorts. */
    private final File csvFile;

    /** Logger used for outputting debug information. */
    private final Logger logger;

    /** Contains all travel ports. */
    private final Map<Integer, TravelPort> travelPorts = new HashMap<Integer, TravelPort>();


    /**
     * Creates a new container for TravelPorts.
     *
     * @param plugin  the plugin which holds the instance.
     * @param csvFile the fle used for storing the TravelPorts.
     */
    public FlatFileTravelPortContainer(TravelPlugin plugin, File csvFile) {
        this.csvFile = csvFile;
        this.logger = plugin.getLogger();
        this.server = plugin.getServer();
        this.plugin = plugin;
    }

    /**
     * Gets the travel port with the passed id.
     *
     * @param id the id of the travel port to get.
     * @return the travel port.
     *
     * @throws TravelPortNotFound thrown when there isn't any port with the passed id.
     */
    public TravelPort get(Integer id) throws TravelPortNotFound {
        if (id != null && travelPorts.containsKey(id)) {
            return travelPorts.get(id);
        } else {
            throw new TravelPortNotFound();
        }
    }

    /** @return list of TravelPorts. */
    public Collection<TravelPort> getAll() {
        return travelPorts.values();
    }

    /**
     * Searches a TravelPort.
     *
     * @param id could be a part of the name or the id.
     * @return the founded TravelPort.
     *
     * @throws TravelPortNotFound thrown when there is no match for the id.
     */
    public TravelPort search(String id) throws TravelPortNotFound {
        try {
            Integer portId = Integer.valueOf(id);
            return get(portId);
        } catch (Exception exception) {
            List<TravelPort> foundedResults = new LinkedList<TravelPort>();
            for (TravelPort port : travelPorts.values()) {
                if (port.getName().toLowerCase().contains(id.toLowerCase())) {
                    foundedResults.add(port);
                }
            }

            if (foundedResults.size() == 1) {
                return foundedResults.get(0);
            } else {
                throw new TravelPortNotFound();
            }
        }
    }

    /**
     * Searches a TravelPort at the Location.
     *
     * @param location the location to search for.
     * @return search result.
     */
    public Collection<TravelPort> search(Location location) {
        Collection<TravelPort> ports = plugin.getTravelPorts().getAll();
        Collection<TravelPort> result = new LinkedList<TravelPort>();

        for (TravelPort port : ports) {
            if (port.getArea().contains(location)) {
                result.add(port);
            }
        }

        return result;
    }

    /**
     * Searches the TravelPorts.
     *
     * @param keyword could be a part of the name of the TravelPort to search.
     * @return all TravelPorts matching the keyword.
     */
    public Collection<TravelPort> searchAll(String keyword) {
        List<TravelPort> result = new LinkedList<TravelPort>();

        for (TravelPort port : travelPorts.values()) {
            if (port.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(port);
            }
        }

        return result;
    }

    /**
     * Creates a new TravelPort. (This will automatically creates an unique ID for it and adds it to implementation.)
     *
     * @return the created TravelPort
     */
    public TravelPort create() {
        TravelPort createdPort = new FlatFileTravelPort(this, findUnusedId());
        add(createdPort);
        return createdPort;
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
        if (port.getTargetId() != null) {
            try {
                unlink(port);
            } catch (InvalidLinkException exception) {
                logger.severe("Internal unexpected error occurred! Seems to be a multithread problem");
            }
        }

        travelPorts.remove(port.getId());
    }

    /**
     * Links the passed TravelPorts.
     *
     * @param port1 the first port to link.
     * @param port2 another port to link.
     * @throws InvalidLinkException thrown when the ports are already linked.
     */
    public void link(TravelPort port1, TravelPort port2) throws InvalidLinkException {
        if (port1.getTargetId() == null && port2.getTargetId() == null) {
            port1.setTargetId(port2.getId());
            port2.setTargetId(port1.getId());
        } else {
            throw new InvalidLinkException("Ports are already linked!");
        }
    }

    /**
     * Unlink the passed TravelPort
     *
     * @param port the port to unlink
     * @throws InvalidLinkException thrown when port isn't linked to another.
     */
    public void unlink(TravelPort port) throws InvalidLinkException {
        if (port.getTargetId() != null) {
            try {
                TravelPort anotherPort = get(port.getTargetId());
                anotherPort.setTargetId(null);
            } catch (TravelPortNotFound exception) {
                logger.warning("TravelPort wasn't linked correctly!");
            }

            port.setTargetId(null);

        } else {
            throw new InvalidLinkException("Can't unlink ports, which aren't linked!");
        }
    }

    /** @return number of available entries. */
    public int size() {
        return travelPorts.size();
    }

    /** @return the server which holds the container. */
    public Server getServer() {
        return server;
    }

    /** @return the plugin, which holds the container. */
    public TravelPlugin getPlugin() {
        return plugin;
    }

    /** Loads the TravelPorts. */
    public void load() {
        if (csvFile.exists()) {
            try {
                // Remove TravelPorts in RAM before!
                travelPorts.clear();

                // Parse the CSV file.
                FileReader reader = new FileReader(csvFile);
                Scanner scanner = new Scanner(reader);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        CsvLineParser parser = new CsvLineParser(line);
                        TravelPort port = new FlatFileTravelPort(this, parser.getInt(INDEX_ID));
                        port.setName(parser.getString(INDEX_NAME));
                        port.setPrice(parser.getDouble(INDEX_PRICE));
                        port.setTargetId(parser.getInteger(INDEX_TARGET));
                        port.setPassword(parser.getString(INDEX_PASSWORD));
                        port.setOwner(parser.getString(INDEX_OWNER));
                        port.setAllowed(StringHelper.decode(parser.getString(INDEX_ALLOWED)));
                        port.setDeparture(DepartureHelper.load(port, parser.getString(INDEX_DEPARTURE)));

                        String areaString = parser.getString(INDEX_AREA);
                        if (areaString != null) {
                            port.setArea(new CuboidArea(areaString));
                        }

                        String destinationString = parser.getString(INDEX_DESTINATION);
                        if (destinationString != null) {
                            port.setDestination(new Destination(server, destinationString));
                        }

                        travelPorts.put(port.getId(), port);
                    } catch (SyntaxException e) {
                        server.getLogger().warning(
                                String.format("Corrupt TravelPort configuration line! '%s'", line));
                        server.getLogger().info("Exception: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        server.getLogger().warning(String.format("Invalid TravelPort configuration line! '%s'", line));
                    }
                }
                scanner.close();
                reader.close();

                server.getLogger().info(String.format("Loaded %d TravelPorts!", travelPorts.size()));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

            for (TravelPort port : travelPorts.values()) {
                StringBuilder line = new StringBuilder();

                for (int index = 0; index < CSV_COLUMNS; ++index) {
                    switch (index) {
                        case INDEX_ID:
                            line.append(port.getId());
                            break;
                        case INDEX_NAME:
                            line.append(port.getName().replace(";", "&#59"));
                            break;
                        case INDEX_TARGET:
                            line.append(port.getTargetId());
                            break;
                        case INDEX_OWNER:
                            line.append(port.getOwner());
                            break;
                        case INDEX_ALLOWED:
                            if (port.isAllowedToEverybody()) {
                                line.append("null");
                            } else {
                                line.append(StringHelper.encode(port.getAllowed()));
                            }
                            break;
                        case INDEX_PASSWORD:
                            line.append(port.getPassword());
                            break;
                        case INDEX_PRICE:
                            line.append(port.getPrice());
                            break;
                        case INDEX_AREA:
                            line.append(port.getArea().toString());
                            break;
                        case INDEX_DESTINATION:
                            line.append(port.getDestination().toString());
                            break;
                        case INDEX_DEPARTURE:
                            line.append(port.getDeparture().toString());
                            break;
                        default:
                            throw new RuntimeException(
                                    "This Code should never get reached! Error in program, please contact the developer.");
                    }

                    if (index + 1 < CSV_COLUMNS) {
                        line.append(";");
                    }
                }

                writer.append(line.toString());
                writer.newLine();
            }

            writer.close();
        } catch (IOException exception) {
            logger.severe("Error occurred during saving TravelPorts!");
            logger.severe(exception.getMessage());
        }
    }

    /** @return the next free travel port id. */
    private Integer findUnusedId() {
        Integer currentId = Integer.valueOf(0);

        while (travelPorts.containsKey(currentId)) {
            currentId++;
        }

        return currentId;
    }
}
