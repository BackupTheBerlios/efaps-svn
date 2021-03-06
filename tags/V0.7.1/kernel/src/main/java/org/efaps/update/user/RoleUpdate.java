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

package org.efaps.update.user;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.update.AbstractUpdate;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class RoleUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(RoleUpdate.class);

  /** Link from menu to child command / menu */
/*  private final static Link LINK2CHILD
             = new OrderedLink("Admin_UI_Menu2Command", 
                               "FromMenu", 
                               "Admin_UI_Command", "ToCommand");
*/
  private final static Set <Link> ALLLINKS = new HashSet < Link > ();  {
//    ALLLINKS.add(LINK2CHILD);
//    ALLLINKS.addAll(CommandUpdate.ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public RoleUpdate() {
    super("Admin_User_Role", ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static RoleUpdate readXMLFile(final URL _url)  {
    RoleUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("user-role", RoleUpdate.class);

      digester.addCallMethod("user-role/uuid", "setUUID", 1);
      digester.addCallParam("user-role/uuid", 0);

      digester.addObjectCreate("user-role/definition", RoleDefinition.class);
      digester.addSetNext("user-role/definition", "addDefinition");

      digester.addCallMethod("user-role/definition/version", "setVersion", 4);
      digester.addCallParam("user-role/definition/version/application", 0);
      digester.addCallParam("user-role/definition/version/global", 1);
      digester.addCallParam("user-role/definition/version/local", 2);
      digester.addCallParam("user-role/definition/version/mode", 3);
      
      digester.addCallMethod("user-role/definition/name", "setName", 1);
      digester.addCallParam("user-role/definition/name", 0);

      digester.addCallMethod("user-role/definition/status", "setStatus", 1);
      digester.addCallParam("user-role/definition/status", 0);
      ret = (RoleUpdate) digester.parse(_url);

      if (ret != null)  {
        ret.setURL(_url);
      }
    } catch (IOException e) {
      LOG.error(_url.toString() + " is not readable", e);
    } catch (SAXException e) {
      LOG.error(_url.toString() + " seems to be invalide XML", e);
    }
    return ret;
  }

  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class RoleDefinition extends DefinitionAbstract  {
    
    public void setStatus(final String _status){
      addValue("Status", _status);
    }
 }
}
