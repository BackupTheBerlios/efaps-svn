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

package org.efaps.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.efaps.db.Context;
import org.efaps.db.transaction.ConnectionResource;
import org.efaps.util.EFapsException;

public class Properties {
  /**
   * Logger for this class
   */
  private static final Log           LOG       = LogFactory
                                                   .getLog(Properties.class);

  private static Map<String, String> PROPCACHE = new HashMap<String, String>();

  private static String              LANGUAGE  = null;

  public static void setLanguage(String _Language) {
    LANGUAGE = _Language;

  }

  public static String getProperty(String _key) {
    return PROPCACHE.get(_key);

  }

  public Properties(String _Language) {
    setLanguage(_Language);
    initialise();
  }

  public static String getLanguage() {
    if (LANGUAGE == null) {
      setLanguage(Locale.getDefault().getLanguage());
    }
    return LANGUAGE;
  }

  public Properties() {
    new Properties(getLanguage());
  }

  public static void initialise(String _Language) {
    setLanguage(_Language);
    initialise();
  }

  public static void initialise() {
    String SQLStmt = "select KEY, DEFAULTV, VALUE from T_ADPROP "
        + " left join (select PROPID, VALUE from T_ADPROPLOC"
        + " left join T_ADLANG on (T_ADPROPLOC.LANGID = T_ADLANG.ID)"
        + " where LANG ='" + getLanguage() + "') tmp "
        + " on ( T_ADPROP.ID = tmp.propid )";

    ConnectionResource con;
    String value;
    try {
      con = Context.getThreadContext().getConnectionResource();
      Statement stmt = con.getConnection().createStatement();

      ResultSet rs = stmt.executeQuery(SQLStmt);
      while (rs.next()) {
        value = rs.getString(3);
        if (value == null) {
          value = rs.getString(2);
        }
        PROPCACHE.put(rs.getString(1).trim(), value.trim());
      }
    } catch (EFapsException e) {

      LOG.error("initialise()", e);
    } catch (SQLException e) {

      LOG.error("initialise()", e);
    }

  }

}
