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
package nl.toolforge.karma.core.vc.threads;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.Module;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The ParallelRunner handles parallel <code>RunnerThread</code>s. This concept should be used when parallel read
 * actions on version control repositories are possible to speed up the process of performing commands for
 * <strong>each</strong> module in a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ParallelRunner {

  private Map results = null;

  private Manifest manifest = null;
  private RunnerThread[] threads = null;

  private Class impl = null;

  /**
   * Initializes this <code>ParallelRunner</code> with the correct <code>Manifest</code>.
   *
   * @param manifest The manifest.
   * @param threadClass 
   */
  public ParallelRunner(Manifest manifest, Class threadClass) {
    this.manifest = manifest;
    this.impl = threadClass;
  }

  /**
   *
   */
  public void execute() {

    // todo abstract should work for all subtypes...

    Map modules = manifest.getAllModules();

    // Initialize status overview map
    //
    results = new HashMap();

    // Initialize an array of threads.
    //
    threads = new RunnerThread[modules.size()];

    int index = 0;

    // Start each task in parallel ...
    //
    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      try {
        Constructor constructor = impl.getConstructor(new Class[]{Module.class});
        threads[index] = (RunnerThread) constructor.newInstance(new Object[]{(Module) i.next()});
      } catch (Exception e) {
        throw new KarmaRuntimeException("Could not start a RunnerThread instance.");
      }

      threads[index].start();
      index++;
    }

    for (int i = 0; i < threads.length; i++) {

      try {
        threads[i].join();
        addResult(threads[i].getModule(), threads[i].getResult());
      } catch (InterruptedException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }
  }

  private void addResult(Module module, RunnerResult result) {
    results.put(module, result);
  }

  /**
   * Returns a map of {@link nl.toolforge.karma.core.vc.ModuleStatus} objects, each accessible by the the corresponding
   * {@link nl.toolforge.karma.core.manifest.Module} instance.
   *
   * @return
   */
  public Map retrieveResults() {
    return results;
  }

}
