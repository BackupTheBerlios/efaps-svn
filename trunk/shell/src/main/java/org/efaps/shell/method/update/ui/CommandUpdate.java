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

package org.efaps.shell.method.update.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.shell.method.update.AbstractUpdate;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class CommandUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(CommandUpdate.class);

  /** Link from command to table as target */
  private final static Link LINK2TARGETTABLE
                    = new Link("Admin_UI_LinkTargetTable", 
                               "From", 
                               "Admin_UI_Table", "To");

  /** Link from command to menu as target */
  private final static Link LINK2TARGETMENU
                    = new Link("Admin_UI_LinkTargetMenu", 
                               "From", 
                               "Admin_UI_Menu", "To");

  private final static Set <Link> ALLLINKS = new HashSet < Link > ();  {
    ALLLINKS.add(LINK2TARGETTABLE);
    ALLLINKS.add(LINK2TARGETMENU);
/*    ALLLINKS.add(LINK2PERSON);
    ALLLINKS.add(LINK2ROLE);
    ALLLINKS.add(LINK2GROUP);
*/
  }

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public CommandUpdate() {
    super("Admin_UI_Command", ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static CommandUpdate readXMLFile(final String _fileName) throws IOException  {
//    } catch (IOException e)  {
//      LOG.error("could not open file '" + _fileName + "'", e);
    return readXMLFile(new File(_fileName));
  }

  public static CommandUpdate readXMLFile(final File _file) throws IOException  {
    CommandUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("ui-command", CommandUpdate.class);

      digester.addCallMethod("ui-command/uuid", "setUUID", 1);
      digester.addCallParam("ui-command/uuid", 0);

      digester.addObjectCreate("ui-command/definition", Definition.class);
      digester.addSetNext("ui-command/definition", "addDefinition");

      digester.addCallMethod("ui-command/definition/version", "setVersion", 4);
      digester.addCallParam("ui-command/definition/version/application", 0);
      digester.addCallParam("ui-command/definition/version/global", 1);
      digester.addCallParam("ui-command/definition/version/local", 2);
      digester.addCallParam("ui-command/definition/version/mode", 3);
      
      digester.addCallMethod("ui-command/definition/name", "setName", 1);
      digester.addCallParam("ui-command/definition/name", 0);

      digester.addCallMethod("ui-command/definition/target/table", "assignTargetTable", 1);
      digester.addCallParam("ui-command/definition/target/table", 0);

      digester.addCallMethod("ui-command/definition/target/menu", "assignTargetMenu", 1);
      digester.addCallParam("ui-command/definition/target/menu", 0);

      digester.addCallMethod("ui-command/definition/property", "addProperty", 2);
      digester.addCallParam("ui-command/definition/property/name", 0);
      digester.addCallParam("ui-command/definition/property/value", 1);

      ret = (CommandUpdate) digester.parse(_file);
    } catch (SAXException e)  {
e.printStackTrace();
      //      LOG.error("could not read file '" + _fileName + "'", e);
    }
    return ret;
  }

  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class Definition extends DefinitionAbstract {
    
    ///////////////////////////////////////////////////////////////////////////
    // instance methods

    /**
     * Assigns a table as target for this command definition.
     *
     * @param _targetTable  name of the target table
     */
     public void assignTargetTable(final String _targetTable)  {
       addLink(LINK2TARGETTABLE, _targetTable);
     }

    /**
     * Assigns a menu as target for this command definition.
     *
     * @param _targetMenu  name of the target menu
     */
     public void assignTargetMenu(final String _targetMenu)  {
       addLink(LINK2TARGETMENU, _targetMenu);
     }
  }
}