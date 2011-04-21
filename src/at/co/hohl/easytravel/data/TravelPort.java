package at.co.hohl.easytravel.data;

import org.bukkit.Location;

/**
 * Represents a place, where a user could travel.
 *
 * @author Michael Hohl
 */
public class TravelPort {
    /** Unique ID of the Travel Port. */
    private final Integer id;

    /** The first Edge. */
    private Location edge1;

    /** The second Edge. */
    private Location edge2;

    /** The name of the travel port. */
    private String name;

    /** The name of the target of this port. */
    private Integer target;

    /** The password for the travel port. */
    private String password;

    /** The price to travel */
    private int price = 0;

    /** Creates a new travel port. */
    public TravelPort(Integer id) {
        this.id = id;
    }

    /** @return the unique id of the travel port. */
    public Integer getId() {
        return id;
    }

    /** @return the first edge. */
    public Location getEdge1() {
        return edge1;
    }

    /**
     * Checks if the location is inside the TravelPort.
     *
     * @param location the location to check.
     * @return true, if the location is inside the TravelPoint.
     */
    public boolean contains(Location location) {

        boolean insideX = Math.min(edge1.getBlockX(), edge2.getBlockX()) <= location.getBlockX() &&
                location.getBlockX() <= Math.max(edge1.getBlockX(), edge2.getBlockX());

        boolean insideY = Math.min(edge1.getBlockY(), edge2.getBlockY()) <= location.getBlockY() &&
                location.getBlockY() <= Math.max(edge1.getBlockY(), edge2.getBlockY());

        boolean insideZ = Math.min(edge1.getBlockZ(), edge2.getBlockZ()) - 1 <= location.getBlockZ() &&
                location.getBlockZ() <= Math.max(edge1.getBlockZ(), edge2.getBlockZ()) + 1;

        return insideX && insideY && insideZ;
    }

    /**
     * Sets the first edge.
     *
     * @param edge1 the first edge.
     */
    public void setEdge1(Location edge1) {
        this.edge1 = edge1;
    }

    /** @return the second edge. */
    public Location getEdge2() {
        return edge2;
    }

    /**
     * Sets the second edge.
     *
     * @param edge2 the second edge.
     */
    public void setEdge2(Location edge2) {
        this.edge2 = edge2;
    }

    /** @return the name of the travel point. */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the TravelPoint.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the id of the target. */
    public Integer getTarget() {
        return target;
    }

    /**
     * Sets the id of the target.
     *
     * @param target the id of the target.
     */
    public void setTarget(Integer target) {
        this.target = target;
    }

    /** @return the price it costs to travel. */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price it costs to travel.
     *
     * @param price the price it costs to travel
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /** @return the password needed to travel with this port. */
    public String getPassword() {
        return password;
    }

    /** @param password sets the password needed to travel */
    public void setPassword(String password) {
        this.password = password;
    }
}
