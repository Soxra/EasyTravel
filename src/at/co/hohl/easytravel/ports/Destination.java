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

package at.co.hohl.easytravel.ports;

import at.co.hohl.utils.storage.PropertiesParser;
import at.co.hohl.utils.storage.SyntaxException;
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
    /**
     * Location of the destination.
     */
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

        if ("Destination".equals(parser.getType())) {
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
     * Teleports the passed players to the location.
     *
     * @param player the location to teleport to.
     */
    public void teleport(Player player) {
        if (location != null) {
            player.teleport(location);
        } else {
            throw new MissDestinationException("Whoops, something went wrong! Travel port destination not set!");
        }
    }

    /**
     * @return the x coord of the destination.
     */
    public double getX() {
        return location.getX();
    }

    /**
     * @return the y coord of the destination.
     */
    public double getY() {
        return location.getY();
    }

    /**
     * @return the z coord of the destination.
     */
    public double getZ() {
        return location.getZ();
    }

    /**
     * @return the location of the destination.
     */
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

    /**
     * Exception which is thrown when there is no Destination set.
     */
    public static class MissDestinationException extends RuntimeException {
        public MissDestinationException(String s) {
            super(s);
        }
    }
}
