/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.cli;

import org.apache.commons.lang.StringUtils;

/**
 * Writer to <code>System.out</code>. This class provides a helper to write output to the console.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ConsoleWriter {

  private boolean defaultPrompt = false;

  /**
   * Creates a <code>ConsoleWriter</code> to be able to write command output to <code>System.out</code>. A
   * <code>boolean</code> value can be provided to determine whether to use the defaultPrompt or
   *
   * @param defaultPrompt <code>true</code> When the default prompt should be used
   *                      ({@link ConsoleConfiguration#getDefaultPrompt}), or <code>false</code> when the user defined
   *                      prompt ({@link ConsoleConfiguration#getPrompt}) should be used.
   */
  public ConsoleWriter(boolean defaultPrompt) {
    this.defaultPrompt = defaultPrompt;
  }

  /**
   * Writes the prompt to the console, without moving the cursor to the next line.
   */
  public void prompt() {
    System.out.print(getPrompt());
  }

  public String getPrompt() {

    String prompt = null;

    if (defaultPrompt) {
      prompt = ConsoleConfiguration.getDefaultPrompt();
    } else {
//			prompt = Preferences.getInstance().get("", ConsoleConfiguration.getDefaultPrompt());
      // TODO based on any logic like the PS stuff on Unix, do something with the prompt.
    }
    return prompt.trim().concat(" "); // Remove too many spaces, and add one for readability.
  }

//  /**
//   * Writes <code>text</code> to <code>System.out</code>, preceded with {@link ConsoleConfiguration#getPrompt}.
//   *
//   * @param text Text to write to the console.
//   */
//  public void write(String text) {
//    System.out.println(getPrompt());
//    System.out.print(text);
//  }

  public void showProgress() {
    System.out.print(".");
  }

  /**
   * Writes <code>text</code> to <code>System.out</code>, preceded with {@link ConsoleConfiguration#getPrompt} and
   * followed by a newline.
   *
   * @param text Text to write to the console.
   */
  public void writeln(String text) {
    //System.out.println(getPrompt());
    System.out.println(text);
  }

//  public void writeln(boolean writePrompt, String text) {
//    System.out.println(StringUtils.repeat(" ", getPrompt().length()).concat(text));
//  }
//  public void writeln(boolean writePrompt, String text) {
//    if (writePrompt) {
//      writeln(text);
//    } else {
//      System.out.println(StringUtils.repeat(" ", getPrompt().length()).concat(text));
//    }
//  }

  public void blankLine() {
    System.out.println("");
  }

  public void newLine() {
    System.out.println("");
  }

}