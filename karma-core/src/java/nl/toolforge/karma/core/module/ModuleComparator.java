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
package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.module.Module;

import java.util.Comparator;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleComparator implements Comparator {

  public int compare(Object o1, Object o2) {

    if ((!(o1 instanceof Module)) || (!(o1 instanceof Module))) {
      throw new KarmaRuntimeException("Don't compare apples with pears; they are not the same ... (use Module instances).");
    }

    String n1 = ((Module) o1).getName();
    String n2 = ((Module) o2).getName();

    return n1.compareTo(n2);
  }
}
