package at.co.hohl.easytravel.data;

/**
 * Exception which is thrown when the passed id doesn't match any port.
 *
 * @author Michael Hohl
 */
public class TravelPortNotFound extends Exception {
    public TravelPortNotFound() {
        super("Passed ID doesn't match any TravelPort!");
    }
}
