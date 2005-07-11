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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class KarmaLauncher {

    public static final String KARMA_CLASSPATH = "KARMA_CLASSPATH";

    // Singleton implementation
    //
    private static KarmaLauncher instance = null;

    public static synchronized KarmaLauncher getInstance() {
        if (instance == null) {
            instance = new KarmaLauncher();
        }
        return instance;
    }

    private KarmaLauncher() {
        // Empty
    }

    public void invoke(String className, String methodName, Object[] args,
            String[] classPathEntries) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException, FileNotFoundException {
        File[] entries = null;

        if (classPathEntries != null) {
            entries = new File[classPathEntries.length];

            for (int i = 0; i < classPathEntries.length; i++) {
                entries[i] = new File(classPathEntries[i]);
            }
        }
        invoke(className, methodName, args, entries);
    }

    public void invoke(String className, String methodName, Object[] args,
            File[] classPathEntries) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException, FileNotFoundException {

        KarmaClassLoader classLoader = new KarmaClassLoader(this.getClass()
                .getClassLoader());
        if (classPathEntries != null) {
            for (int i = 0; i < classPathEntries.length; i++) {
                try {
                    classLoader.addClassPathEntry(classPathEntries[i]);
                } catch (FileNotFoundException e) {
                    System.out.println("[karma-launcher] WARNING: Classpath entry '" + classPathEntries[i] + "' does not exist.");
                }
            }
        }
        Class clazz = Class.forName(className, true, classLoader);
        
        
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method main = clazz.getDeclaredMethod(methodName,
                argTypes);

        main.invoke(null, args);
    }

    public static void main(String[] args) {
        if ((args == null) || (args.length == 0)) {
            throw new RuntimeException(
                    "No class specified that should contain \"main\" method.");
        }
        String mainMethodClassName = args[0];

        String[] applicationArgs = new String[args.length - 1];
        System.arraycopy(args, 1, applicationArgs, 0, args.length - 1);

        String[] classPathEntries = null;
        String karmaClassPath = System.getProperty(KARMA_CLASSPATH);
        if (karmaClassPath != null) {
            StringTokenizer tokenizer = new StringTokenizer(karmaClassPath, File.pathSeparator);

            int n = tokenizer.countTokens();
            classPathEntries = new String[n];

            int i = 0;
            while (i < n) {
                classPathEntries[i++] = tokenizer.nextToken();
            }
        }

        System.setSecurityManager(new KarmaLauncherSecurityManager());

        KarmaLauncher launcher = getInstance();
        try {
            launcher.invoke(mainMethodClassName, "main",
                    new Object[] { applicationArgs }, classPathEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}