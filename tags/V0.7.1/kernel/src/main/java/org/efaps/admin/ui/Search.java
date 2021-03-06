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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.admin.ui;

import org.efaps.util.EFapsException;

/**
 *
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class Search extends MenuAbstract  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * The static variable defines the class name in eFaps.
   */
  public static EFapsClassName EFAPS_CLASSNAME = EFapsClassName.SEARCH;

  
  /**
   * Stores all instances of class {@link Search}.
   *
   * @see #getCache
   */
  private static final UserInterfaceObjectCache<Search> searchCache
          = new UserInterfaceObjectCache<Search>(Search.class);

  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * Stores the default search command used when the search is called.
   */
  private CommandAbstract defaultCommand = null;

  /////////////////////////////////////////////////////////////////////////////
  // constructors / destructors

  /**
   * This is the constructor to create a new instance of the class Search. The
   * parameter <i>_name</i> is a must value to identify clearly the search
   * instance.
   *
   * @param _context  context for this request
   * @param _id       search id
   * @param _name     search name
   */
  public Search(final Long _id, final String _name) throws Exception  {
    super(_id, _name);
//    readFromDB(_context);
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  /**
   * An sub command or menu with the given id is added to this menu.
   *
   * @param _context  eFaps context for this request
   * @param _sortId   id used to sort
   * @param _id       command / menu id
   */
  protected void add(long _sortId, long _id)  {
    Command command = Command.get(_id);
    if (command != null)  {
      add(_sortId, command);
    } else  {
      Menu subMenu = Menu.get(_id);
      add(_sortId, subMenu);
    }
  }

  /**
   * @param _context  eFaps context for this request
   * @param _linkType type of the link property
   * @param _toId     to id
   * @param _toType   to type
   * @param _toName   to name
   */
  protected void setLinkProperty(final EFapsClassName _linkType,
                                 final long _toId,
                                 final EFapsClassName _toType,
                                 final String _toName)  throws Exception {
    switch (_linkType) {
      case LINK_DEFAULT_SEARCHCOMMAND:
        this.defaultCommand = Command.get(_toId);
        if (this.defaultCommand == null)  {
          this.defaultCommand = Menu.get(_toId);
        }
      break;
      default:
        super.setLinkProperty(_linkType, _toId, _toType, _toName);
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // getter / setter methods


  /**
   * This is the getter method for the instance variable
   * {@link #defaultCommand}.
   * 
   * @return value of instance variable {@link #defaultCommand}
   * @see #defaultCommand
   */
  public CommandAbstract getDefaultCommand() {
    return this.defaultCommand;
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  /**
   * Returns for given parameter <i>_name</i> the instance of class
   * {@link Command}.
   *
   * @param _context  context this request
   * @param _name     name to search in the cache
   * @return instance of class {@link Command}
   * @see #getCache
   */
  static public Search get(String _name) throws EFapsException  {
    Search search = (Search) getCache().get(_name);
    if (search==null)  {
      search = getCache().read(_name);
    }
    return search;
  }

  /**
   * Static getter method for the type hashtable {@link #cache}.
   *
   * @return value of static variable {@link #cache}
   */
  protected static UserInterfaceObjectCache<Search> getCache()  {
    return searchCache;
  }
}