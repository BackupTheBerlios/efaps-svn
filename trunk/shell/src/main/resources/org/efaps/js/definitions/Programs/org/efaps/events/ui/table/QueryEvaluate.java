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
 * Revision:        $Rev:851 $
 * Last Changed:    $Date:2007-06-02 12:36:03 -0500 (Sat, 02 Jun 2007) $
 * Last Changed By: $Author:jmo $
 */

package org.efaps.events.ui.table;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.ui.CommandAbstract;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

/**
 * 
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class QueryEvaluate implements EventExecution {
  /**
   * Logger for this class
   */
  private static final Log LOG = LogFactory.getLog(QueryEvaluate.class);

  public Return execute(final Parameter _parameter) {
Return ret = new Return();
try {
    
    CommandAbstract command = (CommandAbstract) _parameter.get(ParameterValues.UIOBJECT);

    String types = command.getProperty("TargetQueryTypes");

    System.out.println("types="+types);

    SearchQuery query = new SearchQuery();
    query.setQueryTypes(command.getProperty("TargetQueryTypes"));
    query.addSelect("OID");
    query.execute();

    List < List < Instance >> list = new ArrayList < List < Instance >> ();
    while (query.next())  {
      List < Instance > instances = new ArrayList < Instance > (1);
      instances.add(new Instance((String) query.get("OID")));
      list.add(instances);
    }

    ret.put(ReturnValues.VALUES, list);
} catch (EFapsException e)  {
  e.printStackTrace();
}
    
    return ret;
  }
}