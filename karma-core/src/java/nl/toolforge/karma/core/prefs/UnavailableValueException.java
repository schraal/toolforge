package nl.toolforge.karma.core.prefs;

import nl.toolforge.karma.core.KarmaRuntimeException;

/**
 * Thrown when a required property is not available for Karma.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class UnavailableValueException extends KarmaRuntimeException
{
    public UnavailableValueException(String message) {
        super(message);
    }
}
