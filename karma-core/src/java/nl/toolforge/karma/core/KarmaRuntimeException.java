package nl.toolforge.karma.core;


/**
 * Root runtime exception for <code>nl.toolforge.karma</code> classes.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class KarmaRuntimeException extends RuntimeException {

	public KarmaRuntimeException(String message) {
		super(message);
	}

	public KarmaRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
