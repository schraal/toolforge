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
package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;

/**
 * <p>A <code>CompositeCommand</code> is suited for executing multiple commands and at the same time, act as a
 * <code>CommandResponseListener</code>. A good example is a command traversing all modules in a manifest and calling
 * some command on each of them.
 *
 * <p>When a <code>CompositeCommand</code> is registered as a listener to other commands, the composite command is
 * responsible .
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class CompositeCommand extends DefaultCommand implements CommandResponseListener {

  public CompositeCommand(CommandDescriptor commandDescriptor) {
    super(commandDescriptor);
  }

  /**
   * Implemenattion of {@link CommandResponseListener#commandStarted}. Made <code>final</code> because in Karma R1.0
   * we don't use this feature that much for <code>CompositeCommand</code>s.
   */
  public final void commandStarted(CommandResponseEvent event) { }

  /**
   * Implemenattion of {@link CommandResponseListener#commandFinished}. Made <code>final</code> because in Karma R1.0
   * we don't use this feature that much for <code>CompositeCommand</code>s.
   */
  public final void commandFinished(CommandResponseEvent event) { }

  /**
   * Implemenattion of {@link CommandResponseListener#messageLogged}. Made <code>final</code> because in Karma R1.0
   * we don't use this feature that much for <code>CompositeCommand</code>s.
   */
  public final void messageLogged(CommandResponseEvent event) { }

}
