package at.co.hohl.easytravel.data;

import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.utils.StringHelper;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

    /** The file used for storing the TravelPorts. */
    private final File csvFile;

    /** Logger used for outputting debug information. */
    private final Logger logger;

    /** Contains all travel ports. */
    private final Map<Integer, TravelPort> travelPorts = new HashMap<Integer, TravelPort>();

    /** The travel port, where a player is inside. */
    private final Map<Player, TravelPort> playerInsideTravelPort = new HashMap<Player, TravelPort>();

    /** true, if the passed player traveled recently. */
    private final Map<Player, Boolean> playerTraveledRecently = new HashMap<Player, Boolean>();


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
    }

    /** Loads the TravelPorts. */
    public void load() {
        if (csvFile.exists()) {
            try {
                FlatFilePortStorage.loadPorts(server, travelPorts, csvFile);
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
            FlatFilePortStorage.savePorts(travelPorts, csvFile);
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
     * @throws at.co.hohl.easytravel.data.TravelPortNotFound
     *          thrown when there is no match for the id.
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
     * Creates a new TravelPort. (This will automatically creates an unique ID for it and adds it to storage.)
     *
     * @return the created TravelPort
     */
    public TravelPort create() {
        TravelPort createdPort = new TravelPort(findUnusedId());
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
        private static final int INDEX_WORLD = 7;
        private static final int INDEX_EDGE1_X = 8;
        private static final int INDEX_EDGE1_Y = 9;
        private static final int INDEX_EDGE1_Z = 10;
        private static final int INDEX_EDGE2_X = 11;
        private static final int INDEX_EDGE2_Y = 12;
        private static final int INDEX_EDGE2_Z = 13;
        private static final int INDEX_DESTINATION_X = 14;
        private static final int INDEX_DESTINATION_Y = 15;
        private static final int INDEX_DESTINATION_Z = 16;
        private static final int INDEX_DESTINATION_YAW = 17;
        private static final int INDEX_DESTINATION_PITCH = 18;
        private static final int CSV_COLUMNS = 19;

        /** Hidden default constructor. */
        private FlatFilePortStorage() {
            throw new RuntimeException("Don't call the hidden default constructor! " +
                    "This is a static helper class, not build for creating an instance of it.");
        }

        /**
         * Loads TravelPorts out of a file into the passed Map.
         *
         * @param server  the server used for searching the worlds.
         * @param csvFile the file to load.
         * @param ports   the Map used to store the loaded TravelPorts.
         * @throws java.io.IOException thrown when there are problems in reading the file.
         */
        static void loadPorts(Server server, Map<Integer, TravelPort> ports, File csvFile) throws IOException {
            ports.clear();

            FileReader reader = new FileReader(csvFile);
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    String[] lineParts = line.split(";");
                    if (lineParts.length == CSV_COLUMNS) {
                        TravelPort port = new TravelPort(Integer.valueOf((lineParts[INDEX_ID])));
                        port.setName(lineParts[INDEX_NAME]);
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
                        port.setPrice(Double.parseDouble(lineParts[INDEX_PRICE]));

                        World world = server.getWorld(lineParts[INDEX_WORLD]);
                        Double edge1X = Double.parseDouble(lineParts[INDEX_EDGE1_X]);
                        Double edge1Y = Double.parseDouble(lineParts[INDEX_EDGE1_Y]);
                        Double edge1Z = Double.parseDouble(lineParts[INDEX_EDGE1_Z]);
                        Location edge1 = new Location(world, edge1X, edge1Y, edge1Z);
                        port.setEdge1(edge1);

                        Double edge2X = Double.parseDouble(lineParts[INDEX_EDGE2_X]);
                        Double edge2Y = Double.parseDouble(lineParts[INDEX_EDGE2_Y]);
                        Double edge2Z = Double.parseDouble(lineParts[INDEX_EDGE2_Z]);
                        Location edge2 = new Location(world, edge2X, edge2Y, edge2Z);
                        port.setEdge2(edge2);

                        Double destinationX = Double.parseDouble(lineParts[INDEX_DESTINATION_X]);
                        Double destinationY = Double.parseDouble(lineParts[INDEX_DESTINATION_Y]);
                        Double destinationZ = Double.parseDouble(lineParts[INDEX_DESTINATION_Z]);
                        Location destination = new Location(world, destinationX, destinationY, destinationZ);
                        destination.setYaw(Float.parseFloat(lineParts[INDEX_DESTINATION_YAW]));
                        destination.setPitch(Float.parseFloat(lineParts[INDEX_DESTINATION_PITCH]));
                        port.setDestination(destination);

                        ports.put(port.getId(), port);
                    } else {
                        server.getLogger().warning(String.format("Invalid number of columns! '%s'", line));
                    }
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
         * @param travelPorts the ports to save.
         * @param csvFile     the file to save.
         * @throws java.io.IOException thrown when there is an error during writing.
         */
        static void savePorts(Map<Integer, TravelPort> travelPorts, File csvFile) throws IOException {
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
                        case INDEX_WORLD:
                            line.append(port.getEdge1().getWorld().getName());
                            break;
                        case INDEX_EDGE1_X:
                            line.append(port.getEdge1().getX());
                            break;
                        case INDEX_EDGE1_Y:
                            line.append(port.getEdge1().getY());
                            break;
                        case INDEX_EDGE1_Z:
                            line.append(port.getEdge1().getZ());
                            break;
                        case INDEX_EDGE2_X:
                            line.append(port.getEdge2().getX());
                            break;
                        case INDEX_EDGE2_Y:
                            line.append(port.getEdge2().getY());
                            break;
                        case INDEX_EDGE2_Z:
                            line.append(port.getEdge2().getZ());
                            break;
                        case INDEX_DESTINATION_X:
                            line.append(port.getDestination().getX());
                            break;
                        case INDEX_DESTINATION_Y:
                            line.append(port.getDestination().getY());
                            break;
                        case INDEX_DESTINATION_Z:
                            line.append(port.getDestination().getZ());
                            break;
                        case INDEX_DESTINATION_YAW:
                            line.append(port.getDestination().getYaw());
                            break;
                        case INDEX_DESTINATION_PITCH:
                            line.append(port.getDestination().getPitch());
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
