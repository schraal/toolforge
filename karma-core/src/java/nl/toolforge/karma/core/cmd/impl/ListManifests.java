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
package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestHeader;
import org.apache.commons.digester.Digester;
import org.apache.tools.ant.DirectoryScanner;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation for the ListManifests command.
 *
 * @author D.A. Smedes
 * @version @version $Id$
 */
public class ListManifests extends DefaultCommand {

  private Set headers = new HashSet();

  /**
   * This constructor performs all generic (enduser-independent functionality).
   *
   * @throws ManifestException See {@link nl.toolforge.karma.core.KarmaException#MANIFEST_STORE_NOT_FOUND}.
   */
  public ListManifests(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  /**
   * Gets the commands' response object.
   *
   * @return The commands' response object.
   */
  public CommandResponse getCommandResponse() {
    return null;
  }

  /**
   * Scans the manifest store and parses all manifest files.
   *
   * @throws CommandException 
   */
  public void execute() throws CommandException {

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(getWorkingContext().getManifestStore());
    scanner.setIncludes(new String[] {"**/*.xml"});
    scanner.scan();

    String[] manifestFiles = scanner.getIncludedFiles();

    for (int i = 0; i < manifestFiles.length; i++) {
      try {
        headers.add((ManifestHeader) getDigester().parse(new File(getWorkingContext().getManifestStore(), manifestFiles[i])));
      } catch (IOException e) {
        throw new CommandException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifestFiles[i]});
      } catch (SAXException e) {
        e.printStackTrace();
        throw new CommandException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifestFiles[i]});
      }
    }
  }

  /**
   * Returns the headers found in the manifest store. The set contains <code>ManifestHeader</code> instances.
   *
   * @return A <code>Set</code> of <code>ManifestHeader</code> instances.
   */
  protected Set getHeaders() {
    return headers;
  }

  private Digester getDigester() {

    Digester digester = new Digester();
    digester.addObjectCreate("manifest", ManifestHeader.class);
    digester.addSetProperties("manifest");

    return digester;
  }

}