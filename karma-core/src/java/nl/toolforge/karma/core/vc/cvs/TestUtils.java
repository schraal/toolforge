package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.SourceModuleDescriptor;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.model.MainLine;

public class TestUtils extends LocalCVSInitializer {

  public void testCreateSymbolicName() {

    try {

      SourceModule module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation() ), getDevelopmentHome());

      // First test, apparently on the MAINLINE of a module.
      //
      assertEquals(new CVSTag(MainLine.NAME_PREFIX + "_0-0"), Utils.createSymbolicName(module, Version.INITIAL_VERSION));

      module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation(), new DevelopmentLine("B1")), getDevelopmentHome());

      assertEquals(new CVSTag("B1_0-2"), Utils.createSymbolicName(module, new Version("0-2")));

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


}
