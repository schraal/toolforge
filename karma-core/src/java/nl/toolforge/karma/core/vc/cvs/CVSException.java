package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Exceptions related to CVS stuff.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class CVSException extends KarmaException {

	/** When no valid <code>CVSROOT</code> could be compiled from <code>CVSLocationImpl</code> instance variables. */
	public static final ErrorCode INVALID_CVSROOT = new ErrorCode("CVS-00000");

}