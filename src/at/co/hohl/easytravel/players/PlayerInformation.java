package at.co.hohl.easytravel.players;

import at.co.hohl.easytravel.ports.TravelPort;

/**
 * Contains information about the players.
 *
 * @author Michael Hohl
 */
public class PlayerInformation {
    /** The current TravelPort of the players. */
    private TravelPort currentPort;

    /** The last entered password by this user. */
    private String enteredPassword;

    /** Creates a new players information. */
    public PlayerInformation() {
    }

    /** @return the current port of the player. */
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

    /** @return the entered password. */
    public String getEnteredPassword() {
        return enteredPassword;
    }

    /**
     * Sets the password entered by the player.
     *
     * @param enteredPassword the password to set.
     */
    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }
}
