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
package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.test.LocalCVSInitializer;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author W.H. Schraal
 */
public class TestModuleHistoryFactory extends LocalCVSInitializer {

  public void testGetSetHistory() {
      try {
        checkoutDefaultModule1();

        File projectRoot = getWorkingContext().getProjectBaseDirectory();
        ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(projectRoot);
        assertNotNull(factory);

        ModuleHistory moduleHistory = factory.getModuleHistory(checkoutDefaultModule1());
        assertNotNull(moduleHistory);

        ModuleHistoryEvent event = new ModuleHistoryEvent();

        event.setType(ModuleHistoryEvent.CREATE_MODULE_EVENT);
        Date time = new Date();
        event.setDatetime(time);
        try {
          event.setAuthor(null);
          assertTrue("Excepted an exception here, since the author is null", false);
        } catch (IllegalArgumentException iae) {
          assertTrue(true);
        }
        event.setAuthor("me");
        event.setComment("new module created");
        event.setVersion(new Version("0-0"));

        moduleHistory.addEvent(event);
        moduleHistory.save();

        assertTrue(true);

        List events = moduleHistory.getEvents();
        assertEquals(events.size(), 3);
        ModuleHistoryEvent event2 = (ModuleHistoryEvent)events.get(2);
        assertEquals(event2, event);
        assertEquals(event2.getAuthor(), "me");
        assertEquals(event2.getComment(), "new module created");
        assertEquals(event2.getDatetime(), time);
        assertEquals(event2.getType(), ModuleHistoryEvent.CREATE_MODULE_EVENT);
        assertEquals(event2.getVersion(), new Version("0-0"));
      } catch (Exception e) {
          fail(e.getMessage());
      }
  }

}
