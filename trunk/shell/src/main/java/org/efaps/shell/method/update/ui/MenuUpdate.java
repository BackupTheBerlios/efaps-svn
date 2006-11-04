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
public class MenuUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(MenuUpdate.class);

  /** Link from menu to table as target */
  private final static Link LINK2TARGETTABLES
                    = new Link("Admin_UI_LinkTargetTable", 
                               "From", 
                               "Admin_UI_Table", "To");

  private final static Set <Link> ALLLINKS = new HashSet < Link > ();  {
    ALLLINKS.add(LINK2TARGETTABLES);
/*    ALLLINKS.add(LINK2DATAMODELTYPE);
    ALLLINKS.add(LINK2PERSON);
    ALLLINKS.add(LINK2ROLE);
    ALLLINKS.add(LINK2GROUP);
*/
  }

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public MenuUpdate() {
    super("Admin_UI_Menu", ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static MenuUpdate readXMLFile(final String _fileName) throws IOException  {
//    } catch (IOException e)  {
//      LOG.error("could not open file '" + _fileName + "'", e);
    return readXMLFile(new File(_fileName));
  }

  public static MenuUpdate readXMLFile(final File _file) throws IOException  {
    MenuUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("ui-menu", MenuUpdate.class);

      digester.addCallMethod("ui-menu/uuid", "setUUID", 1);
      digester.addCallParam("ui-menu/uuid", 0);

      digester.addObjectCreate("ui-menu/definition", Definition.class);
      digester.addSetNext("ui-menu/definition", "addDefinition");

      digester.addCallMethod("ui-menu/definition/version", "setVersion", 4);
      digester.addCallParam("ui-menu/definition/version/application", 0);
      digester.addCallParam("ui-menu/definition/version/global", 1);
      digester.addCallParam("ui-menu/definition/version/local", 2);
      digester.addCallParam("ui-menu/definition/version/mode", 3);
      
      digester.addCallMethod("ui-menu/definition/name", "setName", 1);
      digester.addCallParam("ui-menu/definition/name", 0);

      digester.addCallMethod("ui-menu/definition/target/table", "assignTargetTable", 1);
      digester.addCallParam("ui-menu/definition/target/table", 0);

      digester.addCallMethod("ui-menu/definition/property", "addProperty", 2);
      digester.addCallParam("ui-menu/definition/property/name", 0);
      digester.addCallParam("ui-menu/definition/property/value", 1);

      ret = (MenuUpdate) digester.parse(_file);
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
     * Assigns a table as target for this menu definition.
     *
     * @param _targetTable  name of the target table
     */
     public void assignTargetTable(final String _targetTable)  {
       addLink(LINK2TARGETTABLES, _targetTable);
     }
  }
}
