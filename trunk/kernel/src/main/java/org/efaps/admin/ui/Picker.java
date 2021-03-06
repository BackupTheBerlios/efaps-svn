/*
 * Copyright 2003 - 2009 The eFaps Team
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

package org.efaps.admin.ui;

import static org.efaps.admin.EFapsClassNames.PICKER;

import java.util.UUID;

import org.efaps.admin.EFapsClassNames;
import org.efaps.util.cache.CacheReloadException;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class Picker extends AbstractCollection {

  /**
   * The static variable defines the class name in eFaps.
   */
  public static final EFapsClassNames EFAPS_CLASSNAME = PICKER;

  /**
   * Cache for the Pickers.
   */
  private static final PickerCache CACHE = new PickerCache();

  /**
   * @param _id     ID
   * @param _uuid   UUID
   * @param _name   Name
   */
  public Picker(final Long _id, final String _uuid, final String _name) {
    super(_id, _uuid, _name);
  }

   /**
   * Returns for given parameter <i>_id</i> the instance of class
   * {@link Picker}.
   *
   * @param _id id to search in the cache
   * @return instance of class {@link Picker}
   * @throws CacheReloadException
   * @see #getCache
   */
  public static Picker get(final long _id) {
    return CACHE.get(_id);
  }

  /**
   * Returns for given parameter <i>_name</i> the instance of class
   * {@link Picker}.
   *
   * @param _name name to search in the cache
   * @return instance of class {@link Picker}
   * @throws CacheReloadException
   * @see #getCache
   */
  public static Picker get(final String _name) {
    return CACHE.get(_name);
  }

  /**
   * Returns for given parameter <i>_uuid</i> the instance of class
   * {@link Picker}.
   *
   * @param _uuid UUID to search in the cache
   * @return instance of class {@link Picker}
   * @throws CacheReloadException
   * @see #getCache
   */
  public static Picker get(final UUID _uuid)  {
    return CACHE.get(_uuid);
  }

  /**
   * Static getter method for the type hashtable {@link #CACHE}.
   *
   * @return value of static variable {@link #CACHE}
   */
  protected static UserInterfaceObjectCache<Picker> getCache() {
    return CACHE;
  }

  /**
   * Cache for Picker.
   */
  private static class PickerCache extends UserInterfaceObjectCache<Picker> {

    /**
     *
     */
    protected PickerCache() {
      super(Picker.class);
    }
  }
}
