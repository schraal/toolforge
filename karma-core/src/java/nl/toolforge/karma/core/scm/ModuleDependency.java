package nl.toolforge.karma.core.scm;

import java.io.File;

/**
 * Describes a dependency for a <code>Module</code>. This class is accessed by a Digester reading in a file called
 * <code>dependencies.xml</code> which is located in the root for each module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleDependency {

  private String id = null;
  private String groupId = null;
  private String artifactId = null;
  private String version = null;
  private String module = null;
  private String jar = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    // todo should comply to the correct pattern !!!!

    this.module = module;
  }

  public String getJar() {
    return jar;
  }

  public void setJar(String jar) {
    this.jar = jar;
  }

  public String getJarDependency() {

    String dep = null;

    // <dependency groupId="" artifactId="" version=""/>
    //
    if (groupId != null) {
      dep =  groupId + File.separator + "jars" + File.separator + artifactId + "-" + version;
      dep += ".jar";
    }

    // <dependency id="" jar=""/>
    //
    if (id != null) {
      dep = id + File.separator + "jars" + File.separator + jar;
    }

    return dep;
  }

  public boolean isModuleDependency() {

    // <dependency module=""/>
    //
    return module != null;
  }

}
