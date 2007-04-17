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
 * Revision:        $Rev: 732 $
 * Last Changed:    $Date: 2007-03-18 22:17:23 +0000 (Sun, 18 Mar 2007) $
 * Last Changed By: $Author: jmo $
 */

package org.efaps.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.db.Checkin;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.db.Update;
import org.efaps.util.EFapsException;

public class InsertObject extends AbstractObject {
  /**
   * Logger for this class
   */
  private static final Log                  LOG              = LogFactory
                                                                 .getLog(InsertObject.class);

  private String                            TYPE             = null;

  private Map<String, Object>               ATTRIBUTES       = new HashMap<String, Object>();

  private String                            PARENTATTRIBUTE  = null;

  private Map<String, List<AbstractObject>> CHILDS           = new HashMap<String, List<AbstractObject>>();

  private String                            ID               = null;

  private Set<ForeignObject>                LINKS            = new HashSet<ForeignObject>();

  private Set<String>                       UNIQUEATTRIBUTES = new HashSet<String>();

  private CheckinObject                     CHECKINOBJECT    = null;

  public InsertObject() {

  }

  public InsertObject(final String _type) {
    setType(_type);
  }

  public void setType(String _Type) {
    TYPE = _Type;
  }

  public void setAttribute(String _Name, String _Value, String _unique) {
    this.ATTRIBUTES.put(_Name, _Value.trim());

    if (_unique != null && _unique.equals("true")) {
      this.UNIQUEATTRIBUTES.add(_Name);
    }
  }

  public void setParentAttribute(String _ParentAttribute, String _unique) {
    this.PARENTATTRIBUTE = _ParentAttribute;

    if (_unique != null && _unique.equals("true")) {
      this.UNIQUEATTRIBUTES.add(_ParentAttribute);
    }

  }

  public void addChild(AbstractObject _object) {
    List<AbstractObject> list = this.CHILDS.get(_object.getType());
    if (list == null) {

      list = new ArrayList<AbstractObject>();
      this.CHILDS.put(_object.getType(), list);
      list.add(_object);
    } else {

      list.add(_object);
      if (RootObject.getOrder(_object.getType()) != null) {

        TreeSet<AbstractObject> treeSet = new TreeSet<AbstractObject>(
            RootObject.getOrder(_object.getType()));

        treeSet.addAll(list);
        list.clear();
        list.addAll(treeSet);
      }

    }
  }

  public void addLink(ForeignObject _Object) {
    this.LINKS.add(_Object);

  }

  public void addUniqueAttribute(String _unique, String _Name) {
    if (_unique != null && _unique.equals("true")) {
      this.UNIQUEATTRIBUTES.add(_Name);
    }
  }

  public void setID(String _ID) {
    ID = _ID;
  }

  public void insertObject() {

    String ID = null;
    boolean noInsert = false;
    
    
    for (List<AbstractObject> list : this.CHILDS.values()) {
      for (AbstractObject object : list) {

        try {
          if (object.getUniqueAttributes().size() > 0) {
            SearchQuery query = new SearchQuery();
            query.setQueryTypes(object.getType());
            query.addSelect("ID");
            for (String element : object.getUniqueAttributes()) {

              if (object.getAttributes().get(element) != null) {
                query.addWhereExprEqValue(element, object.getAttributes().get(
                    element).toString());
               
              }

              if (object.getParrentAttribute() != null
                  && object.getParrentAttribute().equals(element)) {
                query.addWhereExprEqValue(element, this.ID);
             
                
              }
              for (ForeignObject link : object.getLinks()) {
                if (link.getAttribute().equals(element)) {
                  String foreignID = link.getID();
                  if (foreignID != null) {
                    query.addWhereExprEqValue(element, foreignID);
                  
                  } else {
                    noInsert = true;
                  }
                }
              }

            }
            query.executeWithoutAccessCheck();

            if (query.next() && !noInsert) {
              ID = UpdateOrInsert(object, new Update(
                  Type.get(object.getType()), query.get("ID").toString()));

            } else {
              if (noInsert && object.hasChilds() == false) {
                LOG.error("skipt: " + object.toString());
              } else {
                ID = UpdateOrInsert(object, new Insert(object.getType()));
                
              }
            }

          } else {
            ID = UpdateOrInsert(object, new Insert(object.getType()));

          }
          object.setID(ID);

          if (object.isCheckinObject()) {
            object.checkObjectin();
          }

          object.insertObject();

        }

        catch (EFapsException e) {

          LOG.error("insertObject() " + this.toString(), e);
        }
        catch (Exception e) {

          LOG.error("insertObject() " + this.toString(), e);
        }
      }
    }
  }

  private String UpdateOrInsert(AbstractObject _Object, Update _UpIn) {
    try {
      for (Entry element : _Object.getAttributes().entrySet()) {

        if (element.getValue() instanceof Timestamp) {

          _UpIn
              .add(element.getKey().toString(), (Timestamp) element.getValue());

        } else {
          _UpIn.add(element.getKey().toString(), element.getValue().toString());
        }
      }
      if (_Object.getParrentAttribute() != null) {
        _UpIn.add(_Object.getParrentAttribute(), this.ID);
      }
      for (ForeignObject link : _Object.getLinks()) {
        _UpIn.add(link.getAttribute(), link.getID());
      }
      _UpIn.executeWithoutAccessCheck();
      String ID = _UpIn.getId();
      _UpIn.close();

      return ID;
    }
    catch (EFapsException e) {

      LOG.error("UpdateOrInsert() " + this.toString(), e);
    }
    catch (Exception e) {

      LOG.error("UpdateOrInsert() " + this.toString(), e);
    }

    return ID;

  }

  public String getID() {
    return this.ID;
  }

  @Override
  public String getType() {
    return this.TYPE;

  }

  @Override
  public Map<String, Object> getAttributes() {
    for (Entry element : this.ATTRIBUTES.entrySet()) {

      Attribute attribute = Type.get(this.TYPE).getAttribute(
          element.getKey().toString());

      if (attribute.getAttributeType().getClassRepr().getName().equals(
          "org.efaps.admin.datamodel.attributetype.DateTimeType")) {

        Date date = new SimpleDateFormat(RootObject.DATEFORMAT).parse(element
            .getValue().toString(), new ParsePosition(0));

        this.ATTRIBUTES.put((String) element.getKey(), new Timestamp(date
            .getTime()));
      }
    }
    return this.ATTRIBUTES;
  }

  public Object getAttribute(final String _attribute) {

    return (this.ATTRIBUTES.get(_attribute));
  }

  @Override
  public String getParrentAttribute() {

    return this.PARENTATTRIBUTE;
  }

  @Override
  public Set<ForeignObject> getLinks() {

    return this.LINKS;
  }

  public Set<String> getUniqueAttributes() {
    return this.UNIQUEATTRIBUTES;
  }

  public void setCheckinObject(String _Name, String _URL) {
    this.CHECKINOBJECT = new CheckinObject(_Name, _URL);

  }

  @Override
  public boolean isCheckinObject() {
    if (this.CHECKINOBJECT != null) {
      return true;
    }
    return false;
  }

  @Override
  public void checkObjectin() {

    Checkin checkin = new Checkin(new Instance(this.TYPE, this.ID));

    try {
      checkin.executeWithoutAccessCheck(this.CHECKINOBJECT.getName(),
          this.CHECKINOBJECT.getInputStream(), -1);
    }
    catch (EFapsException e) {

      LOG.error("checkObjectin() " + this.toString(), e);
    }
  }

  public String toString() {

    StringBuilder tmp = new StringBuilder();
    tmp.append("Type: ");
    tmp.append(this.TYPE);
    tmp.append(" - ParentAttribute: ");
    tmp.append(this.PARENTATTRIBUTE);
    tmp.append(" - Attributes: ");
    tmp.append(this.ATTRIBUTES.toString());
    tmp.append(" - Links: ");
    tmp.append(this.LINKS.toString());
    return tmp.toString();
  }

  public class CheckinObject {
    /**
     * Logger for this class
     */
    private Log    LOG  = LogFactory.getLog(CheckinObject.class);

    private String NAME = null;

    private String URL  = null;

    public CheckinObject(String _Name, String _Url) {
      this.NAME = _Name.trim();
      this.URL = _Url.trim();
    }

    public String getName() {
      return this.NAME;
    }

    public String getURL() {
      return this.URL;
    }

    public InputStream getInputStream() {
      try {
        InputStream inputstream = new FileInputStream(this.URL);
        return inputstream;
      }
      catch (FileNotFoundException e) {

        LOG.error("getInputStream()", e);
      }

      return null;

    }
  }

  @Override
  public boolean hasChilds() {

    return this.CHILDS.size() > 0;
  }

}
