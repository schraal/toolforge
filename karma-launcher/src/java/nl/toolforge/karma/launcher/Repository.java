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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author W.M. Oosterom
 */
final class Repository {

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

    File getJarByVersion(String id, String version)
            throws FileNotFoundException, IOException {
        File jarFile = null;

        try {
            jarFile = localRepository.getJarByVersion(id, version);
        } catch (FileNotFoundException fnfe) {
            jarFile = remoteRepository.getJarByVersion(id, version);
            localRepository.putJarByVersion(id, version, jarFile);
            jarFile = localRepository.getJarByVersion(id, version);
        }

        return jarFile;
    }

    File getJarByName(String id, String name) throws FileNotFoundException,
            IOException {

        File jarFile = null;

        try {
            jarFile = localRepository.getJarByName(id, name);
        } catch (FileNotFoundException fnfe) {
            jarFile = remoteRepository.getJarByName(id, name);
            localRepository.putJarByName(id, name, jarFile);
            jarFile = localRepository.getJarByName(id, name);
        }

        return jarFile;
    }
}

