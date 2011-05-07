package at.co.hohl.easytravel.ports;

import org.bukkit.Location;

/**
 * Represents an 3D area of blocks.
 *
 * @author Michael Hohl
 */
public interface Area {
    /**
     * Checks if the passed location is inside the area.
     *
     * @param location the location to check.
     * @return true, if the passed location is inside this area.
     */
    boolean contains(Location location);
}
