package nl.toolforge.karma.core.location;


/**
 * Describes a Maven repository.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class MavenRepositoryImpl extends BaseLocation {

	public MavenRepositoryImpl(String id) {
		super(id, Location.Type.MAVEN_REPOSITORY);
	}

}
