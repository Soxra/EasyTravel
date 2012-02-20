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

package at.co.hohl.easytravel;

import at.co.hohl.easytravel.ports.TravelPort;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains information about the players.
 *
 * @author Michael Hohl
 */
public class PlayerInformation {
    /**
     * Maximum number of passwords stored for each player.
     */
    private static final int MAX_STORED_PASSWORDS = 3;

    /**
     * Player which is the owner of the information.
     */
    private final Player player;

    /**
     * The current TravelPort of the players.
     */
    private TravelPort currentPort;

    /**
     * The last entered password by this user.
     */
    private List<String> enteredPasswords = new LinkedList<String>();

    /**
     * Creates a new players information.
     */
    public PlayerInformation(Player player) {
        this.player = player;
    }

    /**
     * @return player which holds the information.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return true, if the player is currently inside a TravelPort.
     */
    public boolean isInsideTravelPort() {
        return currentPort != null;
    }

    /**
     * @return the current port of the player.
     */
    public TravelPort getCurrentPort() {
        return currentPort;
    }

    /**
     * Sets the port the player is currently inside.
     *
     * @param currentPort the port to set.
     */
    public void setCurrentPort(TravelPort currentPort) {
        this.currentPort = currentPort;
    }

    /**
     * Checks if the user has entered the passed password.
     *
     * @param password the password to check.
     * @return true if the user entered the password.
     */
    public boolean hasPassword(String password) {
        for (String enteredPassword : enteredPasswords) {
            if (enteredPassword.equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the password entered by the player.
     *
     * @param password the password to set.
     */
    public void addPassword(String password) {
        // To much saved passwords? Remove one...
        if (enteredPasswords.size() > MAX_STORED_PASSWORDS) {
            enteredPasswords.remove(0);
        }

        enteredPasswords.add(password);
    }
}
