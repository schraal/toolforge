/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package nl.toolforge.karma.core.location;

/**
 * Enumeration for the location type.
 *
 * @author D.A. Smedes
 */
public final class LocationType {

  /**
   * A CVS repository location
   */
  public static final LocationType CVS = new LocationType("cvs");

  /**
   * A Subversion repository location
   */
  public static final LocationType SUBVERSION = new LocationType("svn");

  public static final LocationType DIRECTORY = new LocationType("directory");

  String type = null;

  private LocationType(String type) {
    this.type = type;
  }

  public static LocationType getTypeInstance(String type) {

    if (!type.matches(CVS + "|" + SUBVERSION + "|" + DIRECTORY)) {
      throw new IllegalArgumentException("Type should match `" + CVS + "|" + SUBVERSION + "|" + DIRECTORY + "`");
    }
    return new LocationType(type);
  }

  /**
   * Shows which type we're talking about.
   *
   * @return A lowercase string for the specific type of location.
   */
  public String toString() {
    return this.type.toLowerCase();
  }

  public int hashCode() {
    return type.hashCode();
  }

  public boolean equals(Object obj) {

    if (obj instanceof LocationType) {
      return ((LocationType) obj).type.equals(type);
    }
    return false;
  }
}