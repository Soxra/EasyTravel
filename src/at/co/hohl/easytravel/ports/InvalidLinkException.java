package at.co.hohl.easytravel.ports;

import at.co.hohl.easytravel.TravelException;

/**
 * Exception for invalid links.
 *
 * @author Michael Hohl
 */
public class InvalidLinkException extends TravelException {
    public InvalidLinkException() {
        super();
    }

    public InvalidLinkException(String message) {
        super(message);
    }
}
