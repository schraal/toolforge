package nl.toolforge.karma.core.process.common;

import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.MavenModule;
import nl.toolforge.karma.core.SourceModule;
import org.apache.tools.ant.types.FileList;

import javax.xml.transform.Source;
import java.io.File;

/**
 * A dependency builder processes all available dependencies given a module or a manifest and builds a classpath string
 * for compile purposes.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class DependencyBuilder {

  private Manifest manifest = null; // When this object is instantiated with a Manifest

  private Module module = null; // When this object is instantiated with a Module
  private File localPath = null;

  private FileList fileList = null;

  private DependencyBuilder() {
    fileList = new FileList();
  }

  /**
   * Creates a builder for the manifest supplied.
   *
   * @param manifest
   */
  public DependencyBuilder(Manifest manifest) {

    this();

    this.manifest = manifest;

    // todo to be implemented
  }

  /**
   * Creates a builder for the module supplied.
   *
   * @param module
   * @param localPath A modules' location on disk (usually within the context of a manifest).
   */
  public DependencyBuilder(Module module, File localPath) {

    this();

    this.localPath = localPath;
    this.module = module;

    // Determine this modules' dependencies
    //
    setDependencies();
  }

  /**
   * Returns a dependency {@link FileList} based on the module or manifest supplied.
   *
   * @return
   */
  public FileList getDependencies() {
    return fileList;
  }

  private void setDependencies() {

    // 1. Get the correct module type. If type == maven, get project.xml, else get module.xml.
    //

    if (this.module instanceof MavenModule) {

    } else if (this.module instanceof SourceModule) {

    }
//
//
//    fileList.setFiles(buf.toString());
//
//    fileList.setFiles(localPath + File.separator + "build" + File.separator + module.getDependencyName());


  }
}
