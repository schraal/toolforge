package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.test.LocalCVSInitializer;

import java.io.File;

/**
 * @author W.H. Schraal
 */
public class TestModuleHistoryFactory extends LocalCVSInitializer {

    public void testGetHistory() {
        try {
            File projectRoot = getDevelopmentHome();
            ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(projectRoot);
            assertNotNull(factory);

            ModuleHistory moduleHistory = factory.getModuleHistory("core-1");
            assertNotNull(moduleHistory);

            moduleHistory.save();
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
