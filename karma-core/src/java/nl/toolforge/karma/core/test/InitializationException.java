package nl.toolforge.karma.core.test;

/**
 * Thrown when the local version control repository could not be located for tests.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class InitializationException extends RuntimeException {

	public InitializationException(String message) {
		super(message);
	}
}