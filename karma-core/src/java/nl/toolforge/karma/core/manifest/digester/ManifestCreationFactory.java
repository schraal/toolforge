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
package nl.toolforge.karma.core.manifest.digester;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.DevelopmentManifest;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ReleaseManifest;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Creation factory to be able to create {@link nl.toolforge.karma.core.manifest.Manifest} instances. The reason to have this class
 * is that Digester at some point calls <code>hashCode()</code> on newly created <code>AbstractManifest</code> instances
 * and this fails with a <code>NullPointerException</code> if <code>name</code> is still
 * <code>null</code>; Digester somehow calls upon the setName() later on in the process.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCreationFactory extends AbstractObjectCreationFactory {

  /**
   * Called by the Digester framework to create the correct implementation of <code>Manifest</code>.
   *
   * @param attributes The attributes of the <code>&lt;manifest&gt;</code>-element in the manifest file.
   * @return An object of type <code>DevelopmentManifest</code> or an object of type <code>ReleaseManifest</code>.
   * @throws Exception
   */
  public Object createObject(Attributes attributes) throws Exception {

    String name = attributes.getValue("name");
    String type = attributes.getValue("type");

    if (Manifest.DEVELOPMENT_MANIFEST.equals(type)) {
      return new DevelopmentManifest(name);
    }
    if (Manifest.RELEASE_MANIFEST.equals(type)) {
      return new ReleaseManifest(name);
    }
    throw new KarmaRuntimeException("A manifest should be of type 'development' or of type 'release'.");
  }
}
