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

import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.LibModule;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.module.JavaEnterpriseApplicationModule;
import nl.toolforge.karma.core.module.JavaWebApplicationModule;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleCreation extends LocalCVSInitializer {

  public void testCreateRemote() {

    Module module = null;
    try {

      module = new SourceModule("A", getTestLocation());
      module.createRemote("Unit testing ...");
      assertTrue(RunnerFactory.getRunner(getTestLocation()).existsInRepository(module));

      RunnerFactory.getRunner(getTestLocation()).checkout(module);
      assertEquals(module.getType(), Module.JAVA_SOURCE_MODULE);

      module = new JavaEnterpriseApplicationModule("B", getTestLocation());
      module.createRemote("Unit testing ...");
      assertTrue(RunnerFactory.getRunner(getTestLocation()).existsInRepository(module));

      RunnerFactory.getRunner(getTestLocation()).checkout(module);
      assertEquals(module.getType(), Module.JAVA_ENTERPRISE_APPLICATION);

      module = new JavaWebApplicationModule("C", getTestLocation());
      module.createRemote("Unit testing ...");
      assertTrue(RunnerFactory.getRunner(getTestLocation()).existsInRepository(module));

      RunnerFactory.getRunner(getTestLocation()).checkout(module);
      assertEquals(module.getType(), Module.JAVA_WEB_APPLICATION);

      module = new LibModule("D", getTestLocation());
      module.createRemote("Unit testing ...");
      assertTrue(RunnerFactory.getRunner(getTestLocation()).existsInRepository(module));

      RunnerFactory.getRunner(getTestLocation()).checkout(module);
      assertEquals(module.getType(), Module.LIBRARY_MODULE);

    } catch (VersionControlException e) {
      fail();
    } catch (ModuleTypeException e) {
      fail();
    } catch (AuthenticationException e) {
      fail();
    }
  }

}
