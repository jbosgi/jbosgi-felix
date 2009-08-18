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
import org.jboss.osgi.spi.framework.FrameworkIntegration;
import org.jboss.osgi.spi.util.ServiceLoader;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Felix specific OSGi Framework integration.
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Jan-2009
 */
public class FelixIntegration extends FrameworkIntegration
{
   // Provide logging
   final Logger log = Logger.getLogger(FelixIntegration.class);
   
   public void create()
   {
      String implVersion = getClass().getPackage().getImplementationVersion();
      log.info("OSGi Integration Felix - " + implVersion);

      // When a Felix instance is embedded in a host application,
      // the host application must inform the Felix instance that it is embedded
      properties.put("felix.embedded.execution", "true");

      // An instance of Logger that the framework uses as its default logger
      properties.put("felix.log.logger", new FelixLogger());

      // Load the framework instance
      FrameworkFactory factory = ServiceLoader.loadService(FrameworkFactory.class);
      framework = factory.newFramework(properties);
   }

   public void stop()
   {
      if (framework != null)
      {
         // Running the Felix shutdown in a separate thread that gets 
         // interrupted after a 10sec timeout. This is a workaround for
         //
         // [FELIX-1311] Felix shutdown may lead to dead lock
         // https://issues.apache.org/jira/browse/FELIX-1311
         Runnable runnable = new Runnable()
         {
            public void run()
            {
               try
               {
                  framework.stop();
                  framework.waitForStop(5000);
                  framework = null;
                  log.debug("SystemBundle STOPPED");
               }
               catch (BundleException ex)
               {
                  log.error("Cannot stop Felix", ex);
               }
               catch (InterruptedException ex)
               {
                  log.error("Cannot stop Felix", ex);
               }
            }
         };
         
         Thread thread = new Thread(runnable);
         thread.start();

         int sleep = 500;
         int timeout = 10000;
         while (framework != null && timeout > 0)
         {
            try
            {
               Thread.sleep(sleep);
               timeout -= sleep;
            }
            catch (InterruptedException ex)
            {
               // ignore
            }
         }
         
         if (timeout == 0 && thread.isAlive())
            thread.interrupt();
      }
   }
}