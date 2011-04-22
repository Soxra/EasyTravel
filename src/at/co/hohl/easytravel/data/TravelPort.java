package at.co.hohl.easytravel.data;

import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.messages.Speaker;
import org.bukkit.Location;

/**
 * Represents a place, where a user could travel.
 *
 * @author Michael Hohl
 */
public class TravelPort {
    private static final Speaker DEFAULT_SPEAKER = new Speaker(Messages.defaultSpeakerName);

    /** Unique ID of the Travel Port. */
    private final Integer id;

    /** The first Edge. */
    private Location edge1;

    /** The second Edge. */
    private Location edge2;

    /** The name of the travel port. */
    private String name;

    /** The name of the target of this port. */
    private Integer targetId;

    /** The password for the travel port. */
    private String password;

    /** The price to travel */
    private double price;

    /** The speaker. */
    private Speaker speaker;

    /**
     * Creates a new travel port.
     *
     * @param id the id used for this port.
     */
    public TravelPort(Integer id) {
        this.id = id;
    }

    /**
     * Checks if the location is inside the TravelPort.
     *
     * @param location the location to check.
     * @return true, if the location is inside the TravelPoint.
     */
    public final boolean contains(Location location) {

        boolean insideX = Math.min(edge1.getBlockX(), edge2.getBlockX()) <= location.getBlockX() &&
                location.getBlockX() <= Math.max(edge1.getBlockX(), edge2.getBlockX());

        boolean insideY = Math.min(edge1.getBlockY(), edge2.getBlockY()) <= location.getBlockY() &&
                location.getBlockY() <= Math.max(edge1.getBlockY(), edge2.getBlockY());

        boolean insideZ = Math.min(edge1.getBlockZ(), edge2.getBlockZ()) - 1 <= location.getBlockZ() &&
                location.getBlockZ() <= Math.max(edge1.getBlockZ(), edge2.getBlockZ()) + 1;

        return insideX && insideY && insideZ;
    }

    /** @return the unique id of the travel port. */
    public final Integer getId() {
        return id;
    }

    /** @return the first edge. */
    public final Location getEdge1() {
        return edge1;
    }

    /**
     * Sets the first edge.
     *
     * @param edge1 the first edge.
     */
    public final void setEdge1(Location edge1) {
        this.edge1 = edge1;
    }

    /** @return the second edge. */
    public final Location getEdge2() {
        return edge2;
    }

    /**
     * Sets the second edge.
     *
     * @param edge2 the second edge.
     */
    public final void setEdge2(Location edge2) {
        this.edge2 = edge2;
    }

    /** @return the name of the travel point. */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the TravelPoint.
     *
     * @param name the name to set.
     */
    public final void setName(String name) {
        this.name = name;
    }

    /** @return the id of the target. */
    public final Integer getTargetId() {
        return targetId;
    }

    /**
     * Sets the id of the target.
     *
     * @param targetId the id of the target.
     */
    public final void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    /** @return the price it costs to travel. */
    public final double getPrice() {
        return price;
    }

    /**
     * Sets the price it costs to travel.
     *
     * @param price the price it costs to travel
     */
    public final void setPrice(double price) {
        this.price = price;
    }

    /** @return the password needed to travel with this port. */
    public final String getPassword() {
        return password;
    }

    /** @param password sets the password needed to travel */
    public final void setPassword(String password) {
        this.password = password;
    }

    /** @return the speaker or the default speaker if none set. */
    public final Speaker getSpeaker() {
        if (speaker != null) {
            return speaker;
        } else {
            return DEFAULT_SPEAKER;
        }
    }

    /**
     * Sets the Speaker.
     *
     * @param speaker the speaker to set.
     */
    public final void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    /** @return true, if the default speaker is set. */
    public final boolean isDefaultSpeaker() {
        return speaker == null;
    }
}
