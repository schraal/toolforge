package nl.toolforge.karma.core;

/**
 * Root runtime exception for <code>nl.toolforge.karma</code> classes.
 *
 * @author D.A. Smedes
 */
public class KarmaRuntimeException extends RuntimeException {

    public KarmaRuntimeException(String message) {
        super(message);
    }

    public KarmaRuntimeException(Throwable t) {
        super(t);
    }
}
