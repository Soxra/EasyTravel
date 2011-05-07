package at.co.hohl.easytravel.data;

import at.co.hohl.easytravel.TravelException;

/**
 * Called when EasyTravel isn't able to restore data on reload.s
 *
 * @author Michael Hohl
 */
public class SyntaxException extends TravelException {
    public SyntaxException() {
    }

    public SyntaxException(String s) {
        super(s);
    }

    public SyntaxException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SyntaxException(Throwable throwable) {
        super(throwable);
    }
}
