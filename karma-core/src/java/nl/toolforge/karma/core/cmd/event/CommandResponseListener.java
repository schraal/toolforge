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
package nl.toolforge.karma.core.cmd.event;

/**
 * When CommandResponses change, classes that implement this interface (and register themselves)
 * recieve a CommandResponseEvent.
 */
public interface CommandResponseListener {

  public void commandHeartBeat();

  /**
   * Called when a CommandResponse has changed, for example when a new CommandMessage has been added.
   *
   * @param event  The event that described what has changed in the CommandResponse, so that the Listener
   *               can react adequately.
   */
  public void commandResponseChanged(CommandResponseEvent event);

  /**
   * Called when a command is finished. Especially usefull when an interactive system is generating a lot of response
   * and the end of the full 'transaction' must be fetched.
   *
   * @param event An event; can be <code>null</code>.
   */
  public void commandResponseFinished(CommandResponseEvent event);

}
