/*
* Copyright (c) 2004 Your Corporation. All Rights Reserved.
*/
package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;
import nl.toolforge.karma.core.vc.svnimpl.SubversionRepository;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class LocationFactory {

  private static Log logger = LogFactory.getLog(LocationFactory.class);
  private static LocationFactory instance = null;

  /**
   * Gets the singleton instance of this factory.
   */
  public static LocationFactory getInstance() {

    if (instance == null) {
      instance = new LocationFactory();
    }
    return instance;
  }

  private LocationFactory() { }

  public Location createLocation(LocationDescriptor descriptor) throws LocationException {

    LocationType type = null;
    try {
      type = LocationType.getTypeInstance(descriptor.getType());
    } catch (IllegalArgumentException e) {
      throw new LocationException(LocationException.INVALID_LOCATION_TYPE, new Object[]{descriptor.getType(), descriptor.getId()});
    }

    try {
      if (type.equals(LocationType.CVS)) {

        CVSRepository location = new CVSRepository(descriptor.getId());

        try {
          location.setProtocol(descriptor.getProtocol());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"protocol"});
        }
        try {
          location.setHost(descriptor.getHost());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"host"});
        }
        try {
          location.setPort(descriptor.getPort());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"port"});
        }
        try {
          location.setRepository(descriptor.getRepository());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"repository"});
        }
        try {
          location.setOffset(descriptor.getModuleOffset());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"module-offset"});
        }
        return location;

      } else if (type.equals(LocationType.SUBVERSION)) {

        SubversionRepository location = new SubversionRepository(descriptor.getId());

        try {
          location.setProtocol(descriptor.getProtocol());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"protocol"});
        }
        try {
          location.setHost(descriptor.getHost());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"host"});
        }
        try {
          location.setPort(descriptor.getPort());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"port"});
        }
        try {
          location.setRepository(descriptor.getRepository());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"repository"});
        }
        try {
          location.setOffset(descriptor.getModuleOffset());
        } catch (RuntimeException r) {
          throw new LocationException(LocationException.INVALID_ELEMENT_VALUE, new Object[]{"module-offset"});
        }
        return location;
      }
    } catch (RuntimeException r) {
      logger.error(r);
      throw new LocationException(LocationException.INVALID_ELEMENT_VALUE);
    }
    throw new KarmaRuntimeException("Not implemented for other types than '"+LocationType.CVS+"' and '"+LocationType.SUBVERSION+"' ");
  }
}
