/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.felix;

//$Id$

import java.net.URL;

import org.jboss.logging.Logger;
import org.jboss.osgi.spi.framework.BundleContextWrapper;
import org.jboss.osgi.spi.service.DeployerService;
import org.jboss.osgi.spi.util.BundleDeployment;
import org.jboss.osgi.spi.util.BundleDeploymentFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * The FelixBundleContextWrapper wrapps the BundleContext provided by the Felix implemenation.
 * 
 * It provides additional functionality on bundle install.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
public class FelixBundleContextWrapper extends BundleContextWrapper
{
   // Provide logging
   final Logger log = Logger.getLogger(FelixBundleContextWrapper.class);
   
   public FelixBundleContextWrapper(BundleContext context)
   {
      super(context);
   }

   @Override
   public Bundle installBundle(String location) throws BundleException
   {
      BundleDeployment dep = BundleDeploymentFactory.createBundleDeployment(location);
      URL bundleURL = dep.getLocation();
      String symbolicName = dep.getSymbolicName();
      Version version = Version.parseVersion(dep.getVersion());
      
      Bundle bundle;
      
      ServiceReference sref = context.getServiceReference(DeployerService.class.getName());
      if (sref != null)
      {
         DeployerService service = (DeployerService)context.getService(sref);
         service.deploy(bundleURL);
         bundle = getBundle(symbolicName, version, true);
      }
      else
      {
         bundle = context.installBundle(bundleURL.toExternalForm());
      }
      
      return bundle;
   }

   private Bundle getBundle(String symbolicName, Version version, boolean mustExist)
   {
      Bundle bundle = null;
      for (Bundle aux : getBundles())
      {
         if (aux.getSymbolicName().equals(symbolicName))
         {
            if (version == null || version.equals(aux.getVersion()))
            {
               bundle = aux;
               break;
            }
         }
      }
      
      if (bundle == null && mustExist == true)
         throw new IllegalStateException("Cannot obtain bundle: " + symbolicName + "-" + version);
      
      return bundle;
   }
}