package at.co.hohl.easytravel.ports.implementation.file;

import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.TravelPermissions;
import at.co.hohl.easytravel.ports.*;
import at.co.hohl.easytravel.ports.depart.Departure;
import at.co.hohl.easytravel.ports.depart.ManualDeparture;
import at.co.hohl.easytravel.ports.implementation.AbstractTravelPort;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a place, where a user could travel.
 *
 * @author Michael Hohl
 */
public class FlatFileTravelPort extends AbstractTravelPort {
    /** Unique ID of the Travel Port. */
    private final Integer id;

    /** Name of the travel port. */
    private String name;

    /** Area of the port. */
    private Area area;

    /** The destination. */
    private Destination destination;

    /** The departure handler. */
    private Departure departure = new ManualDeparture(this);

    /** Name of the target of this port. */
    private Integer targetId;

    /** Password for the travel port. */
    private String password;

    /** Owner of the travel port. */
    private String owner;

    /** List of allowed Groups and Players. */
    private List<String> allowed = new LinkedList<String>();

    /** The price to travel */
    private double price;

    /**
     * Creates a new travel port.
     *
     * @param container container which holds the instance.
     * @param id        id used for this port.
     */
    public FlatFileTravelPort(TravelPortContainer container, Integer id) {
        super(container);

        this.id = id;
    }

    /**
     * Sets who is allowed to use this TravelPort. If set to null, everybody is allowed to use.
     *
     * @param allowed list of Strings with names of groups and players.
     */
    @Override
    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

    /** @return true, if this TravelPort is allowed to everybody. */
    @Override
    public boolean isAllowedToEverybody() {
        if (allowed == null || allowed.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the players is allowed to use the TravelPort.
     *
     * @param permissions needed to check if the user is in group.
     * @param player      the players to check.
     * @return true, if the players is allowed to.
     */
    @Override
    public boolean isAllowed(PermissionsHandler permissions, Player player) {
        if (permissions.hasPermission(player, TravelPermissions.DEPART)) {
            if (isAllowedToEverybody() || allowed.contains(player.getName())) {
                return true;
            } else {
                String group = permissions.getGroup(player);
                return allowed.contains(group);
            }
        } else {
            return false;
        }
    }

    /** Allows this TravelPort to everybody. */
    @Override
    public void setAllowedToEverybody() {
        allowed.clear();
    }

    /**
     * Adds somebody to the allowed list.
     *
     * @param allowed the one to add.
     */
    @Override
    public void addAllowed(String allowed) {
        this.allowed.add(allowed.trim());
    }

    /**
     * Removes someone from the allowed list. If the allowed list is empty, everybody is allowed to use!
     *
     * @param allowed the one to remove.
     */
    @Override
    public void removeAllowed(String allowed) {
        this.allowed.remove(allowed);
    }

    /** @return the departure. */
    @Override
    public Departure getDeparture() {
        return departure;
    }

    /**
     * Sets the departure.
     *
     * @param departure the departure to set.
     */
    @Override
    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    /** @return the unique id of the travel port. */
    @Override
    public final Integer getId() {
        return id;
    }

    /** @return area of the port. */
    @Override
    public Area getArea() {
        return area;
    }

    /**
     * Sets the area of the port.
     *
     * @param area the area to set.
     */
    @Override
    public void setArea(Area area) {
        this.area = area;
    }

    /** @return the destination */
    @Override
    public Destination getDestination() {
        return destination;
    }

    /**
     * Sets the destination.
     *
     * @param destination the destination to set.
     */
    @Override
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /** @return the name of the travel point. */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the TravelPoint.
     *
     * @param name the name to set.
     */
    @Override
    public final void setName(String name) {
        this.name = name;
    }

    /** @return the id of the target. */
    @Override
    public final Integer getTargetId() {
        return targetId;
    }

    /** @return the target or null if not linked to any. */
    @Override
    public TravelPort getTarget() {
        try {
            return container.get(targetId);
        } catch (TravelPortNotFound e) {
            return null;
        }
    }

    /**
     * Sets the id of the target.
     *
     * @param targetId the id of the target.
     */
    @Override
    public final void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    /** @return the price it costs to travel. */
    @Override
    public final double getPrice() {
        return price;
    }

    /**
     * Sets the price it costs to travel.
     *
     * @param price the price it costs to travel
     */
    @Override
    public final void setPrice(double price) {
        this.price = price;
    }

    /** @return the password needed to travel with this port. */
    @Override
    public final String getPassword() {
        return password;
    }

    /** @return true if the TravelPort is locked with a password. */
    @Override
    public final boolean isPasswordLocked() {
        return password != null;
    }

    /** @param password sets the password needed to travel */
    @Override
    public final void setPassword(String password) {
        this.password = password;
    }

    /** @return a list of allowed groups and players. If null everybody is allowed to use that TravelPort. */
    @Override
    public List<String> getAllowed() {
        return allowed;
    }

    /** @return owner of the port. */
    @Override
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the port.
     *
     * @param owner the name of the owner to set.
     */
    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
