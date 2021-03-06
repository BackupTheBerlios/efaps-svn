/*
 * Copyright 2005 The eFaps Team
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
 */

package org.efaps.admin.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.AttributeTypeInterface;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.user.Role;
import org.efaps.db.Cache;
import org.efaps.db.CacheInterface;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;

/**
 *
 */
abstract public class MenuAbstract extends CommandAbstract  {

  /**
   * Constructor to set the id and name of the menu object.
   *
   * @param _id   id  of the command to set
   * @param _name name of the command to set
   */
  protected MenuAbstract(long _id, String _name)  {
    super(_id, _name);
  }

  /**
   * Adds a command or menu to this menu instance. The method must be
   * specific  implemented by all menu implementations.
   *
   * @param _context  eFaps context for this request
   * @param _id       id of the sub command / menu to add
   */
  abstract protected void add(Context _context, long _id) throws Exception;

  /**
   * Add a command to the menu structure.
   *
   * @param _command command to add
   */
  public void add(CommandAbstract _command)  {
    getCommands().add(_command);
  }

  /**
   * Add all sub commands and menus of the given menu to this menu structure.
   *
   * @param _menu   menu with sub structure
   */
  public void addAll(MenuAbstract _menu)  {
    getCommands().addAll(_menu.getCommands());
  }


  /**
   * Check, if the user of the context has access to this user interface
   * object. First, the instance method checks, if some acces configuration
   * exists for this menu instance object. If the user has access for this
   * menu, it is test, if the context user has access to minimum one sub command
   * command / menu. If yes, the user is allowed to access this menu instance,
   * other the user is not allowed to access this menu.
   *
   * @param _context  context for this request (including the person)
   * @return  <i>true</i>if context user has access, otherwise <i>false</i> is
   *          returned
   */
  public boolean hasAccess(Context _context)  {
    boolean ret = super.hasAccess(_context);

    if (ret && getCommands().size()>0)  {
      ret = false;
      Iterator iter = getCommands().iterator();
      while (iter.hasNext())  {
        UserInterfaceObject obj = (UserInterfaceObject)iter.next();
        if (obj.hasAccess(_context))  {
          ret = true;
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Returns all information from the menu as string.
   */
  public String toString()  {
    ToStringBuilder buf = new ToStringBuilder(this).
        appendSuper(super.toString());

    for (CommandAbstract cmd : getCommands())  {
      buf.append(" ").append(cmd);
    }
    return buf.toString();
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * The instance method reads all needed information for this user interface
   * object. The method extends the original method, because the sub menus and
   * commands must be read.
   *
   * @param _context  eFaps context for this request
   * @see #readFromDB4Childs
   */
  protected void readFromDB(Context _context) throws Exception  {
    super.readFromDB(_context);
    readFromDB4Childs(_context);
  }

  /**
   * The instance method gets all sub menus and commands and adds them to
   * this menu instance via method {@link #add(long)}.
   *
   * @param _context  eFaps context for this request
   * @see #readFromDB
   * @see #add(long)
   */
  private void readFromDB4Childs(Context _context) throws Exception  {
    Instance menuInst = new Instance(_context, Type.get(EFapsClassName.MENU.name), getId());
    SearchQuery query = new SearchQuery();
    query.setExpand(_context, menuInst, "Admin_UI_Menu2Command\\FromMenu.ToCommand");
    query.addSelect(_context, "ID");
    query.execute(_context);

    while (query.next())  {
      long id = (Long)query.get(_context, "ID");
      add(_context, id);
    }
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Instance Variable to hold all the commands in a vector instance.
   *
   * @see #getCommands
   */
  private List<CommandAbstract> commands = new ArrayList<CommandAbstract>();

  /////////////////////////////////////////////////////////////////////////////

  /**
   * This is the getter method for instance variable {@link #commands}.
   *
   * @see #commands
   */
  public List<CommandAbstract> getCommands()  {
    return this.commands;
  }
}
