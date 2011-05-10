package at.co.hohl.easytravel.ports.depart;

import at.co.hohl.easytravel.ports.TravelPort;

/**
 * Static helper class for loading and saving Departures.
 *
 * @author Michael Hohl
 */
public final class DepartureHelper {
    /**
     * Loads the Departure for the passed TravelPort.
     *
     * @param port        the port to load the Departure for.
     * @param description the description of the Departure.
     * @return the loaded departure.
     */
    public static Departure load(TravelPort port, String description) {
        // ToDo: Implement more!
        return new ManualDeparture(port);
    }

    /** No need of creating an instance of that! */
    private DepartureHelper() {
    }
}
