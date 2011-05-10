package at.co.hohl.easytravel.ports;

import at.co.hohl.Permissions.PermissionsHandler;
import at.co.hohl.easytravel.TravelPlugin;
import at.co.hohl.easytravel.messages.Messages;
import at.co.hohl.easytravel.ports.depart.Departure;
import at.co.hohl.economy.EconomyHandler;
import at.co.hohl.utils.ChatHelper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: hohl Date: 10.05.11 Time: 17:54 To change this template use File | Settings | File
 * Templates.
 */
public abstract class TravelPort {
    /** Container which holds the instance. */
    protected final TravelPortContainer container;

    /** @param container container, which holds this port. */
    public TravelPort(TravelPortContainer container) {
        this.container = container;
    }

    /**
     * Sets who is allowed to use this TravelPort. If set to null, everybody is allowed to use.
     *
     * @param allowed list of Strings with names of groups and players.
     */
    public abstract void setAllowed(List<String> allowed);

    /** @return true, if this TravelPort is allowed to everybody. */
    public abstract boolean isAllowedToEverybody();

    /**
     * Checks if the players is allowed to use the TravelPort.
     *
     * @param permissions needed to check if the user is in group.
     * @param player      the players to check.
     * @return true, if the players is allowed to.
     */
    public abstract boolean isAllowed(PermissionsHandler permissions, Player player);

    /** Allows this TravelPort to everybody. */
    public abstract void setAllowedToEverybody();

    /**
     * Adds somebody to the allowed list.
     *
     * @param allowed the one to add.
     */
    public abstract void addAllowed(String allowed);

    /**
     * Removes someone from the allowed list. If the allowed list is empty, everybody is allowed to use!
     *
     * @param allowed the one to remove.
     */
    public abstract void removeAllowed(String allowed);

    /** @return the unique id of the travel port. */
    public abstract Integer getId();

    /** @return area of the port. */
    public abstract Area getArea();

    /**
     * Sets the area of the port.
     *
     * @param area the area to set.
     */
    public abstract void setArea(Area area);

    /** @return the destination */
    public abstract Destination getDestination();

    /**
     * Sets the destination.
     *
     * @param destination the destination to set.
     */
    public abstract void setDestination(Destination destination);

    /** @return the name of the travel point. */
    public abstract String getName();

    /**
     * Sets the name of the TravelPoint.
     *
     * @param name the name to set.
     */
    public abstract void setName(String name);

    /** @return the id of the target. */
    public abstract Integer getTargetId();

    /** @return the target or null if not linked to any. */
    public abstract TravelPort getTarget();

    /**
     * Sets the id of the target.
     *
     * @param targetId the id of the target.
     */
    public abstract void setTargetId(Integer targetId);

    /** @return the departure. */
    public abstract Departure getDeparture();

    /**
     * Sets the departure.
     *
     * @param departure the departure to set.
     */
    public abstract void setDeparture(Departure departure);

    /** @return the price it costs to travel. */
    public abstract double getPrice();

    /**
     * Sets the price it costs to travel.
     *
     * @param price the price it costs to travel
     */
    public abstract void setPrice(double price);

    /** @return the password needed to travel with this port. */
    public abstract String getPassword();

    /** @return true if the TravelPort is locked with a password. */
    public abstract boolean isPasswordLocked();

    /** @param password sets the password needed to travel */
    public abstract void setPassword(String password);

    /** @return a list of allowed groups and players. If null everybody is allowed to use that TravelPort. */
    public abstract List<String> getAllowed();

    /** @return owner of the port. */
    public abstract String getOwner();

    /**
     * Sets the owner of the port.
     *
     * @param owner the name of the owner to set.
     */
    public abstract void setOwner(String owner);

    /**
     * Called when departing all passed players is needed.
     *
     * @param players the player to depart.
     */
    public final void depart(final List<Player> players) {
        // Own runnable, for delaying the teleport.
        Runnable departRunnable = new Runnable() {
            public void run() {
                // Teleport all players.
                for (Player player : players) {
                    TravelPort targetPort = getTarget();

                    if (targetPort != null) {
                        targetPort.getDestination().teleport(player);

                        targetPort.onPlayerArrived(player);
                    } else {
                        ChatHelper.sendMessage(player, Messages.get("problem.miss-target"));
                    }
                }
            }
        };

        for (int index = 0; index < players.size(); ++index) {
            Player player = players.get(index);

            // Is players allowed to use TravelPort?
            if (!isAllowed(container.getPlugin().getPermissionsHandler(), player)) {
                ChatHelper.sendMessage(player, Messages.get("problem.not-allowed"));
                players.remove(players);
                --index;
                break;
            }

            // Has player enough money?
            if (getPrice() > 0) {
                EconomyHandler economyHandler = container.getPlugin().getEconomyHandler();
                if (economyHandler != null && economyHandler.pay(player.getName(), getPrice())) {
                    ChatHelper.sendMessage(player,
                            String.format(Messages.get("event.money-paid"), getPrice(), economyHandler.getCurrency()));
                } else {
                    ChatHelper.sendMessage(player, Messages.get("problem.little-money"));
                    players.remove(players);
                    --index;
                    break;
                }
            }

            // Notify all players of departing.
            if (TravelPlugin.DEPART_DELAY > 0) {
                ChatHelper.sendMessage(player, Messages.get("event.departing"));
            }
        }

        container.getServer().getScheduler()
                .scheduleAsyncDelayedTask(container.getPlugin(), departRunnable, TravelPlugin.DEPART_DELAY);
    }

    /**
     * Called when a player arrives at the TravelPort.
     *
     * @param player the player which arrives at the TravelPort.
     */
    public final void onPlayerArrived(final Player player) {
        // Save Players current location.
        container.getPlugin().getPlayerInformation(player).setCurrentPort(this);

        // Notify user about arriving.
        Runnable notifyRunnable = new Runnable() {
            public void run() {
                ChatHelper.sendMessage(player, String.format(Messages.get("event.arrived"), getName()));
            }
        };
        container.getServer().getScheduler().scheduleAsyncDelayedTask(container.getPlugin(), notifyRunnable,
                TravelPlugin.ARRIVED_NOTIFICATION_DELAY);
    }

    /**
     * Called when a player entered the TravelPort.
     *
     * @param player the player which enters the TravelPort.
     */
    public final void onPlayerEntered(final Player player) {
        if (getPrice() > 0) {
            EconomyHandler economyHandler = container.getPlugin().getEconomyHandler();
            if (economyHandler != null) {
                ChatHelper.sendMessage(player, String.format(Messages.get("greeting.paid"), getPrice(),
                        economyHandler.getCurrency()));
            } else {
                ChatHelper.sendMessage(player, Messages.get("problem.miss-economy"));
            }
        } else {
            ChatHelper.sendMessage(player, Messages.get("greeting.free"));
        }

        getDeparture().onPlayerEntered(player);
    }

    /**
     * Called when a player left the TravelPort.
     *
     * @param player the player, which left the TravelPort.
     */
    public final void onPlayerLeft(final Player player) {
        getDeparture().onPlayerLeft(player);
    }
}
