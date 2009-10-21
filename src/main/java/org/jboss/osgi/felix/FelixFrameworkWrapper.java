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

import org.jboss.logging.Logger;
import org.jboss.osgi.deployment.internal.DeploymentServicesActivator;
import org.jboss.osgi.spi.framework.FrameworkWrapper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

/**
 * The FelixFrameworkWrapper wrapps the Framework provided by the Felix implemenation.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
class FelixFrameworkWrapper extends FrameworkWrapper
{
   // Provide logging
   final Logger log = Logger.getLogger(FelixFrameworkWrapper.class);
   
   private BundleActivator deploymentServices;
   
   FelixFrameworkWrapper(Framework framework)
   {
      super(framework);
   }

   @Override
   public void start() throws BundleException
   {
      super.start();
      
      // Start the deployment services
      try
      {
         BundleContext context = framework.getBundleContext();
         deploymentServices = new DeploymentServicesActivator();
         deploymentServices.start(context);
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (BundleException ex)
      {
         throw ex;
      }
      catch (Exception ex)
      {
         throw new BundleException("Cannot start deployment services", ex); 
      }
   }

   @Override
   public void stop() throws BundleException
   {
      // Stop the deployment services
      if (deploymentServices != null)
      {
         try
         {
            BundleContext context = framework.getBundleContext();
            deploymentServices.stop(context);
         }
         catch (RuntimeException rte)
         {
            throw rte;
         }
         catch (BundleException ex)
         {
            throw ex;
         }
         catch (Exception ex)
         {
            throw new BundleException("Cannot start deployment services", ex); 
         }
      }
      super.stop();
   }

   @Override
   public BundleContext getBundleContext()
   {
      BundleContext context = framework.getBundleContext();
      return new FelixBundleContextWrapper(context);
   }
}