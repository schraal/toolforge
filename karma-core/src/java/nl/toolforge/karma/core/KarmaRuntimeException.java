package nl.toolforge.karma.core;

import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Root runtime exception for <code>nl.toolforge.karma</code> classes.
 *
 * @author D.A. Smedes
 *
 * @version
 */
public class KarmaRuntimeException extends RuntimeException {

	public KarmaRuntimeException(String message) {
		super(message);
	}

	public KarmaRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
