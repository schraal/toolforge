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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Mapping of a manifest file and its included manifest files (any level deep possible). Note that the structure that
 * is build is merely the template for the real <code>Manifest</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestStructure {

  private String name = null;
  private String version = null;
  private String type = null;

  private String description = null;

  private Map childs = null;
  private List modules = null;

  public ManifestStructure() {
    childs = new Hashtable();
    modules = new ArrayList();
  }

  public Map getChilds() {
    return childs;
  }

  public ManifestStructure getChild(String childName) {
    return (ManifestStructure) childs.get(childName);
  }

  public void addChild(ManifestStructure structure) throws ManifestException {
    if (childs.containsKey(structure.getName())) {
      throw new ManifestException(ManifestException.MANIFEST_NAME_RECURSION, new Object[] {structure.getName()});
    }
    childs.put(structure.getName(), structure);
  }

  public List getModules() {
    return modules;
  }

  public void addModule(ModuleDigester digester) throws ManifestException {

    if (modules.contains(digester)) {
      throw new ManifestException(ManifestException.DUPLICATE_MODULE, new Object[] {digester.getName()});
    }
    modules.add(digester);
  }

  public String getName() {
    return name;
  }

  public void setName(String name)  {

    if (name.trim().startsWith(".")) {
      throw new IllegalArgumentException("A manifest name cannot start with a `.`.");
    }
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {

    if (!version.matches("\\d{1}-\\d{1}")) {
      throw new PatternSyntaxException("A manifests' version attribute should look like '0-0'.", "\\d{1}-\\d{1}", -1);
    }
    this.version = version;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void update(ManifestStructure child) {

    this.name = child.name;
    this.type = child.type;
    this.version = child.version;
    this.description = child.description;
    this.childs = child.childs;
    this.modules = child.modules;
  }
}
