package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.model.SourceModuleDescriptor;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.model.MainLine;

public class TestUtils extends LocalCVSInitializer {

  public void testCreateSymbolicName() {

//		class SourceModuleFaker extends SourceModule {
//			public SourceModuleFaker() throws KarmaException {
//				super("DUMMY", getTestLocation());
//			}
//		}

    try {

      SourceModule module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());



//			SourceModuleFaker m = new SourceModuleFaker();
//			module.setVersion(new Version("0-1"));

      // First test, apparently on the MAINLINE of a module.
      //
      assertEquals(new CVSTag(MainLine.NAME_PREFIX + "_0-0"), Utils.createSymbolicName(module, new Version("0-0")));

      module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation(), new DevelopmentLine("B1")), getDevelopmentHome());

//			module.setDevelopmentLine(new DevelopmentLine("B1"));

      assertEquals(new CVSTag("B1_0-2"), Utils.createSymbolicName(module, new Version("0-2")));

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


}
