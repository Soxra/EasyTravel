package at.co.hohl.easytravel.data;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Helps to loading TravelPorts out of a array of strings.
 *
 * @author Michael Hohl
 */
final class TravelPortStorage {
    private static final int INDEX_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_TARGET = 2;
    private static final int INDEX_PASSWORD = 3;
    private static final int INDEX_PRICE = 4;
    private static final int INDEX_WORLD = 5;
    private static final int INDEX_EDGE1_X = 6;
    private static final int INDEX_EDGE1_Y = 7;
    private static final int INDEX_EDGE1_Z = 8;
    private static final int INDEX_EDGE2_X = 9;
    private static final int INDEX_EDGE2_Y = 10;
    private static final int INDEX_EDGE2_Z = 11;
    private static final int CSV_COLUMNS = 12;

    /** Hidden default constructor. */
    private TravelPortStorage() {
        throw new RuntimeException("Don't call the hidden default constructor! " +
                "This is a static helper class, not build for creating an instace of it.");
    }

    /**
     * Loads TravelPorts out of a file into the passed Hashtable.
     *
     * @param server  the server used for searching the worlds.
     * @param cvsFile the file to load.
     * @param ports   the Hashtable used to store the loaded TravelPorts.
     * @throws IOException thrown when there are problems in reading the file.
     */
    static void loadPorts(Server server, Hashtable<Integer, TravelPort> ports, File cvsFile) throws IOException {
        ports.clear();

        Scanner scanner = new Scanner(new FileReader(cvsFile));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                String[] lineParts = line.split(";");
                if (lineParts.length == CSV_COLUMNS) {
                    TravelPort port = new TravelPort(new Integer(lineParts[INDEX_ID]));
                    port.setName(lineParts[INDEX_NAME]);
                    if (!"null".equals(lineParts[INDEX_TARGET])) {
                        port.setTarget(new Integer(lineParts[INDEX_TARGET]));
                    }
                    if (!"null".equals(lineParts[INDEX_PASSWORD])) {
                        port.setPassword(lineParts[INDEX_PASSWORD]);
                    }
                    port.setPrice(Integer.parseInt(lineParts[INDEX_PRICE]));

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
                } else {
                    server.getLogger().info(String.format("Invalid number of columns! '%s'", line));
                }
            } catch (NumberFormatException e) {
                server.getLogger().info(String.format("Invalid TravelPort configuration line! '%s'", line));
            }
        }
        scanner.close();

        server.getLogger().info(String.format("Loaded %d TravelPorts!", ports.size()));
    }

    /**
     * Puts the TravelPorts into a csv file.
     *
     * @param travelPorts the ports to save.
     * @param csvFile     the file to save.
     * @throws IOException thrown when there is an error during writing.
     */
    static void savePorts(Hashtable<Integer, TravelPort> travelPorts, File csvFile) throws
            IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

        for (TravelPort port : travelPorts.values()) {
            StringBuilder line = new StringBuilder();

            for (int index = 0; index < CSV_COLUMNS; ++index) {
                switch (index) {
                    case INDEX_ID:
                        line.append(port.getId());
                        break;
                    case INDEX_NAME:
                        line.append(port.getName());
                        break;
                    case INDEX_TARGET:
                        line.append(port.getTarget());
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
                        line.append(port.getEdge1().getBlockX());
                        break;
                    case INDEX_EDGE1_Y:
                        line.append(port.getEdge1().getBlockY());
                        break;
                    case INDEX_EDGE1_Z:
                        line.append(port.getEdge1().getBlockZ());
                        break;
                    case INDEX_EDGE2_X:
                        line.append(port.getEdge2().getBlockX());
                        break;
                    case INDEX_EDGE2_Y:
                        line.append(port.getEdge2().getBlockY());
                        break;
                    case INDEX_EDGE2_Z:
                        line.append(port.getEdge2().getBlockY());
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
