package nl.toolforge.karma.core.cmd.util;

import org.xml.sax.SAXException;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;

import nl.toolforge.karma.core.manifest.AbstractManifest;

/**
 * Convenience class capable of reading the most common deployment descriptor XML files as per their standards.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DescriptorReader {

  private static Log logger = LogFactory.getLog(AbstractManifest.class);

  /**
   * The <code>application.xml</code> file describing an J2EE enterprise application.
   */
  public static final String APPLICATION_XML = "application.xml";

  /**
   * The <code>web.xml</code> file describing an J2EE enterprise application.
   */
  public static final String WEB_XML = "web.xml";

  private String descriptor = null;

  /**
   *
   * @param descriptor A string containing the common file-name of the descriptor.
   */
  public DescriptorReader(String descriptor) {

    if (!descriptor.matches(APPLICATION_XML + "|" + WEB_XML)) {
      throw new IllegalArgumentException("Unsupported descriptor file.");
    }
    this.descriptor = descriptor;
  }


  public ApplicationXml getApplicationXmlDescriptor() {
    throw new UnsupportedOperationException("Method 'getApplicationXmlDescriptor' is not implemented yet.");
  }

  public void parse(File baseDir) throws IOException, SAXException {

    if (descriptor.equals(APPLICATION_XML)) {
      parseApplicationXml(baseDir);
    } else if (descriptor.equals(WEB_XML)) {
      parseWebXml(baseDir);
    }
  }

  private void parseWebXml(File baseDir) throws IOException, SAXException {
    throw new UnsupportedOperationException("Method 'parseWebXml' is not implemented yet.");
  }

  private void parseApplicationXml(File baseDir) throws IOException, SAXException {

    Digester digester = new Digester();
    digester.setValidating(false);   //todo: dit moet true worden
    digester.addObjectCreate("application", "java.util.ArrayList");
    digester.addObjectCreate("application/module/ejb", "java.lang.StringBuffer");
    digester.addCallMethod("application/module/ejb", "append", 0);
    digester.addSetNext("application/module/ejb", "add", "java.lang.StringBuffer");
    digester.addObjectCreate("application/module/java", "java.lang.StringBuffer");
    digester.addCallMethod("application/module/java", "append", 0);
    digester.addSetNext("application/module/java", "add", "java.lang.StringBuffer");
    digester.addObjectCreate("application/module/web/web-uri", "java.lang.StringBuffer");
    digester.addCallMethod("application/module/web/web-uri", "append", 0);
    digester.addSetNext("application/module/web/web-uri", "add", "java.lang.StringBuffer");

    logger.debug("Parsing 'application.xml' from " + baseDir.getPath());

    moduleNames = (List) digester.parse(new File(baseDir, "application.xml").getPath());
  }

  private List moduleNames = null;

  /**
   * Returns a List of StringBuffer objects, each buffer containing a 'module', 'java' or 'ejb' module name as they
   * appear in <code>application.xml</code> files.
   *
   * @return
   */
  public List getModuleNames() {

    //todo this method is NOT abstract. Should be phased out later on.

    if (moduleNames == null) {
      return new ArrayList();
    }
    return moduleNames;
  }

}
