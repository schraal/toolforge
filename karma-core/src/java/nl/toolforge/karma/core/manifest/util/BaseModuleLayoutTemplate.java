package nl.toolforge.karma.core.manifest.util;

import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.KarmaRuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class BaseModuleLayoutTemplate implements ModuleLayoutTemplate {

  protected static Log logger = LogFactory.getLog(BaseModuleLayoutTemplate.class);

  public abstract FileTemplate[] getFileElements();

  public abstract String[] getDirectoryElements();

  public final void createLayout(File baseDir) throws IOException {

    FileTemplate[] fileTemplates = getFileElements();
    String[] templateFiles = new String[fileTemplates.length];

    for (int i = 0; i < fileTemplates.length; i++) {

      FileTemplate fileTemplate = fileTemplates[i];
      logger.debug("Write template '" + fileTemplate.getSource() + "' to '" + fileTemplate.getTarget() + "'.");
      Reader input = new BufferedReader(new InputStreamReader(SourceModule.class.getResourceAsStream(fileTemplate.getSource().toString().replace('\\','/'))));

      File outputFile = new File(baseDir + File.separator + fileTemplate.getTarget());
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();

      FileOutputStream output = new FileOutputStream(outputFile);

      while (input.ready()) {
        output.write(input.read());
      }
      templateFiles[i] = fileTemplate.getTarget().getPath();
      logger.debug("Wrote template.");
    }

    // Create directories.
    //
    String[] dirs = getDirectoryElements();

    for (int i=0; i < dirs.length; i++) {

      File dirToAdd = new File(baseDir, dirs[i]);

      // Try to create the dirs
      if (!dirToAdd.mkdirs() && !dirToAdd.exists()) {
        // Failed to create the dirs and they do not exist yet.
        //
        throw new KarmaRuntimeException("Error while creating module layout.");
      }
    }
  }
}
