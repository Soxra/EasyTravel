package at.co.hohl.easytravel.ports.impl;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.ports.*;
import at.co.hohl.easytravel.storage.SyntaxException;
import at.co.hohl.utils.StringHelper;
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

    /** Loads the TravelPorts. */
    public void load() {
        if (csvFile.exists()) {
            try {
                FlatFilePortStorage.loadPorts(server, this, csvFile);
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
            FlatFilePortStorage.savePorts(this, csvFile);
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
     * @throws TravelPortNotFound thrown when there isn't any port with the passed id.
     */
    public TravelPort get(Integer id) throws TravelPortNotFound {
        if (id != null && travelPorts.containsKey(id)) {
            return travelPorts.get(id);
        } else {
            throw new TravelPortNotFound();
        }
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

    /** @return list of TravelPorts. */
    public Collection<TravelPort> getAll() {
        return travelPorts.values();
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
     * Creates a new TravelPort. (This will automatically creates an unique ID for it and adds it to impl.)
     *
     * @return the created TravelPort
     */
    public TravelPort create() {
        TravelPort createdPort = new SimpleTravelPort(this, findUnusedId());
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

    /** @return the next free travel port id. */
    private Integer findUnusedId() {
        Integer currentId = Integer.valueOf(0);

        while (travelPorts.containsKey(currentId)) {
            currentId++;
        }

        return currentId;
    }

    /** Load and saves TravelPorts out of a flat file. */
    private static final class FlatFilePortStorage {
        private static final int INDEX_ID = 0;
        private static final int INDEX_NAME = 1;
        private static final int INDEX_TARGET = 2;
        private static final int INDEX_OWNER = 3;
        private static final int INDEX_ALLOWED = 4;
        private static final int INDEX_PASSWORD = 5;
        private static final int INDEX_PRICE = 6;
        private static final int INDEX_AREA = 7;
        private static final int INDEX_DESTINATION = 8;
        private static final int CSV_COLUMNS = 9;

        /** Hidden default constructor. */
        private FlatFilePortStorage() {
            throw new RuntimeException("Don't call the hidden default constructor! " +
                    "This is a static helper class, not build for creating an instance of it.");
        }

        /**
         * Loads TravelPorts out of a file into the passed Map.
         *
         * @param server    the server used for searching the worlds.
         * @param csvFile   the file to load.
         * @param container the container to load into.
         * @throws java.io.IOException thrown when there are problems in reading the file.
         */
        static void loadPorts(Server server, FlatFileTravelPortContainer container, File csvFile) throws IOException {
            Map<Integer, TravelPort> ports = container.travelPorts;
            ports.clear();

            FileReader reader = new FileReader(csvFile);
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    String[] lineParts = line.split(";");
                    if (lineParts.length == CSV_COLUMNS) {
                        TravelPort port =
                                new SimpleTravelPort(container, Integer.valueOf((lineParts[INDEX_ID])));
                        port.setName(lineParts[INDEX_NAME]);
                        port.setPrice(Double.parseDouble(lineParts[INDEX_PRICE]));

                        if (!"null".equals(lineParts[INDEX_TARGET])) {
                            port.setTargetId(Integer.valueOf((lineParts[INDEX_TARGET])));
                        }
                        if (!"null".equals(lineParts[INDEX_OWNER])) {
                            port.setOwner(lineParts[INDEX_OWNER]);
                        }
                        if (!"null".equals(lineParts[INDEX_PASSWORD])) {
                            port.setPassword(lineParts[INDEX_PASSWORD]);
                        }
                        if (!"null".equals(lineParts[INDEX_ALLOWED])) {
                            port.setAllowed(StringHelper.decode(lineParts[INDEX_ALLOWED]));
                        }
                        if (!"null".equals(lineParts[INDEX_AREA])) {
                            port.setArea(new CuboidArea(lineParts[INDEX_AREA]));
                        }
                        if (!"null".equals(lineParts[INDEX_DESTINATION])) {
                            port.setDestination(new Destination(server, lineParts[INDEX_DESTINATION]));
                        }

                        ports.put(port.getId(), port);
                    } else {
                        server.getLogger().warning(String.format("Invalid number of columns! '%s'", line));
                    }
                } catch (SyntaxException e) {
                    server.getLogger()
                            .warning(String.format("Syntax exception in TravelPort configuration line! '%s'", line));
                    server.getLogger().info("Exception: " + e.getMessage());
                } catch (NumberFormatException e) {
                    server.getLogger().warning(String.format("Invalid TravelPort configuration line! '%s'", line));
                }
            }
            scanner.close();
            reader.close();

            server.getLogger().info(String.format("Loaded %d TravelPorts!", ports.size()));
        }

        /**
         * Puts the TravelPorts into a csv file.
         *
         * @param container the container to save.
         * @param csvFile   the file to save.
         * @throws java.io.IOException thrown when there is an error during writing.
         */
        static void savePorts(FlatFileTravelPortContainer container, File csvFile) throws IOException {
            Map<Integer, TravelPort> travelPorts = container.travelPorts;
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

            for (TravelPort port : travelPorts.values()) {
                StringBuilder line = new StringBuilder();

                for (int index = 0; index < CSV_COLUMNS; ++index) {
                    switch (index) {
                        case INDEX_ID:
                            line.append(port.getId());
                            break;
                        case INDEX_NAME:
                            line.append(port.getName().replace(';', ','));
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
        }
    }
}
