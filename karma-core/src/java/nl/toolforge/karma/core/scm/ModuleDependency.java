/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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

  /**
   * Returns the hash code for this instance. The hash code is either <code>module.hashCode()</code> or
   * <code>artifactId.hashCode()</code>; this follows the general structure
   *
   * @return
   */
  public int hashCode() {
    if (isModuleDependency()) {
      return module.hashCode();
    } else {
      if (groupId != null) {
        return artifactId.hashCode();
      } else {
        return id.hashCode();
      }
    }
  }

  /**
   * Checks two <code>ModuleDependency</code> instances for equality. If the dependency is a module dependency, their
   * module names are checked for equality. Otherwise the <code>artifactId</code> attribute is used to determine
   * equality.
   *
   * @param obj Another <code>ModuleDependency</code>.
   */
  public boolean equals(Object obj) {

    if (!(obj instanceof ModuleDependency)) {
      return false;
    } else {

      if (isModuleDependency()) {
        return module.equals(((ModuleDependency) obj).module);
      } else {
        return artifactId.equals(((ModuleDependency) obj).artifactId);
      }
    }
  }

}
