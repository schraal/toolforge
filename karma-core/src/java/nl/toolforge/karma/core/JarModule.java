package nl.toolforge.karma.core;

import nl.toolforge.karma.core.model.JarModuleDescriptor;

import java.io.File;

/**
 * <p>A <code>JarModule</code> represents a Java <code>jar</code>-artifact. 
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see Module
 */
public class JarModule extends BaseModule {

	/**
	 * Element name for a source module in a manifest XML file
	 */
	public static final String ELEMENT_NAME = "jarmodule";

	/**
	 * The <code>version</code>-attribute for a module.
	 */
	public static final String VERSION_ATTRIBUTE = "version";

	private Version version = null;

	/**
	 * Constructs a <code>SourceModule</code> instance.
	 *
	 * @param descriptor
   * @param manifestDirectory
	 * @throws ManifestException
	 */
	JarModule(JarModuleDescriptor descriptor, File manifestDirectory) throws ManifestException {

		super(descriptor, manifestDirectory);
    this.version = descriptor.getVersion();
	}

//	/**
//	 * Creates a <code>Module</code> instance; the module contains a <code>version</code> attribute.
//	 *
//	 * @param moduleName The name of the module.
//	 * @param location   The location of the jar module.
//	 * @param version
//	 * @throws KarmaException When input parameters don't match their respective patterns
//	 */
//	JarModule(String moduleName, Location location, String version) throws KarmaException {
//
//		super(moduleName, location);
//
//		Pattern pattern = Expressions.getPattern("VERSION");
//		Matcher matcher = pattern.matcher(version);
//
//		if (matcher.matches()) {
//			this.version = version;
//		} else {
//			// log.debug();
//			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
//		}
//	}

	/**
	 *
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Overrides {@link BaseModule#setState}, because this implementation has no notion of state.
	 *
	 * @param state Ignored.
	 */
	public void setState(State state) {
	}

  public String getDependencyName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
	 * Not implemented for this type of module (<b>yet</b>).
	 *
	 * @return Nothing, will throw <code>KarmaRuntimeException</code>.
	 */
	public File getModuleDirectory() {
		throw new KarmaRuntimeException("Not implemented.");
	}

}
