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
package nl.toolforge.karma.core.cmd;

import org.apache.commons.cli.Options;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A <code>CommandDescriptor</code> is the object representation of a command as it is specified in a
 * <code>command.xml</code> file.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandDescriptor {

  private String name = null;
  private String description = null;
  private String helpText = null;
  private String className = null;
  private String aliasString = null;

  private Set aliasList = null;
  private Options options = null;

  public CommandDescriptor(String name, String aliasString) {
    this.name = name;
    this.aliasString = aliasString;

    aliasList = new HashSet();
    aliasList.add(name);

    createAliasList(aliasString);
  }

  /**
   * Returns the name of the command represented by this <code>CommandDescriptor</code>.
   *
   * @return The name of the command represented by this <code>CommandDescriptor</code>
   */
  public String getName() {
    return this.name;
  }

  /**
   * The aliasses of a command are all Strings by which the command can be referenced (including its name).
   * 
   * @return
   */
  public Set getAliasList() {
    return aliasList;
  }

  private void createAliasList(String aliasString) {

    if (aliasString.indexOf(" ") != -1) {
      StringTokenizer tokenizer = new StringTokenizer(aliasString, " ");
      while (tokenizer.hasMoreTokens()) {
        aliasList.add(tokenizer.nextToken());
      }
    } else {
      aliasList.add(aliasString);
    }
  }

  public String getAlias() {
    return aliasString;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @param options The name of the command (the lt;options&gt;-child-element attribute of the &lt;command&gt;-element).
   */
  public void addOptions(Options options) {
    this.options = options;
  }

  public Options getOptions() {
    if (options == null) {
      return new Options();
    } else {
      return this.options;
    }
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setHelp(String helpText) {
    this.helpText = helpText;
  }

  public String getHelp() {
    return this.helpText;
  }

  /**
   * Commands are equal when their names are equal or any alias equals an alias from <code>o</code> or the other way
   * around.
   *
   * @param o The object instance that should be compared with <code>this</code>.
   * @return <code>true</code> if this command descriptor is equal to <code>o</code> or <code>null</code> when
   *   <code>o</code> is not a <code>CommandDescriptor</code> instance or when it is not the same object.
   */
  public boolean equals(Object o) {

    if (o instanceof CommandDescriptor) {

      Set s1 = aliasList;
      Set s2 = ((CommandDescriptor) o).aliasList;

      for (Iterator i = s1.iterator(); i.hasNext();) {
        if (s2.contains((String) i.next())) {
          return true;
        }
      }

      for (Iterator i = s2.iterator(); i.hasNext();) {
        if (s1.contains((String) i.next())) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }

//
//  public int hashCode() {
//    return (aliasList == null ? 0 : aliasList.hashCode());
//  }

  public String toString() {

    String a = "";
    for (Iterator i = aliasList.iterator(); i.hasNext();) {
      a += (String) i.next();
      if (i.hasNext()) {
        a += ", ";
      }
    }
    return name + " (" + a + ")";
  }
}


