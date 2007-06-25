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

package org.efaps.beans;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.AttributeTypeInterface;
import org.efaps.admin.datamodel.ui.UIInterface;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.ui.CommandAbstract;
import org.efaps.admin.ui.Field;
import org.efaps.admin.ui.Table;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;
import org.efaps.admin.event.ReturnInterface;
import org.efaps.admin.event.TriggerEvent;

/**
 * @author tmo
 * @version $Id: TableBean.java 675 2007-02-14 20:56:25 +0000 (Wed, 14 Feb 2007)
 *          jmo $
 * @todo description
 */
public class TableBean extends AbstractCollectionBean  {

  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * All field definitions for the table are defined in this list.
   *
   * @see #evalFieldDefs
   * @see #getFieldDefs
   */
  private final List < FieldDefinition > fieldDefs
          = new ArrayList < FieldDefinition > ();

  /**
   * All evaluated rows of this table are stored in this list.
   *
   * @see #getValues
   */
  private final List < Row > values
          = new ArrayList < Row > ();

  /**
   * The instance variable stores the table which must be shown.
   * 
   * @see #getTable
   * @see #setTable
   */
  private Table table = null;

  /**
   * The instance variable stores the string of the sort key.
   * 
   * @see #getSortKey
   * @see #setSortKey
   */
  private String sortKey        = null;

  /**
   * The instance variable stores the string of the sort direction.
   * 
   * @see #getSortDirection
   * @see #setSortDirection
   */
  private String sortDirection  = null;

  /**
   * The instance variable stores the current selected filter of this web table
   * representation.
   * 
   * @see #getSelectedFilter
   * @see #setSelectedFilter(int)
   * @see #setSelectedFilter(String)
   */
  int selectedFilter = 0;

  /////////////////////////////////////////////////////////////////////////////

  public TableBean() throws EFapsException {
    super();
    System.out.println("TableBean.constructor");
  }

  public void finalize() {
    System.out.println("TableBean.destructor");
  }

  public void execute() throws Exception {

    evalFieldDefs();

    Context context = Context.getThreadContext();
    System.out.println("--->selectedFilter=" + getSelectedFilter());

List<ReturnInterface> list = getCommand().executeTrigger(TriggerEvent.UI_TABLE_EVALUATE);

System.out.println("list="+list);

    SearchQuery query = new SearchQuery();


    if (getCommand().getProperty("TargetQueryTypes") != null) {
      query.setQueryTypes(getCommand().getProperty("TargetQueryTypes"));
    } else if (getCommand().getProperty("TargetExpand") != null) {
      query.setExpand(getInstance(), getCommand().getProperty(
          "TargetExpand"));
    }

    query.add(context, getTable());

    if (getCommand().getTargetTableFilters() != null) {
      if (getSelectedFilter() == 0
          && getCommand().getTargetTableFilters().size() > 0) {
        setSelectedFilter(1);
      }
      /*
       * if (getCommand().getTargetTableFilters().size()>=getSelectedFilter() &&
       * getSelectedFilter()>0) { String clause =
       * ((CommandAbstract.TargetTableFilter)getCommand().getTargetTableFilters().get(getSelectedFilter()-1)).getClause();
       * if (clause!=null) { query.addWhere(context, clause); } }
       */
    }

    query.execute();

    executeRowResult(context, query);

    setInitialised(true);
  }

  /**
   * The field definitions for the current table in {@link #table} are set.
   * Each existing label of a field column are translated.
   *
   * @see #fieldDefs
   * @todo depending on the access on a field the field definition is set
   */
  protected void evalFieldDefs()  {
    this.fieldDefs.clear();
    for (Field field : this.table.getFields())  {
      String label = field.getLabel();
      if (label != null)  {
        label = DBProperties.getProperty(label);
      }
      this.fieldDefs.add(new FieldDefinition(label, field));
    }
  }


  void executeRowResult(Context _context, SearchQuery _query) throws Exception {
    while (_query.next()) {
      Row row = new Row(_query.getRowOIDs(_context));
      boolean toAdd = false;
      for (FieldDefinition fieldDef : this.fieldDefs) {
        Object value = null;
        Attribute attr = null;
        // if (field.getProgramValue()!=null) {
        // attrValue = field.getProgramValue().evalAttributeValue(_context,
        // _query);
        // } else
        if (fieldDef.getField().getExpression() != null) {
          value = _query.get(fieldDef.getField());
          attr = _query.getAttribute(fieldDef.getField());
        }
        Instance instance = _query.getInstance(_context, fieldDef.getField());
        // if (attrValue!=null) {
        // attrValue.setField(field);
        // }
        UIInterface classUI = null;
        if (attr != null) {
          classUI = attr.getAttributeType().getUI();
        }
        toAdd = toAdd || (value != null) || (instance != null);
        row.add(fieldDef, classUI, value, instance);
      }
      if (toAdd) {
        getValues().add(row);
      }
    }
  }

  /**
   * The instance method sorts the table values depending on the sort key in
   * {@link #sortKey} and the sort direction in {@link #sortDirection}.
   */
  public boolean sort() {
    /*
     * if (getSortKey()!=null && getSortKey().length()>0) { int sortKey = 0; for
     * (int i=0; i<getTable().getFields().size(); i++) { Field field =
     * (Field)getTable().getFields().get(i); if
     * (field.getName().equals(getSortKey())) { sortKey = i; break; } }
     * 
     * final int index = sortKey; Collections.sort(getValues(), new Comparator<Row>(){
     * public int compare(Row _o1, Row _o2) { int ret; AttributeTypeInterface a1 =
     * _o1.getValues().get(index).getAttrValue(); AttributeTypeInterface a2 =
     * _o2.getValues().get(index).getAttrValue(); return
     * a1.compareTo(getLocale(), a2); } } );
     * 
     * if (getSortDirection()!=null && getSortDirection().equals("-")) {
     * Collections.reverse(getValues()); } }
     */
    return true;
  }

  /**
   * 
   * @param _name
   *          name of the command object
   */
  public void setCommandName(String _name) throws EFapsException {
    super.setCommandName(_name);
    if (getCommand() != null) {
      setTable(getCommand().getTargetTable());

      if (getCommand().getTargetTableSortKey() != null) {
        setSortKey(getCommand().getTargetTableSortKey());
        if (getCommand().getTargetTableSortDirection() == CommandAbstract.TABLE_SORT_DIRECTION_DOWN) {
          setSortDirection("-");
        }
      }
    }
  }

  /**
   * With this instance method the checkboxes for the web table is controlled.
   * The value is get from the calling command which owns a property
   * <i>targetShowCheckBoxes</i> if the value <i>true</i>.
   * 
   * @return <i>true</i> if the check boxes must be shown, other <i>false</i>
   *         is returned.
   */
  public boolean isShowCheckBoxes() {
    return getCommand().isTargetShowCheckBoxes();
  }

  /////////////////////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////////////////////

  /**
   * This is the getter method for the instance variable {@link #fieldDefs}.
   * 
   * @return value of instance variable {@link #fieldDefs}
   * @see #fieldDefs
   * @see #evalFieldDefs
   */
  public List < FieldDefinition > getFieldDefs()  {
    return this.fieldDefs;
  }

  /**
   * This is the getter method for the instance variable {@link #values}.
   *
   * @return value of instance variable {@link #values}
   * @see #values
   * @see #setValues
   */
  public List < Row > getValues()  {
    return this.values;
  }

  /**
   * This is the getter method for the instance variable {@link #table}.
   * 
   * @return value of instance variable {@link #table}
   * @see #table
   * @see #setTable
   */
  public Table getTable() {
    return this.table;
  }

  /**
   * This is the setter method for the instance variable {@link #table}.
   * 
   * @param _table
   *          new value for instance variable {@link #table}
   * @see #table
   * @see #getTable
   */
  public void setTable(Table _table) {
    this.table = _table;
  }

  /**
   * This is the getter method for the instance variable {@link #sortKey}.
   * 
   * @return value of instance variable {@link #sortKey}
   * @see #sortKey
   * @see #setSortKey
   */
  public String getSortKey() {
    return this.sortKey;
  }

  /**
   * This is the setter method for the instance variable {@link #sortKey}.
   * 
   * @param _sortKey
   *          new value for instance variable {@link #sortKey}
   * @see #sortKey
   * @see #getSortKey
   */
  public void setSortKey(String _sortKey) {
    this.sortKey = _sortKey;
  }

  /**
   * This is the getter method for the instance variable {@link #sortDirection}.
   * 
   * @return value of instance variable {@link #sortDirection}
   * @see #sortDirection
   * @see #setSortDirection
   */
  public String getSortDirection() {
    return this.sortDirection;
  }

  /**
   * This is the setter method for the instance variable {@link #sortDirection}.
   * 
   * @param _sortDirection
   *          new value for instance variable {@link #sortDirection}
   * @see #sortDirection
   * @see #getSortDirection
   */
  public void setSortDirection(String _sortDirection) {
    this.sortDirection = _sortDirection;
  }

  /**
   * This is the getter method for the instance variable {@link #selectedFilter}.
   * 
   * @return value of instance variable {@link #selectedFilter}
   * @see #selectedFilter
   * @see #setSelectedFilter
   */
  public int getSelectedFilter() {
    return this.selectedFilter;
  }

  /**
   * This is the setter method for the instance variable {@link #selectedFilter}.
   * 
   * @param _selectedFilter
   *          new value for instance variable {@link #selectedFilter}
   * @see #selectedFilter
   * @see #getSelectedFilter
   */
  public void setSelectedFilter(int _selectedFilter) {
    this.selectedFilter = _selectedFilter;
  }

  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////

  /**
   * The inner class stores one row of the table.
   */
  public class Row {

    ///////////////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The instance variable stores the values for the table.
     * 
     * @see #getValues
     */
    private final List < FieldValue > values = new ArrayList < FieldValue >();

    /**
     * The instance variable stores all oids in a string.
     * 
     * @see #getOids
     */
    private final String oids;

    ///////////////////////////////////////////////////////////////////////////
    // contructors / destructors

    /**
     * The constructor creates a new instance of class Row.
     * 
     * @param _oids
     *          string with all oids for this row
     */
    public Row(final String _oids) {
      this.oids = _oids;
    }

    ///////////////////////////////////////////////////////////////////////////
    // instance methods

    /**
     * The instance method adds a new attribute value (from instance
     * {@link AttributeTypeInterface}) to the values.
     * 
     * @see #values
     */
    public void add(final FieldDefinition _field,
                    final UIInterface _classUI,
                    final Object _value,
                    final Instance _instance) {

      this.values.add(
            new FieldValue(_field, _classUI, _value, _instance));
    }

    /**
     * The instance method returns the size of the array list {@link #values}.
     * 
     * @see #values
     */
    public int getSize() {
      return getValues().size();
    }

    // /////////////////////////////////////////////////////////////////////////

    /**
     * This is the getter method for the values variable {@link #values}.
     * 
     * @return value of values variable {@link #values}
     * @see #values
     */
    public List < FieldValue > getValues() {
      return this.values;
    }

    /**
     * This is the getter method for the instance variable {@link #oids}.
     * 
     * @return value of instance variable {@link #oids}
     * @see #oids
     */
    public String getOids() {
      return this.oids;
    }
  }
}
