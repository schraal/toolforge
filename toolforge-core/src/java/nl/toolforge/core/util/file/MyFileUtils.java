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
