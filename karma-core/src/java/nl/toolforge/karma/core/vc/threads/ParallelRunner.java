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

import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.cvs.threads.CVSLogThread;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.CommandContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

  private boolean initializing = true;
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

    initializing = true;

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      try {
        Constructor constructor = impl.getConstructor(new Class[]{Module.class});
        RunnerThread t = (RunnerThread) constructor.newInstance(new Object[]{(Module) i.next()});

//        Constructor constructor = impl.getConstructor(new Class[]{Module.class, Manifest.class});
//        RunnerThread t = (RunnerThread) constructor.newInstance(new Object[]{(Module) i.next(), manifest});

        threads[index] = t;
      } catch (Exception e) {
        e.printStackTrace();
        throw new KarmaRuntimeException("Could not start a RunnerThread instance.");
      }

      threads[index].start();
      index++;
    }

    initializing = false;
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

  /**
   * Determines if all tasks are finished. This method is non-blocking and should be called continuously if all tasks
   * are required to have finished before the transaction is complete.
   *
   * @return <code>true</code> if all tasks are finished.
   */
  public boolean finished() {

    if (initializing) {
      // This class should have started all threads first, before this method can execute properly.
      //
      return false;
    }

    int totalThreads = threads.length;
    int runningThreads = threads.length;

    for (int i = 0; i < totalThreads; i++) {

      boolean runningThread = threads[i].isRunning();
      runningThreads -= (runningThread ? 0 : 1);

      if (!runningThread) {
        addResult(threads[i].getModule(), threads[i].getResult());
      }
    }
    return (runningThreads == 0);
  }
}
