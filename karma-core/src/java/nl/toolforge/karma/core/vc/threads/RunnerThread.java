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
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.Module;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class RunnerThread extends Thread {

  private boolean running = true;

  protected Throwable exception = null;
  protected RunnerResult result = null;
  private Module module = null;

  public RunnerThread(Module module) {
    this.module = module;
  }

  /**
   * Must be implemented by subclasses. The subclass is where all logic is performed.
   */
  public abstract void run();

  /**
   * Should be called to demarkate the start of the 'transaction'.
   */
  protected void startRunning() {
    running = true;
  }

  /**
   * Should be called to demarkate the end of the 'transaction'.
   */
  protected void stopRunning() {
    running = false;
  }

  /**
   * Should be checked to ensure that <code>run()</code> is finished.
   *
   * @return
   */
  public final boolean isRunning() {
    return running;
  }

  public final RunnerResult getResult() {
    return result;
  }

  /**
   * Returns the module status, irrespective of whether this thread has finished executing or not. This check can be
   * performed by calling {@link #isRunning()}.
   *
   * @return The ModuleStatus instance generated based on what the {@link #run()} has done when executed properly.
   */
  public Module getModule() {
    if (module == null) {
      throw new KarmaRuntimeException("Module instance has not been set.");
    }
    return module;
  }

  public Throwable getException() {
    return exception;
  }

}
