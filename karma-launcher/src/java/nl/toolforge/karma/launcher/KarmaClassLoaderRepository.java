/*
Karma launcher - Library for launching a clean classloader from a Java application
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The KarmaClassLoaderRepository is a repository/factory for KarmaClassLoaders.
 * 
 * @author W.M. Oosterom
 */
public final class KarmaClassLoaderRepository {
    // Singleton implementation
    //
    private static KarmaClassLoaderRepository instance = null;

    /**
     * To use pre-defined classloaders, a system propety is used to define which
     * configuration file is used. The name of this system property
     * karma.classloaderrepository.config. The value of this system property
     * defaults to "karma.classloaderrepository.config". The value of the system
     * property is assumed to represent a file containing the classloader
     * repository configuration and this class tries to read this file from the
     * file system. If it can not be found on the file system, it is searched
     * for in the current classpath.
     */
    public static synchronized KarmaClassLoaderRepository getInstance()
            throws IOException, FileNotFoundException {
        if (instance == null) {
            instance = new KarmaClassLoaderRepository();

            instance.loadClassLoaders();
        }
        return instance;
    }

    private Map classLoaders = new HashMap();

    private KarmaClassLoaderRepository() {

        // Empty
    }

    /**
     * Get a class loader by name. If the class loader is not in the repository
     * then a brand new classloader is returned, otherwise the one found in the
     * repository is returned.
     */
    public KarmaClassLoader getClassLoader(String name) {
        synchronized (classLoaders) {
            if (!classLoaders.containsKey(name)) {
                return new KarmaClassLoader(name, this.getClass()
                        .getClassLoader());
            } else {
                return (KarmaClassLoader) classLoaders.get(name);
            }
        }
    }

    void addClassLoader(KarmaClassLoader classLoader) {

        synchronized (classLoaders) {
            classLoaders.put(classLoader.getName(), classLoader);
        }
    }

    /**
     * Predefined class loaders are read from a configuration input stream
     */
    private void loadClassLoaders() throws IOException, FileNotFoundException {

        String configFile = System.getProperty(
                "karma.classloaderrepository.config",
                "karma.classloaderrepository.config");

        InputStream in = null;
        try {
            //			System.out.println("Trying to read KarmaClassLoaderRepository
            // configuration " +
            //				"\"" + configFile + "\" from filesystem.");
            in = new FileInputStream(configFile);

            //			System.out.println("Found KarmaClassLoaderRepository
            // configuration on filesystem.");
        } catch (IOException e) {
            System.out
                    .println("Failed to read KarmaClassLoaderRepository configuration "
                            + "\""
                            + configFile
                            + "\" from filesystem, due to \""
                            + e.getMessage()
                            + "\", trying to find file in classpath.");
            if ((in = this.getClass().getClassLoader().getResourceAsStream(
                    configFile)) == null) {
                //				System.out.println("Failed to find KarmaClassLoaderRepository
                // configuration " +
                //					"\"" + configFile + "\" in classpath.");
                throw e;
            } else {
                //				System.out.println("Found KarmaClassLoaderRepository
                // configuration in classpath.");
            }
        }

        // We have found the inputstream from which to read the configuration
        // now we are going to parse this inputstream. The parser will add
        // classloaders to this repository, through callback
        //
        ConfigurationParser parser = new ConfigurationParser();
        parser.parse(in, this);
    }
}

