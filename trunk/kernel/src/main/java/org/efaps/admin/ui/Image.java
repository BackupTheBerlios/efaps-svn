/*
 * Copyright 2003-2007 The eFaps Team
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
 * Revision:        $Rev: 1293 $
 * Last Changed:    $Date: 2007-08-28 03:05:42 +0200 (Di, 28 Aug 2007) $
 * Last Changed By: $Author: jmo $
 */

package org.efaps.admin.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.efaps.admin.datamodel.Type;
import org.efaps.servlet.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tmo
 * @version $Id: Command.java 1293 2007-08-28 01:05:42Z jmo $
 * @todo description
 */
public class Image extends UserInterfaceObject  {

  /**
   * The static variable defines the class name in eFaps.
   */
  public final static EFapsClassName EFAPS_CLASSNAME = EFapsClassName.IMAGE;

  /**
   * Logging instance used in this class.
   */
  private final static Logger LOG = LoggerFactory.getLogger(Menu.class);

  /**
   * Stores the mapping from type to tree menu.
   */
  private final static Map<Type, Image> TYPE2IMAGE = new HashMap<Type, Image>();

  /**
   * Constructor to set the id and name of the command object.
   *
   * @param _id   id  of the command to set
   * @param _name name of the command to set
   */
  public Image(final Long _id, final String _uuid, final String _name)  {
    super(_id, _uuid, _name );
  }

  /**
   * Sets the link properties for this object.
   * 
   * @param _linkType type of the link property
   * @param _toId     to id
   * @param _toType   to type
   * @param _toName   to name
   */
  @Override
  protected void setLinkProperty(final EFapsClassName _linkType,
                                 final long _toId,
                                 final EFapsClassName _toType,
                                 final String _toName)  throws Exception {
    switch (_linkType) {
      case LINK_ICONISTYPEICONFOR:
        Type type = Type.get(_toId);
        if (type == null)  {
          LOG.error("Image '" + this.getName() + "' could not defined as type "
                    + "icon for type '" + _toName + "'! Type does not exists!");
        } else  {
          TYPE2IMAGE.put(type, this);
        }
        break;
      default:
        super.setLinkProperty(_linkType, _toId, _toType, _toName);
    }
  }

  /**
   * Returns the URL of this image.
   *
   * @return URL of this image
   */
  public String getUrl()  {
    return RequestHandler.replaceMacrosInUrl("${ROOTURL}/servlet/image/" + getName());
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Returns for given parameter <i>_id</i> the instance of class {@link Image}.
   *
   * @param _id id to search in the cache
   * @return instance of class {@link Image}
   * @see #getCache
   */
  static public Image get(final long _id)  {
    return getCache().get(_id);
  }

  /**
   * Returns for given parameter <i>_name</i> the instance of class
   * {@link Image}.
   *
   * @param _name name to search in the cache
   * @return instance of class {@link Image}
   * @see #getCache
   */
  static public Image get(final String _name)  {
    return getCache().get(_name);
  }

  /**
   * Returns for given parameter <i>UUID</i> the instance of class
   * {@link Image}.
   *
   * @param _uuid UUID to search in the cache
   * @return instance of class {@link Image}
   * @see #getCache
   */
  static public Image get(final UUID _uuid){
    return getCache().get(_uuid);
  }

  /**
   * Returns for given type the type tree menu. If no type tree menu is defined
   * for the type, it is searched if for parent type a menu is defined.
   *
   * @param _type   type for which the type tree menu is searched
   * @return type tree menu for given type if found; otherwise
   *         <code>null</code>.
   */
  public static Image getTypeIcon(final Type _type)  {
    Image ret = TYPE2IMAGE.get(_type);
    if ((ret == null) && (_type.getParentType() != null))  {
      ret = getTypeIcon(_type.getParentType());
    }
    return ret;
  }

  /**
   * Static getter method for the type hashtable {@link #cache}.
   *
   * @return value of static variable {@link #cache}
   */
  static UserInterfaceObjectCache<Image> getCache()  {
    return cache;
  }

  /**
   * Stores all instances of class {@link Image}.
   *
   * @see #getCache
   */
  static private UserInterfaceObjectCache<Image> cache
          = new UserInterfaceObjectCache<Image>(Image.class);
}