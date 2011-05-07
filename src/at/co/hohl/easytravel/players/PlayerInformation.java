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

    /** True, if the players is already at the target. */
    private boolean alreadyTravelled;

    /** The last entered password by this user. */
    private String enteredPassword;

    /** Creates a new players information. */
    public PlayerInformation() {
    }

    public boolean isAlreadyTravelled() {
        return alreadyTravelled;
    }

    public void setAlreadyTravelled(boolean alreadyTravelled) {
        this.alreadyTravelled = alreadyTravelled;
    }

    public TravelPort getCurrentPort() {
        return currentPort;
    }

    public void setCurrentPort(TravelPort currentPort) {
        this.currentPort = currentPort;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }

    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }
}
