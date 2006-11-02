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

package org.efaps.webdav.resource;

import java.util.Date;
import java.util.List;

import org.efaps.db.Instance;
import org.efaps.webdav.WebDAVInterface;

/**
 *
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class CollectionResource extends AbstractResource  {

  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * WebDAV implementation to get information for sub collections / sources.
   */
  private final WebDAVInterface subWebDAVImpl;
  
  /////////////////////////////////////////////////////////////////////////////
  // constructor / desctructors
  
  public CollectionResource(final WebDAVInterface _webDAVImpl,
                            final WebDAVInterface _subWebDAVImpl,
                            final String _name,
                            final Instance _instance,
                            final Date _created,
                            final Date _modified,
                            final String _description)  {
    super(_webDAVImpl, _name, _instance, _created, _modified, _description);
    this.subWebDAVImpl = _subWebDAVImpl;
  }
  
  /**
   * Deletes this collection resource.
   *
   * @return <i>true</i> if deleted, otherwise <i>false</i>
   */
  public boolean delete()  {
    return getWebDAVImpl().deleteCollection(this);
  }

  /**
   * Moves this collection resource to a new location.
   *
   * @param _collection   new parent collection to move
   * @param _newName      new name of the collection in the parent collection
   * @return <i>true</i> if moved, otherwise <i>false</i>
   */
  public boolean move(final CollectionResource _collection,
                      final String _newName)  {
    return getWebDAVImpl().moveCollection(this, _collection, _newName);
  }

  /**
   * @return sub collections and sources for this collection
   */
  public List < AbstractResource > getSubs()  {
    List < AbstractResource > list = this.subWebDAVImpl.getSubs(this);
    for (AbstractResource resource : list)  {
      resource.setParent(this);
    }
    return list;
  }

  public CollectionResource getCollection(final String _name)  {
    CollectionResource ret = this.subWebDAVImpl.getCollection(this, _name);
    if (ret != null)  {
      ret.setParent(this);
    }
    return ret;
  }

  /**
   * @return <i>true</i> if created, otherwise <i>false</i>
   */
  public boolean createCollection(final String _name)  {
    return this.subWebDAVImpl.createCollection(this, _name);
  }


  public SourceResource getSource(final String _name)  {
    SourceResource ret = this.subWebDAVImpl.getSource(this, _name);
    if (ret != null)  {
      ret.setParent(this);
    }
    return ret;
  }

  public boolean createSource(final String _name)  {
    return this.subWebDAVImpl.createSource(this, _name);
  }

}
