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

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.manifest.digester.ModuleDescriptorCreationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Factory class to create {@link nl.toolforge.karma.core.manifest.ManifestStructure} instances.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestLoader {

  private static Log logger = LogFactory.getLog(ManifestLoader.class);

  private WorkingContext workingContext = null;

  public ManifestLoader(WorkingContext workingContext) {
    this.workingContext = workingContext;
  }

  /**
   * This method builds a tree of <code>ManifestStructure</code> instances, representing the root manifest and all its
   * included manifests.
   *
   * @param manifest The manifest name.
   */
  public ManifestStructure load(String manifest) throws ManifestException {

    Digester digester = new Digester();

    // The <manifest>-element
    //
    digester.addObjectCreate("manifest", ManifestStructure.class);
    digester.addSetProperties("manifest");
    digester.addCallMethod("manifest/description", "setDescription", 0);

    // All <module>-elements
    //
    digester.addFactoryCreate("*/module", ModuleDescriptorCreationFactory.class);
    digester.addSetProperties("*/module");
    digester.addSetNext("*/module", "addModule", "nl.toolforge.karma.core.manifest.ModuleDescriptor");

    // All <include-manifest>-elements
    //
    digester.addObjectCreate("*/include-manifest", "nl.toolforge.karma.core.manifest.ManifestStructure");
    digester.addSetProperties("*/include-manifest");
    digester.addSetNext("*/include-manifest", "addChild");

    ManifestStructure structure = null;
    try {
      structure = (ManifestStructure) digester.parse(getManifestFileAsStream(manifest));
    } catch (IOException e) {
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest});
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        throw new ManifestException(
            ((ManifestException) e.getException()).getErrorCode(),
            ((ManifestException) e.getException()).getMessageArguments()
        );
      } else {
        throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest});
      }
    }

    Iterator i = structure.getChilds().values().iterator();

    while (i.hasNext()) {
      String childName = ((ManifestStructure) i.next()).getName();
      ManifestStructure child = load(childName);
      structure.getChild(childName).update(child);
    }

    return structure;
  }

  private InputStream getManifestFileAsStream(String id) throws ManifestException {

    try {
      String fileName = (id.endsWith(".xml") ? id : id.concat(".xml"));

      if (fileName.endsWith(File.separator)) {
        fileName = fileName.substring(0, fileName.length() - 1);
      }
      fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

      logger.debug("Loading manifest " + fileName + " from " + workingContext.getManifestStore().getPath() + File.separator + fileName);

      return new FileInputStream(workingContext.getManifestStore().getPath() + File.separator + fileName);

    } catch (FileNotFoundException f) {
      throw new ManifestException(f, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    } catch (NullPointerException n) {
      throw new ManifestException(n, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    }
  }

}
