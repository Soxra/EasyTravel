package at.co.hohl.easytravel.data;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents an cuboid area.
 *
 * @author Michael Hohl
 */
public class CuboidArea implements Area {
    private double highx, lowx;
    private double highy, lowy;
    private double highz, lowz;
    private String world;

    /**
     * Creates a new CuboidArea with the two passed locations as edges.
     *
     * @param edge1 the first location.
     * @param edge2 the second location.
     */
    public CuboidArea(final Location edge1, final Location edge2) {
        highx = Math.max(edge1.getX(), edge2.getX());
        lowx = Math.min(edge1.getX(), edge2.getX());

        highy = Math.max(edge1.getY(), edge2.getY());
        lowy = Math.min(edge1.getY(), edge2.getY());

        highz = Math.max(edge1.getZ(), edge2.getZ());
        lowz = Math.min(edge1.getZ(), edge2.getZ());

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
    public CuboidArea(final World world, double x1, double x2, double y1, double y2, double z1, double z2) {
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
            highx = parser.getDouble("highx");
            lowx = parser.getDouble("lowx");
            highy = parser.getDouble("highy");
            lowy = parser.getDouble("lowy");
            highz = parser.getDouble("highz");
            lowz = parser.getDouble("lowz");
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
                && (highx > location.getX() && lowx < location.getX()) // Inside X coords?
                && (highy > location.getY() && lowy < location.getY()) // Inside Y coords?
                && (highz > location.getZ() && lowz < location.getZ()); // Inside Z coords?
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
