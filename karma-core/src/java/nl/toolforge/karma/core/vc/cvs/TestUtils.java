package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.model.MainLine;

public class TestUtils extends LocalCVSInitializer {

  public void testCreateSymbolicName() {

    try {

      SourceModule module = new SourceModule("a", getTestLocation());

      // First test, apparently on the MAINLINE of a module.
      //
      assertEquals(new CVSTag(MainLine.NAME_PREFIX + "_0-0"), Utils.createSymbolicName(module, Version.INITIAL_VERSION));

      module = new SourceModule("a", getTestLocation(), new DevelopmentLine("B1"));

      assertEquals(new CVSTag("B1_0-2"), Utils.createSymbolicName(module, new Version("0-2")));

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


}
