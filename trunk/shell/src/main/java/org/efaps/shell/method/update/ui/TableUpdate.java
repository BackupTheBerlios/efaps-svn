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

package org.efaps.shell.method.update.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.db.Delete;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.shell.method.update.AbstractUpdate;
import org.efaps.util.EFapsException;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class TableUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(TableUpdate.class);

  /** Link from field to icon */
  private final static Link LINKFIELD2ICON
                    = new Link("Admin_UI_LinkIcon", 
                               "From", 
                               "Admin_UI_Image", "To");

  private final static Set <Link> ALLLINKS = new HashSet < Link > ();  {
/*    ALLLINKS.add(LINK2ACCESSTYPE);
    ALLLINKS.add(LINK2DATAMODELTYPE);
    ALLLINKS.add(LINK2PERSON);
    ALLLINKS.add(LINK2ROLE);
    ALLLINKS.add(LINK2GROUP);
*/
  }

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public TableUpdate() {
    super("Admin_UI_Table", ALLLINKS);
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static TableUpdate readXMLFile(final String _fileName) throws IOException  {
//    } catch (IOException e)  {
//      LOG.error("could not open file '" + _fileName + "'", e);
    return readXMLFile(new File(_fileName));
  }

  public static TableUpdate readXMLFile(final File _file) throws IOException  {
    TableUpdate ret = null;

    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("ui-table", TableUpdate.class);

      digester.addCallMethod("ui-table/uuid", "setUUID", 1);
      digester.addCallParam("ui-table/uuid", 0);

      digester.addObjectCreate("ui-table/definition", Definition.class);
      digester.addSetNext("ui-table/definition", "addDefinition");

      digester.addCallMethod("ui-table/definition/version", "setVersion", 4);
      digester.addCallParam("ui-table/definition/version/application", 0);
      digester.addCallParam("ui-table/definition/version/global", 1);
      digester.addCallParam("ui-table/definition/version/local", 2);
      digester.addCallParam("ui-table/definition/version/mode", 3);
      
      digester.addCallMethod("ui-table/definition/name", "setName", 1);
      digester.addCallParam("ui-table/definition/name", 0);

      digester.addCallMethod("ui-table/definition/property", "addProperty", 2);
      digester.addCallParam("ui-table/definition/property/name", 0);
      digester.addCallParam("ui-table/definition/property/value", 1);

      digester.addObjectCreate("ui-table/definition/field", Field.class);
      digester.addSetNext("ui-table/definition/field", "addField");

      digester.addCallMethod("ui-table/definition/field/name", "setName", 1);
      digester.addCallParam("ui-table/definition/field/name", 0);

      digester.addCallMethod("ui-table/definition/field/icon", "setIcon", 1);
      digester.addCallParam("ui-table/definition/field/icon", 0);

      digester.addCallMethod("ui-table/definition/field/property", "addProperty", 2);
      digester.addCallParam("ui-table/definition/field/property/name", 0);
      digester.addCallParam("ui-table/definition/field/property/value", 1);

      ret = (TableUpdate) digester.parse(_file);
    } catch (SAXException e)  {
e.printStackTrace();
      //      LOG.error("could not read file '" + _fileName + "'", e);
    }
    return ret;
  }

  /////////////////////////////////////////////////////////////////////////////
  // class for a field

  public static class Field  {
    
    /** Name of the field. */
    private String name = null;
    
    /** Icon of the field. */
    private String icon = null;
    
    /**
     * Property value depending on the property name for this field of a 
     * definition.
     *
     * @see #addProperty.
     */
    private final Map < String, String > properties 
                                          = new HashMap < String, String > ();

    /**
     * @see #name
     */
    public void setName(final String _name)  {
      this.name = _name;
    }

    /**
     * Add a new property with given name and value to this definition.
     *
     * @param _name   name of the property to add
     * @param _value  value of the property to add
     * @see #properties
     */
    public void addProperty(final String _name, final String _value)  {
      this.properties.put(_name, _value);
    }

    /**
     *
     */
    public void setIcon(final String _icon)  {
      this.icon = _icon;
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a field.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
        .append("name",       this.name)
        .append("properties", this.properties)
        .toString();
    }
  }
  
  
  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class Definition extends DefinitionAbstract {

    /** All fields for the table are stored in this variable */
    private List < Field > fields = new ArrayList < Field > ();
    
    ///////////////////////////////////////////////////////////////////////////
    // instance methods

    /**
     *
     */
    public Instance updateInDB(final Instance _instance,
                           final Set < Link > _allLinkTypes,
                           final Insert _insert) throws EFapsException, Exception  {

      Instance instance = super.updateInDB(_instance, _allLinkTypes, _insert);

      // cleanup fields (remove all fields from table)
      SearchQuery query = new SearchQuery();
      query.setExpand(instance, "Admin_UI_Field\\Collection");
      query.addSelect("OID");
      query.executeWithoutAccessCheck();
      while (query.next())  {
        (new Delete((String) query.get("OID"))).executeWithoutAccessCheck();
      }
      query.close();

      // append new fields
      for (Field field : this.fields)  {
        Insert insert = new Insert("Admin_UI_Field");
        insert.add("Collection", "" + instance.getId());
        insert.add("Name",       field.name);
        insert.executeWithoutAccessCheck();
        setPropertiesInDb(insert.getInstance(), field.properties);
        Map < String, Map < String, String > > iconsMap
                          = new HashMap < String, Map < String, String > > ();
        if (field.icon != null)  {
          iconsMap.put(field.icon, null);
        }
        setLinksInDB(insert.getInstance(), LINKFIELD2ICON, iconsMap);
      }
      
      return instance;
    }

    /**
     * Adds a new field to this definition of the table.
     *
     * @param _field new field to add to this table
     * @see #fields
     * @see #Field
     */
    public void addField(final Field _field)  {
      this.fields.add(_field);
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a field.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
        .appendSuper(super.toString())
        .append("fields",       this.fields)
        .toString();
    }
  }
}