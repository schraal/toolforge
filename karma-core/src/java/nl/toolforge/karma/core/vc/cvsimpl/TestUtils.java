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
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.model.MainLine;

public class TestUtils extends LocalCVSInitializer {

  public void testPatchLine() {

    try {

      Module module = checkoutDefaultModuleWithVersion();
      module.markPatchLine(true);

      DevelopmentLine line = module.getPatchLine();

      assertEquals(
          new CVSTag("PATCHLINE|p_" + Version.INITIAL_VERSION.createPatch(0).getVersionNumber()),
          Utils.createSymbolicName(module, line, Version.INITIAL_VERSION.createPatch(0))
      );

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testMainLine() {

    try {
      assertEquals(
          new CVSTag(MainLine.NAME_PREFIX + "_0-0"),
          Utils.createSymbolicName(checkoutDefaultModuleWithVersion(), Version.INITIAL_VERSION)
      );

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


}
