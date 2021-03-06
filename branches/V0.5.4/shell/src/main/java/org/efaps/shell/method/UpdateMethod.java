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

package org.efaps.shell.method;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.JexlContext;

import org.efaps.shell.method.update.AccessSetUpdate;
import org.efaps.shell.method.update.AccessTypeUpdate;
import org.efaps.shell.method.update.datamodel.SQLTableUpdate;
import org.efaps.shell.method.update.datamodel.TypeUpdate;
import org.efaps.shell.method.update.integration.WebDAVUpdate;
import org.efaps.shell.method.update.user.JAASSystemUpdate;

import org.efaps.util.EFapsException;

/**
 *
 * @author tmo
 * @version $Id$
 */
public final class UpdateMethod extends AbstractMethod  {
  
  /////////////////////////////////////////////////////////////////////////////
  // static variables

  private final static Option PROPERTY_VERSION  = OptionBuilder
        .withArgName("number")
        .hasArg()
        .withDescription("Defines global import version")
        .create("version");

  /////////////////////////////////////////////////////////////////////////////
  // constructors / desctructors
  
  /**
   *
   */
  public UpdateMethod()  {
    super("update", "updates eFaps instance",
          PROPERTY_VERSION);
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  /**
   *
   * @todo remove Exception
   */
  public void doMethod() throws EFapsException,Exception {
    login("administrator", "");
    reloadCache();
    startTransaction();
 
    JexlContext jexlContext = JexlHelper.createContext();
    String version = getCommandLine().getOptionValue("version");
    if (version != null)  {
      jexlContext.getVars().put("version", 
                                Integer.parseInt(version));
    }
    
    for (String fileName : getCommandLine().getArgs())  {
      SQLTableUpdate update = SQLTableUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }
    for (String fileName : getCommandLine().getArgs())  {
      TypeUpdate update = TypeUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }
    for (String fileName : getCommandLine().getArgs())  {
      JAASSystemUpdate update = JAASSystemUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }
    for (String fileName : getCommandLine().getArgs())  {
      AccessTypeUpdate update = AccessTypeUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }
    for (String fileName : getCommandLine().getArgs())  {
      AccessSetUpdate update = AccessSetUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }
    for (String fileName : getCommandLine().getArgs())  {
      WebDAVUpdate update = WebDAVUpdate.readXMLFile(fileName);
      if (update != null)  {
        update.updateInDB(jexlContext);
      }
    }

    commitTransaction();
  }
}
