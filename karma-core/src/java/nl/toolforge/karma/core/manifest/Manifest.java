package nl.toolforge.karma.core.manifest;

import java.util.Map;
import java.util.Collection;
import java.io.File;

import nl.toolforge.karma.core.LocalEnvironment;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Manifest {

  public static final String DEVELOPMENT_MANIFEST = "development";
  public static final String RELEASE_MANIFEST = "release";

  public static final String HISTORY_KEY = "manifest.history.last";

  public String getName();

  public Map getAllModules();

  public File getDirectory() throws ManifestException;

  public String resolveJarName(Module module) throws ManifestException;

  public Module getModule(String moduleName) throws ManifestException;

  public Collection getModuleInterdependencies(Module module);

  public Map getInterdependencies();

  public void load(LocalEnvironment localEnvironment) throws ManifestException;

  public void setState(Module module, Module.State working) throws ManifestException;

  public boolean isLocal(Module module);
}
