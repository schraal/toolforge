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
package nl.toolforge.karma.core.manifest.util;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.SourceModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
