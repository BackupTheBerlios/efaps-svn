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

import javax.naming.Context;
import javax.naming.InitialContext;

import org.efaps.db.transaction.VFSStoreFactoryBean;
import org.efaps.util.EFapsException;

public class StartImport extends AbstractTransaction {

  public static void main(String[] _args) {
    
    (new StartImport()).execute(_args);

  }

  public void execute(final String... _args) {
    
// "/Users/janmoxter/Documents/workspace/ydss/bootstrap.xml"
    setBootstrap(_args[0]);
// "file:///Users/janmoxter/Documents/apache-tomcat-5.5.20/webapps/ydss/docs/efaps/store/documents"
    String BaseName = _args[1];
//    "/Users/janmoxter/Documents/workspace/ydss/kernel/src/main/java/org/efaps/importer/Import.xml"
    String ImportFrom = _args[2];
    
    loadRunLevel();
    
    try {
      login("Administrator", "");
      super.startTransaction();

      System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
          "org.efaps.importer.InitialContextFactory");
      VFSStoreFactoryBean bean = new VFSStoreFactoryBean();
      bean.setBaseName(BaseName);
      bean.setProvider("org.apache.commons.vfs.provider.local.DefaultLocalFileProvider");

      Context ctx = new InitialContext();
      ctx.bind("java:comp/env", ctx);
      ctx.bind("eFaps/store/documents", bean);

      AbstractObject
          .importFromXML(ImportFrom);
      AbstractObject.insertDB();

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
}
