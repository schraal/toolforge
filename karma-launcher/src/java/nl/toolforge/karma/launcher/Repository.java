package nl.toolforge.karma.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author W.M. Oosterom
 */
final class Repository
{

	// Singleton implementation
	//
	private static Repository instance = null;

	static synchronized Repository getInstance() throws IOException {
		if (instance == null) {
			instance = new Repository();
		}
		return instance;
	}

	private LocalRepository localRepository = null;
	private RemoteRepository remoteRepository = null;

	private Repository() throws IOException {
		localRepository = LocalRepository.getInstance();
		remoteRepository = RemoteRepository.getInstance();
	}

	File getJarByVersion(String id, String version) throws FileNotFoundException, IOException {
		File jarFile = null;

		try {
			jarFile = localRepository.getJarByVersion(id, version);
		}
		catch (FileNotFoundException fnfe) {
			jarFile = remoteRepository.getJarByVersion(id, version);
			localRepository.putJarByVersion(id, version, jarFile);
			jarFile = localRepository.getJarByVersion(id, version);
		}

		return jarFile;
	}

	File getJarByName(String id, String name) throws FileNotFoundException, IOException {

		File jarFile = null;

		try {
			jarFile = localRepository.getJarByName(id, name);
		}
		catch (FileNotFoundException fnfe) {
			jarFile = remoteRepository.getJarByName(id, name);
			localRepository.putJarByName(id, name, jarFile);
			jarFile = localRepository.getJarByName(id, name);
		}

		return jarFile;
	}
}





