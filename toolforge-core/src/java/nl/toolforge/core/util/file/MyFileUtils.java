/*
Toolforge core - Core of the Toolforge application suite
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
package nl.toolforge.core.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Random;


/**
 * File utilities.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class MyFileUtils {

	public static File createTempDirectory() throws IOException {

		Random randomizer = new Random();

		int someInt = randomizer.nextInt();
		someInt = (someInt< 0 ? someInt * -1 : someInt); // > 0

		File tmp = File.createTempFile("" + someInt, null);
		tmp.delete();
		tmp.mkdir();

		return tmp;
	}
}
