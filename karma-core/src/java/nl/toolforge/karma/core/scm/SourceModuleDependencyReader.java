package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.digester.Digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


/**
 * <p>Reads dependencies from a <code>module.xml</code>, contained in a <code>SourceModule</code>.
 * <code>SourceModule</code>s define dependencies in the following format:</p>
 *
 * <pre>
 *   &lt;dependencies&gt;
 *     &lt;dependency&gt;
 *     &lt;/dependency&gt;
 *   &lt;/dependencies&gt;
 *
 * </pre>
 *
 * @author D.A. Smedes
 */
public class SourceModuleDependencyReader implements DependencyReader {

  /**
   * Parses a modules <code>module.xml</code> file and stores all <code>&lt;dependency&gt;</code>-elements in a
   * <code>List</code>.
   *
   * @param dependencyFileIs
   * @return A <code>List</code>, containing {@link nl.toolforge.karma.core.scm.ModuleDependency} instances.
   */
 public List parse(InputStream dependencyFileIs) throws KarmaException {

    Digester digester = new Digester();

//    digester.addObjectCreate("project", "java.util.ArrayList");
//    digester.addObjectCreate("project/dependencies/dependency", "org.apache.maven.project.Dependency");
//
//    digester.addCallMethod("project/dependencies/dependency/Id", "setId", 1);
//    digester.addCallParam("project/dependencies/dependency/Id", 0);
//    digester.addCallMethod("project/dependencies/dependency/groupId", "setGroupId", 1);
//    digester.addCallParam("project/dependencies/dependency/groupId", 0);
//    digester.addCallMethod("project/dependencies/dependency/artifactId", "setArtifactId", 1);
//    digester.addCallParam("project/dependencies/dependency/artifactId", 0);
//    digester.addCallMethod("project/dependencies/dependency/version", "setVersion", 1);
//    digester.addCallParam("project/dependencies/dependency/version", 0);
//    digester.addCallMethod("project/dependencies/dependency/jar", "setJar", 1);
//    digester.addCallParam("project/dependencies/dependency/jar", 0);
//
//    // Call List.add(Dependency)
//    //
//    digester.addSetNext("project/dependencies/dependency", "add", "org.apache.maven.project.Dependency");

    List deps = null;
//    try {
//      deps = (List) digester.parse(dependencyFileIs);
//    } catch (IOException e) {
//      throw new KarmaException(KarmaException.LAZY_BASTARD);
//    } catch (SAXException e) {
//      throw new KarmaException(KarmaException.LAZY_BASTARD);
//    }

    return deps;
  }

  public List parse(File projectXmlFile) throws KarmaException {
    try {
      return parse(new FileInputStream(projectXmlFile));
    } catch (FileNotFoundException e) {
      throw new KarmaException(KarmaException.LAZY_BASTARD);
    }
  }
}

