package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.KarmaException;

/**
 * Describes a Maven repository.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class MavenRepositoryImpl extends BaseLocation {

	public MavenRepositoryImpl(String id) throws KarmaException {
         super(id, Location.Type.MAVEN_REPOSITORY);
	}

}
