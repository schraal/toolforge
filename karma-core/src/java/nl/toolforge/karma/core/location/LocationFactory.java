/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;
import nl.toolforge.karma.core.vc.svnimpl.SubversionRepository;
import nl.toolforge.karma.core.KarmaRuntimeException;

public class LocationFactory {

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

    if (LocationType.getTypeInstance(descriptor.getType()).equals(LocationType.CVS)) {

      CVSRepository cvsLocation = new CVSRepository(descriptor.getId());

      cvsLocation.setProtocol(descriptor.getProtocol());
      cvsLocation.setHost(descriptor.getHost());
      cvsLocation.setPort(new Integer(descriptor.getPort()).intValue());
      cvsLocation.setRepository(descriptor.getRepository());
      cvsLocation.setOffset(descriptor.getModuleOffset());

      return cvsLocation;

    } else if (LocationType.getTypeInstance(descriptor.getType()).equals(LocationType.SUBVERSION)) {

      SubversionRepository cvsLocation = new SubversionRepository(descriptor.getId());

      cvsLocation.setProtocol(descriptor.getProtocol());
      cvsLocation.setHost(descriptor.getHost());
      cvsLocation.setPort(new Integer(descriptor.getPort()).intValue());
      cvsLocation.setRepository(descriptor.getRepository());
      cvsLocation.setOffset(descriptor.getModuleOffset());

      return cvsLocation;
    }
    throw new KarmaRuntimeException("Not implemented for other types than '"+LocationType.CVS+"' and '"+LocationType.SUBVERSION+"' ");
  }
}
