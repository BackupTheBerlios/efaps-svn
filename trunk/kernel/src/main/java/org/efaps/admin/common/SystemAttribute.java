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

package org.efaps.admin.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.efaps.db.Context;
import org.efaps.db.transaction.ConnectionResource;
import org.efaps.util.EFapsException;
import org.efaps.util.cache.Cache;
import org.efaps.util.cache.CacheObjectInterface;
import org.efaps.util.cache.CacheReloadException;

/**
 * TODO description
 *
 * @author jmox
 * @version $Id$
 */
public class SystemAttribute implements CacheObjectInterface {

  /**
   * this static Variable contains the SQL-Statment used to retrieve the
   * SystemAttributes from the efps-Database.
   */
  private static final String SQL_SELECT =
      " select ID, UUID, NAME, VALUE from V_COMMONSYSATTRIBUTE";

  /**
   * Stores all instances of SytemAttribute.
   */
  private static SystemAttributeCache CACHE = new SystemAttributeCache();

  /**
   * The instance variable stores the id of this SystemAttribute.
   *
   * @see #getId()
   */
  private final long id;

  /**
   * The instance variable stores the UUID of this SystemAttribute.
   *
   * @see #getUUID()
   */
  private final UUID uuid;

  /**
   * The instance variable stores the Name of this SystemAttribute.
   *
   * @see #getName()
   */
  private final String name;

  /**
   * The instance variable stores the Value of this SystemAttribute.
   *
   * @see #getValue()
   * @see #getIntegerValue()
   * @see #getStringValue()
   */
  private final Object value;

  /**
   * Constructor setting instance variables.
   *
   * @param _id     id of the Systemattribute
   * @param _uuid   uuid of the Systemattribute
   * @param _name   name of the Systemattribute
   * @param _value  value of the Systemattribute
   */
  protected SystemAttribute(final long _id, final String _uuid,
                            final String _name, final Object _value) {
    this.id = _id;
    this.uuid = UUID.fromString(_uuid);
    this.name = _name;
    this.value = _value;
  }

  /**
   * Returns for given parameter <i>_id</i> the instance of class
   * {@link #SytemAttribute()}.
   * @param _id  id of the Systemattribute
   * @return instance of class {@link #SytemAttribute()}
   * @throws CacheReloadException
   */
  public static SystemAttribute get(final long _id) {
    return CACHE.get(_id);
  }

  /**
   * Returns for given parameter <i>_name</i> the instance of class
   * {@link #SytemAttribute()}.
   * @param _name  name of the Systemattribute
   * @return instance of class {@link #SytemAttribute()}
   * @throws CacheReloadException
   */
  public static SystemAttribute get(final String _name) {
    return CACHE.get(_name);
  }

  /**
   * Returns for given parameter <i>_uuid</i> the instance of class
   * {@link #SytemAttribute()}.
   *
   * @param _uuid  uuid of the Systemattribute
   * @return instance of class {@link #SytemAttribute()}
   * @throws CacheReloadException
   */
  public static SystemAttribute get(final UUID _uuid) {
    return CACHE.get(_uuid);
  }

  /**
   * This is the getter method for the instance variable {@link #id}.
   *
   * @return value of instance variable {@link #id}
   */
  public final long getId() {
    return this.id;
  }

  /**
   * This is the getter method for the instance variable {@link #uuid}.
   *
   * @return value of instance variable {@link #uuid}
   */
  public final UUID getUUID() {
    return this.uuid;
  }

  /**
   * This is the getter method for the instance variable {@link #name}.
   *
   * @return value of instance variable {@link #name}
   */
  public final String getName() {
    return this.name;
  }

  /**
   * This is the getter method for the instance variable {@link #value}.
   *
   * @return value of instance variable {@link #value}
   */
  public final Object getValue() {
    return this.value;
  }

  /**
   * Method that returns the Value of the instance variable {@link #value}
   * casted to String.
   *
   * @return value of instance variable {@link #value} as String
   */
  public final String getStringValue() {
    return this.value == null ? null : this.value.toString().trim();
  }

  /**
   * Method that returns the Value of the instance variable {@link #value}
   * casted to Integer.
   *
   * @return value of instance variable {@link #value} as Integer
   */
  public final int getIntegerValue() {
    return Integer.parseInt(this.value.toString());
  }

  /**
   * Method that returns the Value of the instance variable {@link #value}
   * casted to Boolean.
   *
   * @return value of instance variable {@link #value} as boolean, true if the
   *         Object equals "true", else false
   */
  public final boolean getBooleanValue() {
    return Boolean.parseBoolean(this.value.toString());
  }

  /**
   * Method to initialize the Cache of this CacheObjectInterface.
   */
  public static void initialize() {
    CACHE.initialize(SystemAttribute.class);
  }

  private static class SystemAttributeCache extends Cache<SystemAttribute> {

    @Override
    protected void readCache(final Map<Long, SystemAttribute> _newCache4Id,
                             final Map<String, SystemAttribute> _newCache4Name,
                             final Map<UUID, SystemAttribute> _newCache4UUID)
        throws CacheReloadException {
      try {

        final ConnectionResource con =
            Context.getThreadContext().getConnectionResource();
        final Statement stmt = con.getConnection().createStatement();
        final ResultSet resultset = stmt.executeQuery(SQL_SELECT);

        while (resultset.next()) {
          final long id = resultset.getLong(1);
          final String uuid = resultset.getString(2).trim();
          final String name = resultset.getString(3).trim();
          final Object value = resultset.getObject(4);
          final SystemAttribute sysatt
                        = new SystemAttribute(id, uuid, name, value);
          _newCache4Id.put(sysatt.getId(), sysatt);
          _newCache4Name.put(sysatt.getName(), sysatt);
          _newCache4UUID.put(sysatt.getUUID(), sysatt);
        }
      } catch (final EFapsException e) {
        throw new CacheReloadException("could not read SystemAttribute", e);
      } catch (final SQLException e) {
        throw new CacheReloadException("could not read SystemAttribute", e);
      }

    }
  }
}
