package nl.toolforge.karma.launcher;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The KarmaClassLoader is a kind of URL class loader, with the extra ability 
 * to add directories or (jar) files after creation of the class loader with the addRepository 
 * methods. 
 *
 * @author W.M. Oosterom
 */
public final class KarmaClassLoader
	extends URLClassLoader
{
	private String name = null;

	/**
	 * @param name The name of the classloader
	 */
	KarmaClassLoader(String name, ClassLoader parentLoader) {
		super(new URL[0], parentLoader);
		this.name = name;
	}

	/**
	 * @return The name of the classloader
	 */
	String getName() {
		return name;
	}

	/**
	 * Overloaded version of <code>addRepository(File repository)</code>
	 */
	public void addRepository(String repository) throws FileNotFoundException {
		addRepository(new File(repository));
	}

	/**
	 * Add a file to the classpath of the classloader.
	 * If the file is a regular file and ends with ".jar"
	 * it is added to the classpath. If the file is a directory
	 * the directory is added to the classpath. If the directory
	 * contains jars files, also the jar files are added to the
	 * classpath
	 *
	 * @param repository The file to be added to the classpath
	 */
	public void addRepository(File repository) throws FileNotFoundException {
		// If the repository does not exist, we are ready
		//
		if (repository == null) {
			throw new NullPointerException("repository == null.");
		}

		if (!repository.exists()) {
			throw new FileNotFoundException(repository.getName() + " does not exist.");
		}

		// We need a try block to wrap MalformedURLExceptions, which
		// are rethrown as a (runtime) IllegalArgumentException.
		//
		try {
			// If the repository is a regular file, and ends with .jar
			// we are supposed to add it to the classpath and we're done
			//
			if (repository.isFile()) {
				if (repository.getAbsolutePath().endsWith(".jar")) {
					addURL(repository.toURL());
				}
				return;
			}

			// Now, the repository seems to be a directory, so we'll add
			// the directory to the classpath
			//
			addURL(repository.toURL());

			// The directory might contain jar files, these should also be added,
			// so try to find jars
			//
			File[] jars = repository.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".jar")) {
						return true;
					} else {
						return false;
					}
				}
			});

			// Add the jars to the classpath
			//
			if (jars != null) {
				for (int i = 0; i < jars.length; i++) {
					addURL(jars[i].toURL());
				}
			}
		}
		catch (MalformedURLException e) {
			throw new FileNotFoundException(e.getMessage());
		}

	}
}






