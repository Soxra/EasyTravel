package at.co.hohl.easytravel.data;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Represents a destination for a TravelPort.
 *
 * @author Michael Hohl
 */
public class Destination {
    /** Location of the destination. */
    private final Location location;

    /**
     * Creates a destination with the passed Location.
     *
     * @param location the location.
     */
    public Destination(final Location location) {
        this.location = location;
    }

    /**
     * Creates a Destination with the passed description.
     *
     * @param server      the server which wants to load the destination.
     * @param description the string which contains all information.
     * @throws SyntaxException thrown when the description isn't formatted valid.
     */
    public Destination(final Server server, final String description) throws SyntaxException {
        PropertiesParser parser = new PropertiesParser(description);

        if ("CuboidArea".equals(parser.getType())) {
            double locx = parser.getDouble("locx");
            double locy = parser.getDouble("locy");
            double locz = parser.getDouble("locz");
            float pitch = parser.getFloat("pitch");
            float yaw = parser.getFloat("yaw");
            String worldName = parser.getString("world");

            if (worldName != null) {
                World world = server.getWorld(worldName);
                location = new Location(world, locx, locy, locz);
                location.setPitch(pitch);
                location.setYaw(yaw);
            } else {
                throw new SyntaxException("No world property found!");
            }
        } else {
            throw new SyntaxException("String isn't a valid description for a Destination!");
        }
    }

    /**
     * Teleports the passed player to the location.
     *
     * @param player the location to teleport to.
     */
    public void teleport(Player player) {
        player.teleport(location);
    }

    /** @return the x coord of the destination. */
    public double getX() {
        return location.getX();
    }

    /** @return the y coord of the destination. */
    public double getY() {
        return location.getY();
    }

    /** @return the z coord of the destination. */
    public double getZ() {
        return location.getZ();
    }

    /** @return the location of the destination. */
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "locx=" + location.getX() +
                ", locy=" + location.getY() +
                ", locz=" + location.getZ() +
                ", world=" + location.getWorld().getName() +
                ", pitch=" + location.getPitch() +
                ", yaw=" + location.getYaw() +
                '}';
    }
}
