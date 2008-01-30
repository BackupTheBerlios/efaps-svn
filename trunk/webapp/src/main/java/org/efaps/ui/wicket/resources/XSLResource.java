/*
 * Copyright 2003-2008 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.ui.wicket.resources;

import org.apache.wicket.Application;
import org.apache.wicket.SharedResources;

import org.efaps.update.program.XSLUpdate;

/**
 * TODO description
 *
 * @author jmox
 * @version $Id$
 *
 */
public class XSLResource extends AbstractEFapsResource {

  private static final long serialVersionUID = 1L;

  public XSLResource(final String _name) {
    super(_name, XSLUpdate.TYPENAME);
  }

  public static XSLResource get(final String _name) {
    final SharedResources sharedResources =
        Application.get().getSharedResources();

    XSLResource resource = (XSLResource) sharedResources.get(_name);

    if (resource == null) {
      resource = new XSLResource(_name);
      sharedResources.add(_name, null, resource);
    }
    return resource;
  }

  @Override
  protected EFapsResourceStream setNewResourceStream() {
    return new XSLResourceStream();
  }

  /**
   * TODO description
   *
   * @author jmox
   * @version $Id$
   *
   */
  public class XSLResourceStream extends EFapsResourceStream {

    private static final long serialVersionUID = 1L;

    public XSLResourceStream() {
      super();
    }

    public String getContentType() {
      return null;
    }

  }
}
