/*
 * Copyright 2003 - 2007 The eFaps Team
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
 * Revision:        $Rev: 732 $
 * Last Changed:    $Date: 2007-03-18 22:17:23 +0000 (Sun, 18 Mar 2007) $
 * Last Changed By: $Author: jmo $
 */

package org.efaps.importer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * Main Class for Importing Objects into the Database connected to eFaps
 * 
 * @author jmo
 * 
 */
public abstract class AbstractObject {

  public abstract Set<ForeignObject> getLinks();

  public abstract String getType();

  public abstract Map<String, Object> getAttributes();

  public abstract void setID(String _ID);

  public abstract void insertObject();

  public abstract String getParrentAttribute();

  public abstract boolean isCheckinObject();

  public abstract void checkObjectin();

  public static void importFromXML(final String _xml) {
    Digester digester = new Digester();

    digester.setValidating(false);

    digester.addObjectCreate("import", RootObject.class);
    digester.addCallMethod("import", "setDateFormat", 1);
    digester.addCallParam("import", 0, "dateformat");

    digester.addObjectCreate("*/object", InsertObject.class);
    digester.addCallMethod("*/object", "setType", 1);
    digester.addCallParam("*/object", 0, "type");

    digester.addCallMethod("*/attribute", "setAttribute", 2);
    digester.addCallParam("*/attribute", 0, "name");
    digester.addCallParam("*/attribute", 1);

    digester.addCallMethod("*/file", "setCheckinObject", 2);
    digester.addCallParam("*/file", 0, "name");
    digester.addCallParam("*/file", 1, "url");

    digester.addCallMethod("*/parentattribute", "setParentAttribute", 1);
    digester.addCallParam("*/parentattribute", 0, "name");

    digester.addSetNext("*/object", "addChild",
        "org.efaps.importer.InsertObject");

    digester.addObjectCreate("*/linkattribute", ForeignObject.class);
    digester.addCallMethod("*/linkattribute", "setLinkAttribute", 2);
    digester.addCallParam("*/linkattribute", 0, "name");
    digester.addCallParam("*/linkattribute", 1, "type");

    digester.addCallMethod("*/queryattribute", "setAttribute", 2);
    digester.addCallParam("*/queryattribute", 0, "name");
    digester.addCallParam("*/queryattribute", 1);

    digester.addSetNext("*/linkattribute", "addLink",
        "org.efaps.importer.ForeignObject");

    try {
      digester.parse(new File(_xml));
    }
    catch (IOException e) {
      e.printStackTrace(System.err);
    }
    catch (SAXException e) {
      e.printStackTrace(System.err);
    }

  }

  public static void insertDB() {
    RootObject.insertDB();
  }

}
