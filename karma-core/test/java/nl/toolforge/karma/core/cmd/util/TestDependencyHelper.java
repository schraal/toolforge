package nl.toolforge.karma.core.cmd.util;

import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.ModuleTypeException;
import nl.toolforge.karma.core.module.Module.State;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.VersionControlException;


/**
 * 
 * 
 * 
 * @author Age Mooy (<a href="mailto:age.mooy@gmail.com">age.mooy@gmail.com</a>)
 */
public class TestDependencyHelper extends TestCase {

  private MockManifest     mockManifest = null;
  private MockModule       mockModule   = null;
  private ModuleDependency mockDependency = null;
  
  
  // ==========================================================================
  // Setup of fixtures
  // ==========================================================================
  
  protected void setUp() throws Exception {
    mockManifest = new MockManifest();
    
    // module A
    mockModule = new MockModule("A");
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("B");
    mockModule.addDependency(mockDependency);
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("C");
    mockModule.addDependency(mockDependency);
    
    mockManifest.addModule(mockModule);
    
    // module B
    mockModule = new MockModule("B");
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("D");
    mockModule.addDependency(mockDependency);
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("E");
    mockModule.addDependency(mockDependency);
    
    mockManifest.addModule(mockModule);
    
    // module C
    mockModule = new MockModule("C");
    mockManifest.addModule(mockModule);
    
    // module D
    mockModule = new MockModule("D");
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("F");
    mockModule.addDependency(mockDependency);
    
    mockManifest.addModule(mockModule);
    
    // module E
    mockModule = new MockModule("E");
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("F");
    mockModule.addDependency(mockDependency);
    
    mockDependency = new ModuleDependency();
    mockDependency.setModule("C");
    mockModule.addDependency(mockDependency);
    
    mockManifest.addModule(mockModule);
    
    // module F
    mockModule = new MockModule("F");
    mockManifest.addModule(mockModule);
  }

  
  // ==========================================================================
  // Test methods
  // ==========================================================================
  
  public void testFixtureAfterSetup() throws Exception {
    assertNotNull(mockManifest);
    
    assertNotNull(mockManifest.getModule("A"));
    assertNotNull(mockManifest.getModule("B"));
    assertNotNull(mockManifest.getModule("C"));
    assertNotNull(mockManifest.getModule("D"));
    assertNotNull(mockManifest.getModule("E"));
    assertNotNull(mockManifest.getModule("F"));
    
    assertEquals(2, mockManifest.getModule("A").getDependencies().size());
    assertEquals(2, mockManifest.getModule("B").getDependencies().size());
    assertEquals(0, mockManifest.getModule("C").getDependencies().size());
    assertEquals(1, mockManifest.getModule("D").getDependencies().size());
    assertEquals(2, mockManifest.getModule("E").getDependencies().size());
    assertEquals(0, mockManifest.getModule("F").getDependencies().size());
  }
  
  public void testGetModuleBuildOrder() throws Exception {
    DependencyHelper helper = new DependencyHelper(mockManifest);
    
    List moduleBuildOrder = helper.getModuleBuildOrder(mockManifest.getModule("A"));
    
    assertEquals(6, moduleBuildOrder.size());
    
    assertTrue(moduleBuildOrder.indexOf("F") < moduleBuildOrder.indexOf("E"));
    assertTrue(moduleBuildOrder.indexOf("F") < moduleBuildOrder.indexOf("D"));
    assertTrue(moduleBuildOrder.indexOf("D") < moduleBuildOrder.indexOf("B"));
    assertTrue(moduleBuildOrder.indexOf("E") < moduleBuildOrder.indexOf("B"));
    assertTrue(moduleBuildOrder.indexOf("C") < moduleBuildOrder.indexOf("E"));
    assertTrue(moduleBuildOrder.indexOf("C") < moduleBuildOrder.indexOf("A"));
    assertTrue(moduleBuildOrder.indexOf("B") < moduleBuildOrder.indexOf("A"));
  }
  
  public void testOneLevelCircularBuildOrder() throws ManifestException {
    mockDependency = new ModuleDependency();
    mockDependency.setModule("A");
    
    mockModule = (MockModule) mockManifest.getModule("B");
    mockModule.addDependency(mockDependency);
    
    DependencyHelper helper = new DependencyHelper(mockManifest);
    
    try {
      helper.getModuleBuildOrder(mockManifest.getModule("A"));
      
      fail("Circular dependency between module A and module B not detected.");
    } catch (DependencyException e) {
      assertEquals(DependencyException.CIRCULAR_DEPENDENCY, e.getErrorCode());
      
      Object[] messageArguments = e.getMessageArguments();
      
      assertEquals(1, messageArguments.length);
      assertEquals("A --> B --> A", messageArguments[0]);
    }
  }
  
  public void testTwoLevelCircularBuildOrder() throws ManifestException {
    mockDependency = new ModuleDependency();
    mockDependency.setModule("A");
    
    mockModule = (MockModule) mockManifest.getModule("E");
    mockModule.addDependency(mockDependency);
    
    DependencyHelper helper = new DependencyHelper(mockManifest);
    
    try {
      helper.getModuleBuildOrder(mockManifest.getModule("A"));
      
      fail("Circular dependency between module A and module E not detected.");
    } catch (DependencyException e) {
      assertEquals(DependencyException.CIRCULAR_DEPENDENCY, e.getErrorCode());

      Object[] messageArguments = e.getMessageArguments();
      
      assertEquals(1, messageArguments.length);
      assertEquals("A --> B --> E --> A", messageArguments[0]);
    }
  }
  
  
  
  // ==========================================================================
  // Mock implementations of Module and Manifest
  // ==========================================================================
  
  private static class MockModule implements Module {
    private Set    dependencies = new LinkedHashSet();
    private String name         = null;
    
    public MockModule(String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }

    public Set getDependencies() {
      return dependencies;
    }
    
    // ========================================================================
    // Util methods
    // ========================================================================
    
    public void addDependency(ModuleDependency dependency) {
      dependencies.add(dependency);
    }
    
    // ========================================================================
    // Unimplemented methods
    // ========================================================================
    
    public Type getType() throws ModuleTypeException {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public Location getLocation() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public void setBaseDir(File baseDir) {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public File getBaseDir() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public DevelopmentLine getPatchLine() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public void markPatchLine(boolean mark) {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public boolean hasPatchLine() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public boolean hasDevelopmentLine() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public void markDevelopmentLine(boolean mark) {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public Version getVersion() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public String getVersionAsString() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public boolean hasVersion() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public ModuleLayoutTemplate getLayoutTemplate() {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }

    public void createRemote(Authenticator authenticator, String createComment) throws VersionControlException, AuthenticationException {
      throw new UnsupportedOperationException("Method not implemented by MockModule.");
    }
  }
  
  /**
   * Mock implementation of Manifest that only supports looking up a module 
   * by name. It has an extra util method to add modules to it.
   * 
   * @author Age Mooy (<a href="mailto:age.mooy@gmail.com">age.mooy@gmail.com</a>)
   */
  private static class MockManifest implements Manifest {

    /** Stores modules by name. */
    private Map modules = new HashMap();
    
    /**
     * @see nl.toolforge.karma.core.manifest.Manifest#getModule(java.lang.String)
     */
    public Module getModule(String moduleName) throws ManifestException {
      return (Module) modules.get(moduleName);
    }
    
    // ========================================================================
    // Util methods
    // ========================================================================
    
    public void addModule(Module module) {
      modules.put(module.getName(), module);
    }
    
    // ========================================================================
    // Unimplemented methods
    // ========================================================================
    
    public String getName() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public String getVersion() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public Map getAllModules() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public File getBaseDirectory() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public File getBuildBaseDirectory() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public File getReportsBaseDirectory() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public File getModuleBaseDirectory() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public File getTempDirectory() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public void setState(Module module, State state) {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public boolean isLocal(Module module) {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public State getState(Module module) {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public Collection getModuleInterdependencies(Module module) throws ManifestException {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public Map getInterdependencies() throws ManifestException {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public Collection getIncludes() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }

    public String getType() {
      throw new UnsupportedOperationException("Method not implemented by MockManifest.");
    }
  }
  
  
  
}
