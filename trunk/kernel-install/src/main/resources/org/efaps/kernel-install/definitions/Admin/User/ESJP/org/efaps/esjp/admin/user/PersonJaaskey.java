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

package org.efaps.esjp.admin.user;

import java.util.Map;

import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

/**
 * This esjp inserts the JAASSystem for a User into the eFaps-Database.<br>
 * It is executed on a INSERT_POST Trigger on the Type User_Person.
 * 
 * @author jmo
 * @version $Id:PersonJaaskey.java 1072 2007-07-17 19:00:42Z tmo $
 */
public class PersonJaaskey implements EventExecution {

  public Return execute(Parameter _parameter) throws EFapsException {
    Instance instance = (Instance) _parameter.get(ParameterValues.INSTANCE);
    Map<?, ?> values = (Map<?, ?>) _parameter.get(ParameterValues.NEW_VALUES);

    String jaassystemid = getJAASSystemID();
    if (jaassystemid != null) {
      Insert insert = new Insert("Admin_User_JAASKey");

      insert.add("Key", values.get(instance.getType().getAttribute("Name"))
          .toString());
      insert.add("JAASSystemLink", getJAASSystemID());
      insert.add("UserLink", ((Long) instance.getId()).toString());
      insert.execute();
    }

    return null;
  }

  /**
   * get the ID of the JAASSYstem for eFaps
   * 
   * @return ID of the JAASSYSTEM, NULL if not found
   * @throws EFapsException
   */
  private String getJAASSystemID() throws EFapsException {
    String ID = null;

    SearchQuery query = new SearchQuery();
    query.setQueryTypes("Admin_User_JAASSystem");
    query.addWhereExprEqValue("Name", "eFaps");
    query.addSelect("ID");
    query.execute();
    if (query.next()) {
      ID = query.get("ID").toString();
    }
    query.close();

    return ID;
  }
}
