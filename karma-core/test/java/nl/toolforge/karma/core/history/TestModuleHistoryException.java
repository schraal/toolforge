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

import junit.framework.TestCase;

/**
 * @author W.H. Schraal
 */
public class TestModuleHistoryException extends TestCase {

  /**
   * Test the constructors of the ModuleHistoryException.
   */
  public void testModuleHistoryExceptionCreation() {
    ModuleHistoryException exception;
    exception = new ModuleHistoryException(ModuleHistoryException.HISTORY_FILE_DOES_NOT_EXIST);
    exception = new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE);

    Exception argException = new IllegalArgumentException();
    exception = new ModuleHistoryException(argException, ModuleHistoryException.HISTORY_FILE_DOES_NOT_EXIST);

    exception = new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE, new Object[]{"blaat"});
    exception = new ModuleHistoryException(argException, ModuleHistoryException.HISTORY_FILE_DOES_NOT_EXIST, new Object[]{"blaat"});

    assertNotNull(exception);
  }


  public void testGetMessage() {
    ModuleHistoryException exception = new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE, new Object[]{"blaat"});
    String message = exception.getMessage();
    assertNotNull(message);
  }

  public void testGetErrorCode() {
    ModuleHistoryException exception = new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE, new Object[]{"blaat"});
    assertEquals(exception.getErrorCode(), ModuleHistoryException.INVALID_HISTORY_FILE);
  }

  public void testGetMessageArguments() {
    Object[] arguments = new Object[]{"blaat", "hup", "sakee"};
    ModuleHistoryException exception = new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE, arguments);
    assertEquals(exception.getMessageArguments(), arguments);
  }
  
}
