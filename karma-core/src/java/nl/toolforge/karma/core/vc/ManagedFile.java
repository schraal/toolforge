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
package nl.toolforge.karma.core.vc;

/**
 * Instances of this type represent files that are being managed by a version control system. A checkout of a module
 * is combined with generated files and directories etc. due to code building and other file processing (temporary
 * files). <code>ManagedFile</code>s have a reference in a version control system. CVS for example uses a
 * <code>CVS</code> directory in each subdirectory of a sourcetree to maintain meta-information. Only
 * <code>ManagedFile</code>s can be committed to a version control system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface ManagedFile {
}
