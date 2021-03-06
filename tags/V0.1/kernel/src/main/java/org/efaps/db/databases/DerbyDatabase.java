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
 */

package org.efaps.db.databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;

public class DerbyDatabase extends AbstractDatabase  {

// TODO: specificy real column type
  public DerbyDatabase() throws ClassNotFoundException, IllegalAccessException  {
    super();

    this.columnMap.put(ColumnType.INTEGER,      "bigint");
//    this.columnMap.put(ColumnType.REAL,         "real");
    this.columnMap.put(ColumnType.STRING_SHORT, "char");
    this.columnMap.put(ColumnType.STRING_LONG,  "varchar");
    this.columnMap.put(ColumnType.DATETIME,     "timestamp");
    this.columnMap.put(ColumnType.BLOB,         "blob(2G)");
    this.columnMap.put(ColumnType.CLOB,         "clob(2G)");
  }

  public String getCurrentTimeStamp()  {
    return "current_timestamp";
  }

  /**
   * This is the Derby specific implementation of an all deletion. Following
   * order is used to remove all eFaps specific information:
   * <ul>
   * <li>remove all foreign keys of the user</li>
   * <li>remove all views of the user</li>
   * <li>remove all tables of the user</li>
   * </ul>
   * Attention! If application specific tables, views or contraints are defined,
   * this database objects are also removed!
   *
   * @param _con  sql connection
   * @throws SQLException
   */
  public void deleteAll(final Connection _con) throws SQLException  {

    Statement stmtSel = _con.createStatement();
    Statement stmtExec = _con.createStatement();

    try  {
    // remove all foreign keys
//    print("Remove Foreign Keys");
      ResultSet rs = stmtSel.executeQuery("select t.TABLENAME, c.CONSTRAINTNAME from SYS.SYSSCHEMAS s, SYS.SYSTABLES t, SYS.SYSCONSTRAINTS c where s.AUTHORIZATIONID<>'DBA' and s.SCHEMAID=t.SCHEMAID and t.TABLEID=c.TABLEID and c.TYPE='F'");
      while (rs.next())  {
        String tableName = rs.getString(1);
        String constrName = rs.getString(2);
//      print("  - Table '"+table+"' Constraint '"+constr+"'");
        stmtExec.execute("alter table " + tableName + " drop constraint " + constrName);
      }
      rs.close();

      // remove all views
//    print("Remove Views");
      rs = stmtSel.executeQuery("select t.TABLENAME from SYS.SYSSCHEMAS s, SYS.SYSTABLES t where s.AUTHORIZATIONID<>'DBA' and s.SCHEMAID=t.SCHEMAID and t.TABLETYPE='V'");
      while (rs.next())  {
        String viewName = rs.getString(1);
//      print("  - View '"+table+"'");
        stmtExec.execute("drop view " + viewName);
      }
      rs.close();

      // remove all tables
//    print("Remove Tables");
      rs = stmtSel.executeQuery("select t.TABLENAME from SYS.SYSSCHEMAS s, SYS.SYSTABLES t where s.AUTHORIZATIONID<>'DBA' and s.SCHEMAID=t.SCHEMAID and t.TABLETYPE='T'");
      while (rs.next())  {
        String tableName = rs.getString(1);
//      print("  - Table '"+table+"'");
        stmtExec.execute("drop table " + tableName);
      }
      rs.close();
    } finally  {
      stmtSel.close();
      stmtExec.close();
    }
  }
}