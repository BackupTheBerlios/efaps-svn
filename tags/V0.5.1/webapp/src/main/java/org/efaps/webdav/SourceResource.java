/*
 * Copyright 2006 The eFaps Team
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

package org.efaps.webdav;

import java.util.Date;

import org.efaps.db.Instance;

/**
 *
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class SourceResource extends AbstractResource  {
  
  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * File length of the source resource.
   *
   * @see #getLength
   */
  private final long length;

  /////////////////////////////////////////////////////////////////////////////
  // constructor / desctructors
  
  public SourceResource(final String _name,
                        final Instance _instance,
                        final Date _created,
                        final Date _modified,
                        final String _description,
                        final long _length)  {
    super(_name, _instance, _created, _modified, _description);
    this.length = _length;
  }

  /////////////////////////////////////////////////////////////////////////////
  // getter / setter methods for instance variables

  /**
   * This is the getter method for instance variable {@table #length}.
   *
   * @return value of instance variable {@table #length}
   * @see #length
   */
  public long getLength()  {
    return this.length;
  }

}
