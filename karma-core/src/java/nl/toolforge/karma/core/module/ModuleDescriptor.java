package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.manifest.LibModule;
import nl.toolforge.karma.core.manifest.SourceModule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleDescriptor {

  protected static Log logger = LogFactory.getLog(ModuleDescriptor.class);

  private Module.Type moduleType = null;

  public ModuleDescriptor(Module module) {

    if (module == null) {
      throw new IllegalArgumentException("Module type cannot be null.");
    }

    // todo should be implemented by a method overridden by specific module types
    //
    if (module instanceof LibModule) {
      this.moduleType = Module.LIBRARY_MODULE;
    } else if (module instanceof SourceModule) {
      this.moduleType = Module.JAVA_SOURCE_MODULE;
    } else if (module instanceof JavaEnterpriseApplicationModule) {
      this.moduleType = Module.JAVA_ENTERPRISE_APPLICATION;
    } else if (module instanceof JavaWebApplicationModule) {
      this.moduleType = Module.JAVA_WEB_APPLICATION;
    }
  }

  /**
   * Writes the <code>module-descriptor.xml</code> file to <code>dir</code>.
   *
   * @param dir An existing directory where the <code>module-descriptor.xml</code> file should be created.
   */
  public void createFile(File dir) throws IOException {

    StringBuffer buffer = new StringBuffer();

    buffer.append("<?xml version=\"1.0\"?>\n");

    buffer.append("<module-descriptor version=\"1-0\">\n");
    buffer.append("  <type>").append(moduleType.getType()).append("</type>\n");
    buffer.append("  <layout-specification/> <!-- for future usage -->\n");
    buffer.append("</module-descriptor>\n");

    Writer writer = new BufferedWriter(new FileWriter(new File(dir, "module-descriptor.xml")));
    writer.write(buffer.toString());

    logger.debug("Creating `module-descriptor.xml` for type : " + moduleType.getType());

    writer.flush();
  }

}
