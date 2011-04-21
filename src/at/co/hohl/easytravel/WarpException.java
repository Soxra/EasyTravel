package at.co.hohl.easytravel;

/**
 * Exception thrown, when the plugin can't warp the player because of an internal error.
 *
 * @author Michael Hohl
 */
public class WarpException extends RuntimeException {
    public WarpException() {
        super();
    }

    public WarpException(String message) {
        super(message);
    }

    public WarpException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public WarpException(Throwable throwable) {
        super(throwable);
    }
}
