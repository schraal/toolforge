package nl.toolforge.karma.core.cmd.util;

import junit.framework.TestCase;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.scm.ModuleDependency;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestDependencyHelper extends TestCase {

  private Location location =  new Location() {

    public boolean ping() {
      return false;
    }

    public Type getType() {
      return null;
    }

    public String getId() {
      return null;
    }
  };

  // A root module, with one module dependency and a jar dependency on log4j, version 1.2.8.
  //
  private Module root = new SourceModule("root", location) {

    public Set getDependencies() throws ManifestException {

      Set deps = new HashSet();

      ModuleDependency d1 = new ModuleDependency();
      d1.setModule("child");
      deps.add(d1);

      ModuleDependency d2 = new ModuleDependency();
      d2.setGroupId("log4j");
      d2.setArtifactId("log4j");
      d2.setVersion("1.2.8");
      deps.add(d2);

      return deps;
    }
  };

  // A root module, with one jar dependency on log4j, version 1.2.9.
  //
  private Module child = new SourceModule("child", location) {

    public Set getDependencies() throws ManifestException {

      Set deps = new HashSet();

      ModuleDependency d2 = new ModuleDependency();
      d2.setGroupId("log4j");
      d2.setArtifactId("log4j");
      d2.setVersion("1.2.9");
      deps.add(d2);

      return deps;
    }
  };

  private Manifest manifest = new Manifest() {

    public Module getModule(String moduleName) throws ManifestException {
      return child;
    }

    // todo requires mocks.

    public String getName() { return null; }
    public String getVersion() { return null; }
    public Map getAllModules() { return null; }
    public File getDirectory() { return null; }
    public void setState(Module module, Module.State state) { }
    public boolean isLocal(Module module) { return false; }
    public Module.State getState(Module module) { return null; }
    public String resolveArchiveName(Module module) throws ManifestException { return null; }
    public Collection getModuleInterdependencies(Module module) throws ManifestException { return null; }
    public Map getInterdependencies() throws ManifestException { return null; }
    public Collection getIncludes() { return null; }
    public void load() throws ManifestException { }
    public String getType() { return null; }
  };

  // ********************

  public void testGetAllLevels() {

    DependencyHelper helper = new DependencyHelper(manifest);

    try {

      helper.getAllLevels(root);

      fail("Would have expected a DependencyException.");

    } catch(ManifestException m) {
      fail(m.getMessage());
    } catch (DependencyException e) {
      assertTrue(true);
    }
  }
}
