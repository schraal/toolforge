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
package nl.toolforge.karma.core.boot;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ConfigurationItem {

  private String property = null;
  private String label = null;
  private String defaultValue = null;
  private boolean scrambled = false;

  public ConfigurationItem(String property, String label, String defaultValue) {
    this(property, label, defaultValue, false);
  }

  public ConfigurationItem(String property, String label, String defaultValue, boolean scrambled) {
    this.property = property;
    this.label = label;
    this.defaultValue = defaultValue;
    this.scrambled = scrambled;
  }

  public String getProperty() {
    return property;
  }

  public String getLabel() {
    return label;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean isScrambled() {
    return scrambled;
  }
}
