package nl.toolforge.karma.core.vc.cvs;

/**
 * Thrown when the local CVS repository could not be located for CVS tests.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class InitializationException extends RuntimeException {

	public InitializationException(String message) {
		super(message);
	}
}