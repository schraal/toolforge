/*
Karma Launcher
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

package nl.toolforge.karma.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author oosterom
 */
public class KarmaClassLoader extends URLClassLoader {
     KarmaClassLoader(ClassLoader parentLoader) {
        super(new URL[0], parentLoader);
    }

    /**
     * Overloaded version of <code>addRepository(File repository)</code>
     */
    public void addClassPathEntry(String classPathEntry) throws FileNotFoundException {
        addClassPathEntry(new File(classPathEntry));
    }

    /**
     * Add a file to the classpath of the classloader. If the file is a regular
     * file and ends with ".jar/.zip" it is added to the classpath. If the file
     * is a directory the directory is added to the classpath. If the directory
     * contains jar/zip files, also the jar/zip files are added to the classpath
     * 
     * @param classPathEntry
     *            The file to be added to the classpath
     */
    public void addClassPathEntry(File classPathEntry) throws FileNotFoundException {
        // If the repository does not exist, we are ready
        //
        if (classPathEntry == null) {
            throw new NullPointerException("classPathEntry == null.");
        }

        if (!classPathEntry.exists()) {
            throw new FileNotFoundException(classPathEntry.getName()
                    + " does not exist.");
        }

        // We need a try block to wrap MalformedURLExceptions, which
        // are rethrown as a (runtime) IllegalArgumentException.
        //
        try {
            // If the repository is a regular file, and ends with .jar or zip
            // we are supposed to add it to the classpath and we're done
            //
            if (classPathEntry.isFile()) {
                if (classPathEntry.getAbsolutePath().endsWith(".jar")
                        || classPathEntry.getAbsolutePath().endsWith(".zip")) {
                    addURL(classPathEntry.toURL());
                }

                return;
            }

            // Now, the repository seems to be a directory, so we'll add
            // the directory to the classpath
            //
            addURL(classPathEntry.toURL());

            // The directory might contain jar/zip files, these should also be
            // added, so try to find jar/zip files.
            //
            File[] jars = classPathEntry.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jar") || name.endsWith(".zip")) {
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
        } catch (MalformedURLException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}