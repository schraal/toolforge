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
package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.VersionControlException;

import java.io.File;
import java.util.Set;
import java.util.regex.PatternSyntaxException;


/**
 * <p>A module is a collection of files, representing some block of functionality. This definition is probably highly
 * subjective, but for Karma, that's what it is. A module is part of a container, called a
 * <code>Manifest</code>. System's theory tells us that a system is separated into subsystems. Well, that's what we
 * do in the Karma context as well. An application system consists of one or more (generally more) modules.</p>
 *
 * <p>Karma <code>Module</code>s are maintained in a version management system and grouped together in a
 * <code>Manifest</code>. The manifest is managing the modules.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Module {

  /**
   * <code>UNKNOWN</code> applies to module which have no local presence. The actual type can only be determined when
   * the <code>module-descriptor.xml</code> file is available, which is the case after a checkout of the module.
   */
  public static final Type UNKNOWN = new Type("UNKNOWN");
  /**
   * Represents <code>&lt;type&gt;LIBRARY_MODULE&lt;/type&gt;</code>.
   */
  public static final Type LIBRARY_MODULE = new Type("LIBRARY-MODULE");
  /**
   * Represents <code>&lt;type&gt;JAVA_SOURCE_MODULE&lt;/type&gt;</code>.
   */
  public static final Type JAVA_SOURCE_MODULE = new Type("JAVA-SOURCE-MODULE");
  /**
   * Represents <code>&lt;type&gt;JAVA_WEB_APPLICATION&lt;/type&gt;</code>.
   */
  public static final Type JAVA_WEB_APPLICATION = new Type("JAVA-WEB-APPLICATION");
  /**
   * Represents <code>&lt;type&gt;OTHER-MODULE&lt;/type&gt;</code>.
   */
  public static final Type OTHER_MODULE = new Type("OTHER-MODULE");

  /**
   * Represents <code>&lt;type&gt;JAVA_ENTERPRISE_APPLICATION&lt;/type&gt;</code>.
   */
  public static final Type JAVA_ENTERPRISE_APPLICATION = new Type("JAVA-ENTERPRISE-APPLICATION");

  public static final State WORKING = new State("WORKING");
  public static final State DYNAMIC = new State("DYNAMIC");
  public static final State STATIC = new State("STATIC");

  /**
   * String identifying the file name for the module descriptor.
   */
  public static final String MODULE_DESCRIPTOR = "module-descriptor.xml";

  /**
   * Retrieves a modules' name, the <code>name</code> attribute of the module in the manifest XML file.
   *
   * @return The modules' name.
   */
  public String getName();

  /**
   * Determines the type of the module. This is only possible when the module is checked out locally.
   *
   * @return The type of the module (see constants defined in <code>Module</code>).
   */
  public Type getType() throws ModuleTypeException;

  /**
   * Returns the <code>Location</code> instance, which is derived from the <code>location</code>-attribute.
   */
  public Location getLocation();

  /**
   * A module has a base directory, which is relative to the manifest that requires it. When the manifest is loaded, the
   * base directory can be set.
   *
   * @param baseDir The base directory of the module relative to the active manifest.
   */
  public void setBaseDir(File baseDir);

  /**
   * The base directory of the module relative to the active manifest.
   *
   * @return The base directory of the module relative to the active manifest.
   */
  public File getBaseDir();

//  public void setCheckoutDir(File checkoutDir);

//  public File getCheckoutDir();
  
  /**
   * Returns the <code>PatchLine</code> for this module, if the module matches the correct criteria as specified in
   * {@link #markPatchLine(boolean)}.
   *
   * @return <code>null</code> if a PatchLine does not exist for this module, otherwise the <code>PatchLine</code>
   *   instance for this module.
   * @see #markPatchLine(boolean)
   */
  public DevelopmentLine getPatchLine();

  /**
   * Marks this module as being developed in a <code>PatchLine</code>. This can only happen when the manifest in which
   * the module is used is a <code>ReleaseManifest</code> and the module has a <code>STATIC</code> state. When the
   * manifest is loaded, this method will be called when the module matches the criteria.
   */
  public void markPatchLine(boolean mark);

  public boolean hasPatchLine();

  /**
   * Checks if the module - within the current manifest - has development line configuration. When the manifest is
   * a <code>ReleaseManifest</code>, this is true when a <code>PatchLine</code> exists. When the manifest is a
   * <code>DevelopmentManifest</code>, this method returns false, as this feature is not supported.
   *
   * @return
   */
  public boolean hasDevelopmentLine();

  /**
   * Marks this modules as being developed in a <code>DevelopmentLine</code>. This feature is NOT supported as yet.
   */
  public void markDevelopmentLine(boolean mark);

  /**
   * If a module has a &lt;version&gt;-attribute, this method returns a Version instance representing the version
   * number of the module.
   *
   * @return The version of the module if it has one.
   */
  public Version getVersion();

  /**
   * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
   * value of that attribute.
   *
   * @return The module version, or <code>N/A</code>, when no version number exists.
   */
  public String getVersionAsString();

  /**
   * Checks if a module has a &lt;version&gt;-attribute.
   *
   * @return <ode>true</code> if the module has a &lt;version&gt;-attribute or <code>false</code> if it hasn't.
   */
  public boolean hasVersion();

  /**
   * Gets a <code>Set</code> of <code>ModuleDependency</code> objects. This method should return an empty set if no
   * dependencies have been specified. Dependencies are not checked to be available. 
   *
   * @return A <code>Set</code> containing all dependencies as <code>ModuleDependency</code> objects.
   */
  public Set getDependencies();

  /**
   * Returns the correct layout template for the module.
   *
   * @return A <code>ModuleLayoutTemplate</code> for the specific module type.
   */
  public ModuleLayoutTemplate getLayoutTemplate();

  public void createRemote(Authenticator authenticator, String createComment) throws VersionControlException, AuthenticationException ;

  /**
   * Inner class representing the type of the module, which is determined at runtime by reading the
   * <code>module-descriptor.xml</code> file from the module base directory.
   *
   * @author D.A. Smedes
   */
  final class Type {

    private String type = null;
    private String shortType = null;

    public Type() {}

    private Type(String type) {
      this.type = type;
    }

    // todo think of better shortcuts for these types.
    public void setType(String type) {
      if ("src".equals(type) || JAVA_SOURCE_MODULE.getType().equals(type)) {
        this.type = JAVA_SOURCE_MODULE.getType();
        shortType = "src";
      } else if ("lib".equals(type) || LIBRARY_MODULE.getType().equals(type)) {
        this.type = LIBRARY_MODULE.getType();
        shortType = "lib";
      } else if ("webapp".equals(type) || JAVA_WEB_APPLICATION.getType().equals(type)) {
        this.type = JAVA_WEB_APPLICATION.getType();
        shortType = "webapp";
      } else if ("eapp".equals(type) || JAVA_ENTERPRISE_APPLICATION.getType().equals(type)) {
        this.type = JAVA_ENTERPRISE_APPLICATION.getType();
        shortType = "eapp";
      } else if ("other".equals(type) || OTHER_MODULE.getType().equals(type)) {
        this.type = OTHER_MODULE.getType();
        shortType = "other";
      } else {
        throw new IllegalArgumentException("Type must be 'src', 'lib', 'webapp', 'eapp' or 'other'.");
      }
    }

    public String getType() {
      return type;
    }

    public String getShortType() {
      return shortType;
    }

    public String toString() {
      return getType();
    }
    
    public int hashCode() {
      return type.hashCode();
    }

    public boolean equals(Object o) {
      if (o instanceof Type) {
        if (((Type) o).type.equals(type)) {
          return true;
        } else {
          return false;
        }
      }
      return false;
    }
  }

  /**
   * <p>Inner class representing the 'state' of a module. Three states exist at the moment : <code>WORKING</code>,
   * <code>STATIC</code> and <code>DYNAMIC</code>.
   * <p/>
   *
   * <ul>
   *   <li/><code>WORKING</code> means that a developer wants to develop on the module; add code, remove code etc. The
   *        local copy of the module will be updated to the reflect the latest versions of files in a particular
   *        branch. <code>WORKING</code> state also implies that a developer can promote a module so that manifests
   *        that have the module in a <code>DYNAMIC</code> state, can choose to upgrade their manifest to the latest
   *        (stable) version of the module.
   *   <li/><code>DYNAMNIC</code> means that a developer is not interested in the HEAD of a development line, but only
   *        in stable versions of the module.
   *   <li/><code>STATIC</code> means that a developer wants to use a fixed version of the module in the manifest.
   * </ul>
   *
   * @author D.A. Smedes
   */
  final class State {

    // todo unit test should be written

    private String state = null;

    /**
     * Constructor. Initializes the <code>State</code> instance with the correct state string.
     *
     * @param stateString
     */
    public State(String stateString) {

      if (!stateString.matches("WORKING|DYNAMIC|STATIC|UNDEFINED")) {
        throw new PatternSyntaxException(
            "Pattern mismatch for 'state'; pattern must match 'WORKING|DYNAMIC|STATIC|UNDEFINED'", stateString, -1);
      }
      this.state = stateString;
    }

    /**
     * Gets the string representation of this state object.
     *
     * @return A <code>String</code> representation of this state object.
     */
    public String toString() {
      return state;
    }

    public int hashCode() {
      return state.hashCode();
    }

    /**
     * Checks equality of one <code>State</code> instance to this <code>State</code> instance. Instances are equal
     * when their state strings are equal.
     *
     * @param o An object instance that must be checked for equality with this <code>State</code> instance.
     * @return <code>true</code> if this <code>State</code> instance equals <code>o</code>, otherwise
     *         <code>false</code>.
     */
    public boolean equals(Object o) {

      if (o instanceof State) {
        if (o.toString().equals(this.toString())) {
          return true;
        } else {
          return false;
        }
      }
      return false;
    }

    /**
     * Returns the filename for this state on disk (generally something like <code>.WORKING</code> or
     * <code>.STATIC</code>.
     *
     * @return
     */
    public String getHiddenFileName() {
      return "." + state;
    }
  }
}
