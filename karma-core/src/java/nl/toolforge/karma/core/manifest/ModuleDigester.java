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
package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;

import java.util.regex.PatternSyntaxException;

/**
 * Class modelling a <code>&lt;module&gt;</code>-element in a manifest file.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleDigester {

//  private static Log logger = LogFactory.getLog(ModuleDigester.class);

  // todo string validation should be done in the xml-schema.
  //

  public static final String NAME_PATTERN_STRING = "[A-Za-z\\-\\d]+";
  public static final String SOURCE_TYPE_PATTERN_STRING = "src|maven|lib";
  public static final String LOCATION_PATTERN_STRING = "[0-9a-z\\-\\_]+";

  public static final int SOURCE_MODULE = 0;
  public static final int MAVEN_MODULE = 1;
  public static final int LIB_MODULE = 2;

  private String name = null;
//  private Module.SourceType sourceType = null;
  private String location = null;

  private String version = null;

  /**
   *
   */
//  public ModuleDigester(String name, String type, String location) {
  public ModuleDigester(String name, String location) {

    if (name == null || !name.matches(NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for name-attribute. Should match " + NAME_PATTERN_STRING, name, -1);
    }
    this.name = name;

//    if (type == null || !type.matches(SOURCE_TYPE_PATTERN_STRING)) {
//      throw new PatternSyntaxException(
//          "Pattern mismatch for type-attribute. Should match " + SOURCE_TYPE_PATTERN_STRING, type, -1);
//    }

//    sourceType = new Module.SourceType(type);

    if (location == null || !location.matches(LOCATION_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for location-attribute. Should match " + LOCATION_PATTERN_STRING, location, -1);
    }
    this.location = location;
  }


  public String getName() {
    return name;
  }

//  public int getType() {
//    if ("src".equals(sourceType.getSourceType())) return SOURCE_MODULE;
//    if ("maven".equals(sourceType.getSourceType())) return MAVEN_MODULE;
//    return LIB_MODULE;
//  }

  public String getLocation() {
    return location;
  }

  /**
   * Sets the modules' version. {@link nl.toolforge.karma.core.Version} is used to validate the version-string that
   * is passed as a parameter.
   *
   * @param version Version string.
   */
  public void setVersion(String version) {

    if (!version.matches(Version.VERSION_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for version. Should match " + Version.VERSION_PATTERN_STRING, version, -1);
    }
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  /**
   * Modules are equal when their name and location are equal.
   *
   * @param o The object instance that should be compared with <code>this</code>.
   * @return <code>true</code> if this module descriptor
   */
  public boolean equals(Object o) {

    if (o instanceof ModuleDigester) {
      if (
          (getName().equals(((ModuleDigester) o).getName())) &&
          (getLocation().equals(((ModuleDigester) o).getLocation())) ) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int hashCode() {

    return name.hashCode() + location.hashCode();
  }
}


