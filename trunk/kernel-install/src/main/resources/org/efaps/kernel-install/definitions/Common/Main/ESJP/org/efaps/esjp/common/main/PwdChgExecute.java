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

package org.efaps.esjp.common.main;

import org.efaps.admin.AdminObject.EFapsClassName;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.db.Context;
import org.efaps.db.Update;
import org.efaps.db.Update.Status;
import org.efaps.util.EFapsException;

/**
 * @author jmo
 * @version $Id$
 * @todo description
 */
public class PwdChgExecute implements EventExecution {

  /**
   * @param _parameter
   */
  public Return execute(final Parameter _parameter) throws EFapsException {

    final Context context = Context.getThreadContext();
    final String passwordold = context.getParameter("passwordold");
    final String passwordnew = context.getParameter("passwordnew");
    final Return ret = new Return();

    if (context.getPerson().checkPassword(passwordold)) {
      final Type type = Type.get(EFapsClassName.USER_PERSON.name);
      final Update update = new Update(type, "" + context.getPerson().getId());
      final Status status = update.add("Password", passwordnew);

      if ((status.isOk())) {
        update.execute();
        ret.put(ReturnValues.TRUE, "true");
      } else {
        ret.put(ReturnValues.VALUES, status.getReturnValue());
      }
    } else {
      ret.put(ReturnValues.VALUES,
          "Common_Main_PwdChgForm/PwdChgExecute.checkPassword");
    }
    return ret;
  }

}
