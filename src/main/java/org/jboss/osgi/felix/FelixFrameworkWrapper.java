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

import org.jboss.osgi.spi.framework.FrameworkWrapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

/**
 * Wraps the Framework provided by the Felix implementation.
 * 
 * @author thomas.diesler@jboss.com
 * @since 15-Oct-2009
 */
class FelixFrameworkWrapper extends FrameworkWrapper
{
   FelixFrameworkWrapper(Framework framework)
   {
      super(framework);
   }

   @Override
   public BundleContext getBundleContext()
   {
      BundleContext context = super.getBundleContext();
      return new FelixBundleContextWrapper(context);
   }
}