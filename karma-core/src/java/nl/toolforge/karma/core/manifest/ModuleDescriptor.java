package nl.toolforge.karma.core.manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.PatternSyntaxException;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.vc.DevelopmentLine;

/**
 * Class modelling a <code>&lt;module&gt;</code>-element in a manifest file.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleDescriptor {

  private static Log logger = LogFactory.getLog(ModuleDescriptor.class);

  // todo string validation should be done in the xml-schema.
  //

  public static final String NAME_PATTERN_STRING = "[A-Za-z\\-\\d]+";
  public static final String TYPE_PATTERN_STRING = "src|maven|lib";
  public static final String LOCATION_PATTERN_STRING = "[0-9a-z\\-\\_]+";

  public static final int SOURCE_MODULE = 0;
  public static final int MAVEN_MODULE = 1;
  public static final int LIB_MODULE = 2;

  private String name = null;
  private String type = null;
  private String location = null;

  private String version = null;
//  private Version version = null;
  private String developmentLine = null;
//  private DevelopmentLine developmentLine = null;

  /**
   *
   */
  public ModuleDescriptor(String name, String type, String location) {

    if (name == null || !name.matches(NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for name-attribute. Should match " + NAME_PATTERN_STRING, name, -1);
    }
    this.name = name;

    if (type == null || !type.matches(TYPE_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for type-attribute. Should match " + TYPE_PATTERN_STRING, type, -1);
    }

    this.type = type;

    if (location == null || !location.matches(LOCATION_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for location-attribute. Should match " + LOCATION_PATTERN_STRING, location, -1);
    }
    this.location = location;
  }


  public String getName() {
    return name;
  }

  public int getType() {
    if ("src".equals(type)) return SOURCE_MODULE;
    if ("maven".equals(type)) return MAVEN_MODULE;
    return LIB_MODULE;
  }

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

  public void setDevelopmentLine(String developmentLine) {

    if (developmentLine == null || !developmentLine.matches(DevelopmentLine.DEVELOPMENT_LINE_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for version. Should match " + DevelopmentLine.DEVELOPMENT_LINE_PATTERN_STRING, developmentLine, -1);
    }
    this.developmentLine = developmentLine;
  }
  public String getDevelopmentLine() {
    return developmentLine;
  }

  /**
   * Modules are equal when their name and location are equal.
   *
   * @param o The object instance that should be compared with <code>this</code>.
   * @return <code>true</code> if this module descriptor
   */
  public boolean equals(Object o) {

    if (o instanceof ModuleDescriptor) {
      if (
          (getName().equals(((ModuleDescriptor) o).getName())) &&
          (getLocation().equals(((ModuleDescriptor) o).getLocation())) ) {
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


