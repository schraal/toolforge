package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.model.MainLine;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;

public class TestUtils extends LocalCVSInitializer {

  public void testPatchLine() {

    try {

      Module module = checkoutDefaultModuleWithVersion();
      module.markPatchLine(true);

      DevelopmentLine line = module.getPatchLine();

      assertEquals(
          new CVSTag("PATCHLINE|p_" + Version.INITIAL_VERSION.createPatch("0").getVersionNumber()),
          Utils.createSymbolicName(module, line, Version.INITIAL_VERSION.createPatch("0"))
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
