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

package org.efaps.shell.method.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.db.databases.AbstractDatabase;
import org.efaps.db.transaction.ConnectionResource;
import org.efaps.shell.method.update.AbstractUpdate;
import org.efaps.util.EFapsException;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class SQLTableUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(SQLTableUpdate.class);

  /** Link from JAAS systems to persons */
  private final static Link LINK2PERSONS
                    = new Link("Admin_User_JAASKey", 
                               "JAASSystemLink", 
                               "Admin_User_Person", "UserLink");

  private final static Set <Link> ALLLINKS = new HashSet < Link > ();  {
//    ALLLINKS.add(LINK2PERSONS);
//    ALLLINKS.add(LINK2ROLES);
//    ALLLINKS.add(LINK2GROUPS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public SQLTableUpdate() {
    super("Admin_DataModel_SQLTable", ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static SQLTableUpdate readXMLFile(final String _fileName) throws IOException  {
    return readXMLFile(new File(_fileName));
  }

  public static SQLTableUpdate readXMLFile(final File _file) throws IOException  {
    SQLTableUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("datamodel-sqltable", SQLTableUpdate.class);

      digester.addCallMethod("datamodel-sqltable/uuid", "setUUID", 1);
      digester.addCallParam("datamodel-sqltable/uuid", 0);

      digester.addObjectCreate("datamodel-sqltable/definition", Definition.class);
      digester.addSetNext("datamodel-sqltable/definition", "addDefinition");

      digester.addCallMethod("datamodel-sqltable/definition/version", "setVersion", 4);
      digester.addCallParam("datamodel-sqltable/definition/version/application", 0);
      digester.addCallParam("datamodel-sqltable/definition/version/global", 1);
      digester.addCallParam("datamodel-sqltable/definition/version/local", 2);
      digester.addCallParam("datamodel-sqltable/definition/version/mode", 3);
      
      digester.addCallMethod("datamodel-sqltable/definition/name", "setName", 1);
      digester.addCallParam("datamodel-sqltable/definition/name", 0);

      digester.addCallMethod("datamodel-sqltable/definition/parent", "setParent", 1);
      digester.addCallParam("datamodel-sqltable/definition/parent", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/table-name", "setSQLTableName", 1);
      digester.addCallParam("datamodel-sqltable/definition/database/table-name", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/parent-table", "setParentSQLTableName", 1);
      digester.addCallParam("datamodel-sqltable/definition/database/parent-table", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/table-name", "setSQLTableName", 1);
      digester.addCallParam("datamodel-sqltable/definition/database/table-name", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/create", "setCreate", 1, 
                             new Class[] {Boolean.class});
      digester.addCallParam("datamodel-sqltable/definition/database/create", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/update", "setUpdate", 1, 
                             new Class[] {Boolean.class});
      digester.addCallParam("datamodel-sqltable/definition/database/update", 0);

      digester.addCallMethod("datamodel-sqltable/definition/database/column", "addColumn", 4, 
                             new Class[] {String.class, String.class, Integer.class, Boolean.class});
      digester.addCallParam("datamodel-sqltable/definition/database/column", 0, "name");
      digester.addCallParam("datamodel-sqltable/definition/database/column", 1, "type");
      digester.addCallParam("datamodel-sqltable/definition/database/column", 2, "length");
      digester.addCallParam("datamodel-sqltable/definition/database/column", 3, "not-null");

      digester.addCallMethod("datamodel-sqltable/definition/database/unique", "addUniqueKey", 2);
      digester.addCallParam("datamodel-sqltable/definition/database/unique", 0, "name");
      digester.addCallParam("datamodel-sqltable/definition/database/unique", 1, "columns");

      digester.addCallMethod("datamodel-sqltable/definition/database/foreign", "addForeignKey", 3);
      digester.addCallParam("datamodel-sqltable/definition/database/foreign", 0, "name");
      digester.addCallParam("datamodel-sqltable/definition/database/foreign", 1, "key");
      digester.addCallParam("datamodel-sqltable/definition/database/foreign", 2, "reference");

      ret = (SQLTableUpdate) digester.parse(_file);
    } catch (SAXException e)  {
e.printStackTrace();
      //      LOG.error("could not read file '" + _fileName + "'", e);
    }
    return ret;
  }

  /////////////////////////////////////////////////////////////////////////////
  
  /**
   * The class defines a column in a sql table.
   */
  private static class Column  {
    /** Name of the column. */
    private final String name;
    /** Type of the column. */
    private final AbstractDatabase.ColumnType type;
    /** Length of the Column. */
    private final int length;
    /** Is null allowed in the column? */
    private final boolean isNotNull;

    private Column(final String _name, 
                   final AbstractDatabase.ColumnType _type,
                   final int _length,
                   final boolean _notNull)  {
      this.name = _name;
      this.type = _type;
      this.length = _length;
      this.isNotNull = _notNull;
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a column.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
        .append("name",       this.name)
        .append("type",       this.type)
        .append("isNotNull",  this.isNotNull)
        .toString();
    }
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * The class defines a unqiue key in a sql table.
   */
  private static class UniqueKey  {
    /** Name of the unique key. */
    private final String name;
    /** Columns of the unique key. */
    private final String columns;

    private UniqueKey(final String _name, 
                      final String _columns)  {
      this.name = _name;
      this.columns = _columns;
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a column.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
        .append("name",       this.name)
        .append("columns",    this.columns)
        .toString();
    }
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * The class defines a foreign key in a sql table.
   */
  private static class ForeignKey  {
    /** Name of the foreign key. */
    private final String name;
    /** Key of the foreign key. */
    private final String key;
    /** Reference of the foreign key. */
    private final String reference;

    private ForeignKey(final String _name,
                       final String _key,
                       final String _reference)  {
      this.name = _name;
      this.key = _key;
      this.reference = _reference;
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a column.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
        .append("name",       this.name)
        .append("key",        this.key)
        .append("reference",  this.reference)
        .toString();
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class Definition extends DefinitionAbstract  {
    
    ///////////////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The SQL table name of the parent table.
     */
    private String parentSQLTableName = null;

    private boolean create = false;
    
    private boolean update = false;

    private final List < Column > columns = new ArrayList < Column > ();

    private final List < UniqueKey > uniqueKeys 
                                            = new ArrayList < UniqueKey > ();

    private final List < ForeignKey > foreignKeys 
                                            = new ArrayList < ForeignKey > ();

    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @see #values
     */
    public void setSQLTableName(final String _value)  {
      addValue("SQLTable", _value);
      addValue("SQLColumnID", "ID");
    }

    /**
     * 
     * @see #values
     */
    public void setParentSQLTableName(final String _parentSQLTableName)  {
      this.parentSQLTableName = _parentSQLTableName;
    }

    /**
     * @todo throw Exception is not allowed
     */
    public void setParent(final String _parent) throws Exception {
      if ((_parent != null) && (_parent.length() > 0))  {
        Context context = Context.getThreadContext();
        // search for the instance
        SearchQuery query = new SearchQuery();
        query.setQueryTypes(context, "Admin_DataModel_SQLTable");
        query.addWhereExprEqValue(context, "Name", _parent);
        query.addSelect(context, "OID");
        query.executeWithoutAccessCheck();
        if (query.next())  {
          Instance instance = new Instance((String) query.get(context, "OID"));
          addValue("DMTableMain", "" + instance.getId());
        }  else  {
          addValue("DMTableMain", null);
        }
        query.close();
      } else  {
        addValue("DMTableMain", null);
      }
    }

    public void setCreate(final boolean _create)  {
      this.create = _create;
    }

    public void setUpdate(final boolean _update)  {
      this.update = _update;
    }

    public void addColumn(final String _name,
                          final String _type,
                          final int _length,
                          final boolean _notNull)  {
      this.columns.add(
            new Column(_name,
                       Enum.valueOf(AbstractDatabase.ColumnType.class, _type),
                       _length,
                       _notNull));
    }
    
    public void addUniqueKey(final String _name,
                             final String _columns)   {
      this.uniqueKeys.add(new UniqueKey(_name, _columns));
    }

    public void addForeignKey(final String _name,
                              final String _key,
                              final String _reference)   {
      this.foreignKeys.add(new ForeignKey(_name, _key, _reference));
    }

    /**
     *
     */
    public void updateInDB(final org.efaps.db.Instance _instance,
                           final Set < Link > _allLinkTypes,
                           final org.efaps.db.Insert _insert) throws EFapsException, Exception  {
      if (this.create)  {
        createSQLTable();
      }
      
      if (this.update)  {
        updateSQLTable();
      }
      if (getValue("Name") != null)  {
        super.updateInDB(_instance, _allLinkTypes, _insert);
      }
    }


    /**
     *
     */
    protected void createSQLTable() throws EFapsException  {
      Context context = Context.getThreadContext();
      ConnectionResource con = null;
      String tableName = getValue("SQLTable");
      try  {  
        con = context.getConnectionResource();

        Context.getDbType().createTable(con.getConnection(), 
                                        tableName, 
                                        this.parentSQLTableName);
        con.commit();

      } catch (EFapsException e)  {
        LOG.error(e);
        if (con != null)  {
          con.abort();
        }
        throw e;
      } catch (Throwable e)  {
        LOG.error(e);
        if (con != null)  {
          con.abort();
        }
        throw new EFapsException(getClass(), "createSQLTable.Throwable", e);
      }
    }

    /**
     *
     */
    protected void updateSQLTable() throws EFapsException  {
      Context context = Context.getThreadContext();
      ConnectionResource con = null;
      String tableName = getValue("SQLTable");
      try  {  
        con = context.getConnectionResource();

        Statement stmt = con.getConnection().createStatement();
        
        // update columns
        for (Column column : this.columns)  {
          StringBuilder cmd = new StringBuilder();
          cmd.append("alter table ").append(tableName).append(" ")
             .append("add ").append(column.name).append(" ")
             .append(Context.getDbType().getColumnType(column.type));
          if (column.length > 0)  {
            cmd.append("(").append(column.length).append(")");
          }
          if (column.isNotNull)  {
            cmd.append(" not null");
          }
          stmt.execute(cmd.toString());
        }
        
        // update unique keys
        for (UniqueKey uniqueKey : this.uniqueKeys)  {
          StringBuilder cmd = new StringBuilder();
          cmd.append("alter table ").append(tableName).append(" ")
             .append("add constraint ").append(uniqueKey.name).append(" ")
             .append("unique(").append(uniqueKey.columns).append(")");
          stmt.execute(cmd.toString());
        }
        
        // update foreign keys
        for (ForeignKey foreignKey : this.foreignKeys)  {
          StringBuilder cmd = new StringBuilder();
          cmd.append("alter table ").append(tableName).append(" ")
             .append("add constraint ").append(foreignKey.name).append(" ")
             .append("foreign key(").append(foreignKey.key).append(") ")
             .append("references ").append(foreignKey.reference);
          stmt.execute(cmd.toString());
        }
        
        con.commit();

      } catch (EFapsException e)  {
        LOG.error(e);
        if (con != null)  {
          con.abort();
        }
        throw e;
      } catch (Throwable e)  {
        LOG.error(e);
        if (con != null)  {
          con.abort();
        }
        throw new EFapsException(getClass(), "updateSQLTable.Throwable", e);
      }
    }
  }
}
