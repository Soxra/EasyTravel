package at.co.hohl.easytravel.ports;

import at.co.hohl.easytravel.TravelException;

/**
 * Exception which is thrown when the passed id doesn't match any port.
 *
 * @author Michael Hohl
 */
public class TravelPortNotFound extends TravelException {
    public TravelPortNotFound() {
        super("Passed ID doesn't match any TravelPort!");
    }
}
