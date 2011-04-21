package at.co.hohl.easytravel.data;

import org.bukkit.command.CommandException;

/**
 * Exception which is thrown when the passed id doesn't match any port.
 *
 * @author Michael Hohl
 */
public class InvalidPortIdException extends CommandException {
    public InvalidPortIdException() {
        super("Passed TravelPort ID doesn't match any ports!");
    }
}
