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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.importer;

import java.io.File;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.digester.Digester;
import org.efaps.db.transaction.VFSStoreFactoryBean;
import org.efaps.util.EFapsException;
import org.xml.sax.SAXException;

public class StartImport extends AbstractTransaction {

  private RootObject root = null;
  
  public static void main(String[] _args) {

    (new StartImport()).execute(_args);

  }

  public void execute(final String... _args) {

    // "/Users/janmoxter/Documents/workspace/ydss/bootstrap.xml"
    setBootstrap(_args[0]);

    // "file:///Users/janmoxter/Documents/apache-tomcat-5.5.20/webapps/ydss/docs/efaps/store/documents"
    String BaseName = _args[1];
    System.out.println(_args[1]);

    // "/Users/janmoxter/Documents/workspace/ydss/kernel/src/main/java/org/efaps/importer/Import.xml"
    String ImportFrom = _args[2];
    System.out.println(_args[2]);
    loadRunLevel();

    try {
      login("Administrator", "");
      super.startTransaction();

      System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
          "org.efaps.importer.InitialContextFactory");
      VFSStoreFactoryBean bean = new VFSStoreFactoryBean();
      bean.setBaseName(BaseName);
      bean
          .setProvider("org.apache.commons.vfs.provider.local.DefaultLocalFileProvider");

      Context ctx = new InitialContext();
      ctx.bind("java:comp/env", ctx);
      ctx.bind("eFaps/store/documents", bean);

      importFromXML(ImportFrom);
      insertDB();

      commitTransaction();

    }
    catch (EFapsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void importFromXML(final String _xml) {
    Digester digester = new Digester();

    digester.setValidating(false);

    digester.addObjectCreate("import", RootObject.class);
    
    
    digester.addCallMethod("import/definition/date", "setDateFormat", 1);
    digester.addCallParam("import/definition/date", 0, "format");

    digester.addFactoryCreate("import/definition/order", new OrderObjectFactory(), false);
    digester.addCallMethod("import/definition/order/attribute", "addAttribute",3,new Class[]{Integer.class,String.class,String.class});
    digester.addCallParam("import/definition/order/attribute", 0, "index");
    digester.addCallParam("import/definition/order/attribute", 1, "name");
    digester.addCallParam("import/definition/order/attribute", 2, "criteria");
    
    digester.addSetNext("import/definition/order", "addOrder",
    "org.efaps.importer.OrderObject");
       
    
    digester.addObjectCreate("*/object", InsertObject.class);
    digester.addCallMethod("*/object", "setType", 1);
    digester.addCallParam("*/object", 0, "type");

    digester.addCallMethod("*/object/attribute", "setAttribute", 3);
    digester.addCallParam("*/object/attribute", 0, "name");
    digester.addCallParam("*/object/attribute", 1);
    digester.addCallParam("*/object/attribute", 2,"unique");

    digester.addCallMethod("*/file", "setCheckinObject", 2);
    digester.addCallParam("*/file", 0, "name");
    digester.addCallParam("*/file", 1, "url");

    digester.addCallMethod("*/parentattribute", "setParentAttribute", 2);
    digester.addCallParam("*/parentattribute", 0, "name");
    digester.addCallParam("*/parentattribute", 1, "unique");

    digester.addCallMethod("*/linkattribute", "addUniqueAttribute", 2);
    digester.addCallParam("*/linkattribute", 0, "unique");
    digester.addCallParam("*/linkattribute", 1, "name");
    
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
      this.root = (RootObject) digester.parse(new File(_xml));
    }
    catch (IOException e) {
      e.printStackTrace(System.err);
    }
    catch (SAXException e) {
      e.printStackTrace(System.err);
    }

  }

  public void insertDB() {
    this.root.insertDB();
  }

}
