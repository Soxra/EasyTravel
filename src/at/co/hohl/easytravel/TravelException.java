package at.co.hohl.easytravel;

/**
 * Base class for all exceptions of EasyTravel.
 *
 * @author Michael Hohl
 */
public abstract class TravelException extends Exception {
    public TravelException() {
    }

    public TravelException(String s) {
        super(s);
    }

    public TravelException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TravelException(Throwable throwable) {
        super(throwable);
    }
}
