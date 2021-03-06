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

package org.efaps.shell.method.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.db.Context;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.db.Update;
import org.efaps.util.EFapsException;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class AccessTypeUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(AccessTypeUpdate.class);

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public AccessTypeUpdate() {
    super("Admin_Access_AccessType");
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static AccessTypeUpdate readXMLFile(final String _fileName) throws IOException  {
//    } catch (IOException e)  {
//      LOG.error("could not open file '" + _fileName + "'", e);
    return readXMLFile(new File(_fileName));
  }

  public static AccessTypeUpdate readXMLFile(final File _file) throws IOException  {
    AccessTypeUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("access-type", AccessTypeUpdate.class);

      digester.addCallMethod("access-type/uuid", "setUUID", 1);
      digester.addCallParam("access-type/uuid", 0);

      digester.addObjectCreate("access-type/definition", Definition.class);
      digester.addSetNext("access-type/definition", "addDefinition");

      digester.addCallMethod("access-type/definition/version", "setVersion", 4);
      digester.addCallParam("access-type/definition/version/application", 0);
      digester.addCallParam("access-type/definition/version/global", 1);
      digester.addCallParam("access-type/definition/version/local", 2);
      digester.addCallParam("access-type/definition/version/mode", 3);
      
      digester.addCallMethod("access-type/definition/name", "setName", 1);
      digester.addCallParam("access-type/definition/name", 0);

      ret = (AccessTypeUpdate) digester.parse(_file);
    } catch (SAXException e)  {
e.printStackTrace();
      //      LOG.error("could not read file '" + _fileName + "'", e);
    }
    return ret;
  }

  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class Definition extends DefinitionAbstract {
  }
}
