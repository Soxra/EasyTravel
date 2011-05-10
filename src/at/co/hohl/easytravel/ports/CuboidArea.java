package at.co.hohl.easytravel.ports;

import at.co.hohl.utils.storage.PropertiesParser;
import at.co.hohl.utils.storage.SyntaxException;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents an cuboid area.
 *
 * @author Michael Hohl
 */
public class CuboidArea implements Area {
    private int highx, lowx;
    private int highy, lowy;
    private int highz, lowz;
    private String world;

    /**
     * Creates a new CuboidArea with the two passed locations as edges.
     *
     * @param edge1 the first location.
     * @param edge2 the second location.
     */
    public CuboidArea(final Location edge1, final Location edge2) {
        highx = Math.max(edge1.getBlockX(), edge2.getBlockX());
        lowx = Math.min(edge1.getBlockX(), edge2.getBlockX());

        highy = Math.max(edge1.getBlockY(), edge2.getBlockY());
        lowy = Math.min(edge1.getBlockY(), edge2.getBlockY());

        highz = Math.max(edge1.getBlockZ(), edge2.getBlockZ());
        lowz = Math.min(edge1.getBlockZ(), edge2.getBlockZ());

        world = edge1.getWorld().getName();
    }

    /**
     * Creates a new CuboidArea with the passed coordinates.
     *
     * @param world the world in which the area is inside.
     * @param x1    coord
     * @param x2    coord
     * @param y1    coord
     * @param y2    coord
     * @param z1    coord
     * @param z2    coord
     */
    public CuboidArea(final World world, int x1, int x2, int y1, int y2, int z1, int z2) {
        highx = Math.max(x1, x2);
        lowx = Math.min(x1, x2);

        highy = Math.max(y1, y2);
        lowy = Math.min(y1, y2);

        highz = Math.max(z1, z2);
        lowz = Math.min(z1, z2);

        this.world = world.getName();
    }

    /**
     * Creates a new CuboidArea out of a String containing information.
     *
     * @param string the string containing the information.
     */
    public CuboidArea(final String string) throws SyntaxException {
        PropertiesParser parser = new PropertiesParser(string);

        if ("CuboidArea".equals(parser.getType())) {
            highx = parser.getInt("highx");
            lowx = parser.getInt("lowx");
            highy = parser.getInt("highy");
            lowy = parser.getInt("lowy");
            highz = parser.getInt("highz");
            lowz = parser.getInt("lowz");
            world = parser.getString("world");
        } else {
            throw new SyntaxException("String isn't a valid description for a CuboidArea!");
        }
    }

    /**
     * Checks if the passed location is inside the area.
     *
     * @param location the location to check.
     * @return true, if the passed location is inside this area.
     */
    public boolean contains(final Location location) {
        return world.equalsIgnoreCase(location.getWorld().getName()) // Same World?
                && (highx >= location.getBlockX() && lowx <= location.getBlockX()) // Inside X coords?
                && (highy >= location.getBlockY() && lowy <= location.getBlockY()) // Inside Y coords?
                && (highz >= location.getBlockZ() && lowz <= location.getBlockZ()); // Inside Z coords?
    }

    @Override
    public String toString() {
        return "CuboidArea{" +
                "highx=" + highx +
                ", lowx=" + lowx +
                ", highy=" + highy +
                ", lowy=" + lowy +
                ", highz=" + highz +
                ", lowz=" + lowz +
                ", world=" + world +
                '}';
    }
}
