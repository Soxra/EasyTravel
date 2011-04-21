package at.co.hohl.easytravel.exceptions;

/**
 * Exception for invalid links.
 *
 * @author Michael Hohl
 */
public class InvalidLinkException extends Exception {
    public InvalidLinkException() {
        super();
    }

    public InvalidLinkException(String message) {
        super(message);
    }
}
