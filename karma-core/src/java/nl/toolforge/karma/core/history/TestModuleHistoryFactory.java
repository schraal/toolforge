package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.Version;

import java.io.File;
import java.util.Date;

/**
 * @author W.H. Schraal
 */
public class TestModuleHistoryFactory extends LocalCVSInitializer {

  public void testGetSetHistory() {
      try {
          checkoutDefaultModule1();

          File projectRoot = getDevelopmentHome();
          ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(projectRoot);
          assertNotNull(factory);

          ModuleHistory moduleHistory = factory.getModuleHistory(DEFAULT_MODULE_1);
          assertNotNull(moduleHistory);

          ModuleHistoryEvent event = new ModuleHistoryEvent();

          event.setType(ModuleHistoryEvent.CREATE_MODULE_EVENT);
          event.setDatetime(new Date());
          event.setAuthor("me");
          event.setComment("new module created");
          event.setVersion(new Version("0-0"));

          moduleHistory.addEvent(event);
          moduleHistory.save();

          assertTrue(true);
      } catch (Exception e) {
          fail(e.getMessage());
      }
  }

}
