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
package nl.toolforge.karma.core.scm.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;
import nl.toolforge.karma.core.KarmaException;


/**
 * This class is a lightweight reader for Maven project.xml's. Using the Maven stuff itself sucks, as it
 * is very heavy stuff for the purpose of reading the project's dependencies only.
 *
 * @author D.A. Smedes
 */
public class MavenDependencyReader {

  // todo name the correct maven dependency
  //
  /**
   * Parses a Maven project.xml file and stores all <code>&lt;dependency&gt;</code>-elements in a <code>List</code>.
   *
   * @param dependencyFileIs
   * @return A <code>List</code>, containing <code>org.apache.maven.project.Dependency</code> instances.
   */
 public List parse(InputStream dependencyFileIs) throws KarmaException {

    Digester digester = new Digester();

    digester.addObjectCreate("project", ArrayList.class);
    digester.addObjectCreate("project/dependencies/dependency", "org.apache.maven.project.Dependency");

    digester.addCallMethod("project/dependencies/dependency/Id", "setId", 1);
    digester.addCallParam("project/dependencies/dependency/Id", 0);
    digester.addCallMethod("project/dependencies/dependency/groupId", "setGroupId", 1);
    digester.addCallParam("project/dependencies/dependency/groupId", 0);
    digester.addCallMethod("project/dependencies/dependency/artifactId", "setArtifactId", 1);
    digester.addCallParam("project/dependencies/dependency/artifactId", 0);
    digester.addCallMethod("project/dependencies/dependency/version", "setVersion", 1);
    digester.addCallParam("project/dependencies/dependency/version", 0);
    digester.addCallMethod("project/dependencies/dependency/jar", "setJar", 1);
    digester.addCallParam("project/dependencies/dependency/jar", 0);

    // Call List.add(Dependency)
    //
    digester.addSetNext("project/dependencies/dependency", "add", "org.apache.maven.project.Dependency");

    List deps = null;
    try {
      deps = (List) digester.parse(dependencyFileIs);
    } catch (IOException e) {
      e.printStackTrace();
//      throw new KarmaException(KarmaException.LAZY_BASTARD);
    } catch (SAXException e) {
      e.printStackTrace();
//      throw new KarmaException(KarmaException.LAZY_BASTARD);
    }

    return deps;
  }

  public List parse(File projectXmlFile) throws KarmaException {
    try {
      return parse(new FileInputStream(projectXmlFile));
    } catch (FileNotFoundException e) {
      throw new KarmaException(KarmaException.NO_MAVEN_PROJECT_XML);
    }
  }
}

