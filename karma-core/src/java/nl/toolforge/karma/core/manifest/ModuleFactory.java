package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.vc.DevelopmentLine;

/**
 * <p>Factory class to create modules based on a {@link ModuleDescriptor}.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleFactory {

  private static ModuleFactory instance;

  /**
   * Get the singleton instance of this factory.
   *
   * @return The factory to create modules.
   */
  public synchronized static ModuleFactory getInstance() {
    if (instance == null) {
      instance = new ModuleFactory();
    }
    return instance;
  }

  private ModuleFactory() {}

  public Module create(ModuleDescriptor descriptor) throws LocationException {

    if (descriptor == null) {
      throw new IllegalArgumentException("Descriptor cannot be null.");
    }

    Module module = null;

    Location location = LocationFactory.getInstance().get(descriptor.getLocation());

    DevelopmentLine developmentLine = null;
    if (descriptor.getDevelopmentLine() != null) {
      developmentLine = new DevelopmentLine(descriptor.getDevelopmentLine());
    }
    Version version = null;
    if (descriptor.getVersion() != null) {
      version = new Version(descriptor.getVersion());
    }
//    DevelopmentLine developmentLine = descriptor.getDevelopmentLine();
//    Version version = descriptor.getVersion();

    //
    // Create a SourceModule instance.
    //
    if (version != null) {

      if (developmentLine != null) {
        //
        // <module name="" location="" version="" development-line="">
        //
        switch (descriptor.getType()) {
          case ModuleDescriptor.SOURCE_MODULE :
            module = new SourceModule(descriptor.getName(), location, version, developmentLine);
            break;
          case ModuleDescriptor.MAVEN_MODULE :
            module = new MavenModule(descriptor.getName(), location, version, developmentLine);
            break;
        }
      } else {
        //
        // <module name="" location="" version="">
        //
        switch (descriptor.getType()) {
          case ModuleDescriptor.SOURCE_MODULE :
            module = new SourceModule(descriptor.getName(), location, version);
            break;
          case ModuleDescriptor.MAVEN_MODULE :
            module = new MavenModule(descriptor.getName(), location, version);
            break;
        }
      }
    } else if (developmentLine != null) {
      //
      // <module name="" location="" development-line="">
      //
      switch (descriptor.getType()) {
        case ModuleDescriptor.SOURCE_MODULE :
          module = new SourceModule(descriptor.getName(), location, developmentLine);
          break;
        case ModuleDescriptor.MAVEN_MODULE :
          module = new MavenModule(descriptor.getName(), location, developmentLine);
          break;
      }
    } else {
      //
      // <module name="" location="">
      //
      switch (descriptor.getType()) {
        case ModuleDescriptor.SOURCE_MODULE :
          module = new SourceModule(descriptor.getName(), location);
          break;
        case ModuleDescriptor.MAVEN_MODULE :
          module = new MavenModule(descriptor.getName(), location);
          break;
      }
    }

    return module;
  }

}