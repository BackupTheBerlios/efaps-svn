/*
 * Copyright 2005 The eFaps Team
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
 */

package org.efaps.admin.datamodel.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.ui.Field;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;

public class TypeUI implements UIInterface  {
  /**
   * @param _locale   locale object
   */
  public String getViewHtml(Context _context, Object _value, Field _field) throws EFapsException  {
    String ret = null;

    if (_value instanceof Type)  {
      Type value = ((Type)_value);

      String name = value.getName();

      ResourceBundle msgs = ResourceBundle.getBundle("StringResource", _context.getLocale());
      try  {
        ret = msgs.getString(name+".Label");
      } catch (MissingResourceException e)  {
        ret = "???" + name + ".Label???";
      }
    } else  {
// throw new EFapsException();
    }
    return ret;
  }

  /**
   * @param _locale   locale object
   */
  public String getEditHtml(Context _context, Object _value, Field _field) throws EFapsException  {
return "edit";
  }

  /**
   * @param _locale   locale object
   */
  public String getCreateHtml(Context _context, Object _value, Field _field) throws EFapsException  {
return "create";
  }

  /**
   * @param _locale   locale object
   */
  public String getSearchHtml(Context _context, Object _value, Field _field) throws EFapsException  {
return "search";
  }
}
