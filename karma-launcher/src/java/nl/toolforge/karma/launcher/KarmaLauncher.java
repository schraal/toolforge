package nl.toolforge.karma.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The KarmaLauncher can launch an application. 
 *
 * @author Martin Oosterom
 */
public class KarmaLauncher
{
    // Singleton implementation
    //
    private static KarmaLauncher instance = null;

    private static synchronized KarmaLauncher getInstance() {
	if (instance == null) {
	    instance = new KarmaLauncher();
	}
	return instance;
    }

    private KarmaLauncher() {
	// Empty
    }

    // The run method invokes the "public static void main(String[] args)" method
    // By making use of the KarmaClassLoader and reflection.
    //
    private void run(String[] args) 
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	       InvocationTargetException, IOException, FileNotFoundException
    {
	if ((args == null) || (args.length == 0)) {
	    throw new IllegalArgumentException("No class specified that contains \"main\" method.");
	}

	System.setSecurityManager(new KarmaLauncherSecurityManager());
	
	String mainMethodClass = args[0];
	String[] applicationArgs = new String[args.length - 1];
	for (int i = 0; i < args.length - 1; i++) {
	    applicationArgs[i] = args[i + 1];
	}


	KarmaClassLoader classLoader = KarmaClassLoaderRepository.getInstance().getClassLoader("karma.root");
	Class clazz = Class.forName(mainMethodClass, true, classLoader);
	Method main = clazz.getDeclaredMethod("main", new Class[] { String[].class });
	main.invoke(null, new Object[] { applicationArgs });
    }

    /**
     * The main method takes at least one argument which is the class from which to invoke the main method.
     * Extra arguments are passed to the main method called.
     */
    public static void main(String[] args) {
	KarmaLauncher launcher = getInstance();
	
	try {

	    System.out.println("Arjen Smedes.");

	    launcher.run(args);
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
