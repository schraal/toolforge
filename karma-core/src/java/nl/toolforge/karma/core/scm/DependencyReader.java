package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * A <code>DependencyReader</code> is responsible for reading dependencies for a specific module type.
 *
 * @author D.A. Smedes
 */
public interface DependencyReader {

  /**
   * Parses <code>dependencyFile</code> and returns containing dependencies in a <code>List</code>.
   *
   * @param dependencyFile
   */
  public List parse(File dependencyFile) throws KarmaException;

  /**
   * Parses <code>dependencyFileIs</code> and returns containing dependencies in a <code>List</code>.
   *
   * @param dependencyFileIs
   */
  public List parse(InputStream dependencyFileIs) throws KarmaException;

}
