package nl.toolforge.karma.core;

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

  /**
   * Returns <code>null</code>. A jarmodule in itself has no other dependencies.
   * 
   * @return <code>null</code>.
   */
  public String getDependencies() {
    return null;
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
