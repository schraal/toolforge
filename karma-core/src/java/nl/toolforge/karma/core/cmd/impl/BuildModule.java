package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.File;

public class BuildModule extends DefaultCommand {

	public BuildModule(CommandDescriptor descriptor) {
		super(descriptor);
	}

	public CommandResponse execute() throws KarmaException {

		String moduleName = getCommandLine().getOptionValue("m");
		Module module = getContext().getCurrent().getModule(moduleName);

		// Configure underlying ant to run a command.
		//
		Project project = new Project();
		project.setName("arjen"); // Required
		project.setDefault("compile"); // Required

		project.init();

		// A java compile task
		//
		Javac task = (Javac) project.createTask("javac");

		// A path should be made for the module at hand
		//

		Path path = new Path(project);
		path.setPath("/tmp/MODULE-A/src/java/");

		task.setSrcdir(path);
		task.setDestdir(new File("/tmp"));

		Target target = new Target();
		target.setName("compile");
		target.addTask(task);

		project.addTarget(target);

		try {
			target.execute();
		} catch (BuildException e) {
			//e.printStackTrace();
		}

		//project.executeTarget("compile");

		return null;
	}

}
