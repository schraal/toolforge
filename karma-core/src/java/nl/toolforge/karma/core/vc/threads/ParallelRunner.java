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
import nl.toolforge.karma.core.module.Module;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

  Log logger = LogFactory.getLog(ParallelRunner.class);

  private Map results = null;

  private Manifest manifest = null;
  private RunnerThread[] threads = null;

  private Class impl = null;

  /**
   * Initializes this <code>ParallelRunner</code> with the correct <code>Manifest</code>. Call {@link #execute()} or
   * {@link #execute(int, long)} to start all threads.
   *
   * @param manifest    The manifest.
   * @param threadClass A Class instance, extending {@link Thread}.
   */
  public ParallelRunner(Manifest manifest, Class threadClass) {
    this.manifest = manifest;
    this.impl = threadClass;
  }

  /**
   * Starts all threads, and processes the results. Results can be retrieved by calling {@link #retrieveResults}.
   *
   * @param delayInMilliseconds  The delay in milliseconds between the start of each thread.
   */
  public void execute(long delayInMilliseconds) {
    execute(0, delayInMilliseconds);
  }


  /**
   * Starts all threads, and processes the results. Results can be retrieved by calling {@link #retrieveResults}. Note
   * that no restriction is imposed on the amount of threads, and they are started as soon as possible.
   *
   * @see #execute(long)
   * @see #execute(int, long)
   */
  public void execute() {
    execute(0,0);
  }


  /**
   * Starts all threads, and processes the results. Results can be retrieved by calling {@link #retrieveResults}.
   *
   * @param blockSize            Determines the amount of threads that will be started in one block, with a delay of
   *                             <code>delayInMilliseconds</code>. If all threads should be started as one block, a
   *                             negative <code>blockSize</code> should be provided. When a positive blocksize is
   *                             provided, a default delay of 1000 milliseconds is used between blocks. <b>Note</b>
   *                             this feature is currently ignored.
   * @param delayInMilliseconds  The delay in milliseconds between threads in a block (or all threads if
   *                             <code>blockSize</code> is negative.
   */
  public void execute(int blockSize, long delayInMilliseconds) {

    Map modules = manifest.getAllModules();

    // Initialize status overview map
    //
    results = new HashMap();

    // Initialize an array of threads.
    //
    threads = new RunnerThread[modules.size()];

    int index = 0;

    logger.debug("Starting " + modules.size() + " threads, with a delay of " + delayInMilliseconds + " ms.");

    // Start each task in parallel ...
    //
    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      try {
        Constructor constructor = impl.getConstructor(new Class[]{Module.class});
        threads[index] = (RunnerThread) constructor.newInstance(new Object[]{(Module) i.next()});
      } catch (Exception e) {
        logger.error(e);
        throw new KarmaRuntimeException("Could not start a RunnerThread instance.");
      }

      threads[index].start();
      try {
        Thread.sleep(delayInMilliseconds);
      } catch (InterruptedException e) {
        //
      }
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
   * {@link nl.toolforge.karma.core.module.Module} instance.
   *
   * @return A map, containing {@link nl.toolforge.karma.core.vc.ModuleStatus} objects.
   */
  public Map retrieveResults() {
    return results;
  }

}
