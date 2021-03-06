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
 * <p>Describes a dependency for a <code>Module</code>. This class is used by a Digester reading in a file called
 * <code>dependencies.xml</code> which is located in the root for each module that need dependencies. Dependencies can
 * be defined in three ways:
 *
 * <ul>
 *   <li/><code>&lt;dependency module="&lt;module-name&gt;"/&gt;</code> defines a dependency to another module that is
 *        part of the same manifest. Those modules should be of the correct type (<code>Java - Source Module</code>).
 *   <li/><code>&lt;dependency groupId="" artifactId="" version=""/&gt;</code> defines a dependency Maven-style. This
 *        means that the actual <code>jar</code>-file is found on a local disk in a Maven repository. Karma imposes a
 *        stronger definition of Maven dependencies than Maven does itself. Karma does not allow the following
 *        structure : <code>id="" jar=""</code>.
 *   <li/><code>&lt;dependency libmodule="" artifactId="" version=""/&gt;</code> defines that the
 *        <code>&lt;artifactId&gt;-&lt;version&gt;.jar</code> package is 'loaded' from the given lib module.
 * </ul>
 * <p>
 * As an optional attribute <code>package</code> can be defined, with possible values <code>true</code> or <code>false</code>.
 * <code>false</code> is the default. This defines whether or not the dependency should be packaged in the current module's package.
 * </p>
 *
 * @see nl.toolforge.karma.core.cmd.util.DependencyException
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleDependency {

  private String id = null;
  private String jar = null;

  private String groupId = null;
  private String libModule = null;
  private String artifactId = null;
  private String version = null;

  private String module = null;

  private boolean doPackage = false;


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
    this.module = module;
  }

  public String getLibModule() {
    return libModule;
  }

  public void setLibModule(String libModule) {
    this.libModule = libModule;
  }

  public String getJar() {
    return jar;
  }

  public void setJar(String jar) {
    this.jar = jar;
  }

  public void setPackage(boolean b) {
    this.doPackage = b;
  }

  public boolean doPackage() {
    return this.doPackage;
  }

  public String getJarDependency() {

    String dep = null;

    if (groupId != null) {
      // <dependency groupId="" artifactId="" version=""/>
      //
      dep = groupId + File.separator + "jars" + File.separator + artifactId + "-" + version;
      dep += ".jar";
    } else if (libModule != null) {
      // <dependency libModule="" artifactId="" version=""/>
      //
      dep = libModule + File.separator + "lib" + File.separator + artifactId + "-" + version;
      dep += ".jar";
    } else if (id != null) {
      // <dependency id="" jar=""/>
      //
      dep = id + File.separator + "jars" + File.separator + jar;
    }

    return dep;
  }

  /**
   * <code>true</code> if the dependency identifies a module in the same
   * manifest, otherwise false.
   */
  public boolean isModuleDependency() {

    // <dependency module=""/>
    //
    return module != null;
  }

  /**
   * <code>true</code> if the dependency identifies a jar in a module in the
   * same manifest, otherwise false.
   */
  public boolean isLibModuleDependency() {

    // <dependency module=""/>
    //
    return libModule != null;
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
      if (groupId != null || libModule != null) {
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
      } else if (groupId != null || libModule != null){
        return artifactId.equals(((ModuleDependency) obj).artifactId);
      } else if (id != null) {
        return id.equals(((ModuleDependency) obj).id) &&
                jar.equals(((ModuleDependency) obj).jar);
      } else {
        return false;
      }
    }
  }

  public String toString() {
    String result = "<dependency ";

    if (module != null) {
      result += "module=\""+module+"\" ";
    } else if (groupId != null) {
      result += "groupId=\""+groupId+"\" artifactId=\""+artifactId+"\" version=\""+version+"\" ";
    } else if (id != null) {
      result += "id=\""+id+"\" jar=\""+jar+"\" ";
    } else {
      result += "libModule=\""+libModule+"\" artifactId=\""+artifactId+"\" version=\""+version+"\" ";
    }
    if (doPackage) {
      result += "package=\"true\" ";
    } else {
      result += "package=\"false\" ";
    }
    result += "/>";
    return result;
  }
}
