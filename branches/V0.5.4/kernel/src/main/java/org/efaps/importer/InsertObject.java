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

/**
 * This class presents the main Object for the import of Data into eFaps and the
 * connected Database.<br>
 * <br>
 * Every InsertObject can be a child to an other InsertObject, so that a
 * parent-child-hirachie can be constructed. The first InsertObject must be the
 * child to a {@code org.efaps.importer.RootObject}.
 * 
 * @author jmo
 * 
 */
public class InsertObject extends AbstractObject {
  /**
   * Logger for this class
   */
  private static final Log                  LOG              = LogFactory
                                                                 .getLog(InsertObject.class);

  /**
   * contains the Name of the Type of the current InsertObeject
   */
  private String                            type             = null;

  /**
   * Map containing all Attributes of this InsertObject
   */
  private Map<String, Object>               attributes       = new HashMap<String, Object>();

  /**
   * contains the Name of the Attribute, wich presents the
   * parent-child-relation, for this InsertObject
   */
  private String                            parentAttribute  = null;

  /**
   * contains all Childs of this Insertobject
   */
  private Map<String, List<AbstractObject>> childs           = new HashMap<String, List<AbstractObject>>();

  /**
   * contains the ID for this InsertObject
   */
  private String                            id               = null;

  /**
   * contains all {@link ForeignObjects} of this InsertObject
   */
  private Set<ForeignObject>                links            = new HashSet<ForeignObject>();

  /**
   * contains all Attributes wich are defined as unique for this InsertObejct
   */
  private Set<String>                       uniqueAttributes = new HashSet<String>();

  /**
   * contains the CheckinObject, if the InsertObject contains one
   */
  private CheckinObject                     ceckinobject     = null;

  public InsertObject() {

  }

  /**
   * Constructor used by {@link InsertObjectFactory}
   * 
   * @param _type
   *          Type of the InsertObject
   */
  public InsertObject(final String _type) {
    setType(_type);
  }

  /**
   * set the Type of the InsertObject
   * 
   * @param _type
   *          Type of the InsertObject
   */
  public void setType(final String _type) {
    this.type = _type;
  }

  /**
   * adds an Attribute to the <code>attributes</code> of this InsertObject and
   * in the case that the Parameter "_unique" equals "true" the Attribute will
   * also be added to the <code>uniqueAttributes.</code>
   * 
   * @param _Name
   *          Name of the Attribute
   * @param _Value
   *          Value of the Attribute
   * @param _unique
   *          if _unique equals "true" the Attribute will be added to the
   *          uniqueAttributes of this InsertObject
   */
  public void addAttribute(final String _Name, final String _Value,
                           final String _unique) {
    this.attributes.put(_Name, _Value.trim());

    if (_unique != null && _unique.equals("true")) {
      this.uniqueAttributes.add(_Name);
    }
  }

  /**
   * sets the <code>parentAttribute</code> of this InsertObject. If the
   * Parameter "_unique" equals "true" the Attribute will also be added to the
   * <code>uniqueAttributes.</code>
   * 
   * @param _ParentAttribute
   *          Name of the Attribute
   * @param _unique
   *          if _unique equals "true" the Attribute will be added to the
   *          uniqueAttributes of this InsertObject
   */
  public void setParentAttribute(final String _ParentAttribute,
                                 final String _unique) {
    this.parentAttribute = _ParentAttribute;

    if (_unique != null && _unique.equals("true")) {
      this.uniqueAttributes.add(_ParentAttribute);
    }

  }

  /**
   * adds a Child to this InsertObject seperated for the diferend Types. If the
   * Type of the InsertObject is also an OrderObject, the Childs will be sorted.
   * 
   * @param _object
   *          Child to be added
   */
  public void addChild(AbstractObject _object) {
    List<AbstractObject> list = this.childs.get(_object.getType());
    if (list == null) {

      list = new ArrayList<AbstractObject>();
      this.childs.put(_object.getType(), list);
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

  @Override
  public boolean hasChilds() {

    return this.childs.size() > 0;
  }

  /**
   * adds a ForeignObject to this InsertObject
   * 
   * @param _Object
   *          ForeignObject to be added
   */
  public void addLink(ForeignObject _Object) {
    this.links.add(_Object);

  }

  /**
   * adds a <code>uniqueAttributes</code>
   * 
   * @param _unique
   *          the Attribute will be added if the Parameter equals "true"
   * @param _Name
   *          Name of the Attribute
   */
  public void addUniqueAttribute(String _unique, String _Name) {
    if (_unique != null && _unique.equals("true")) {
      this.uniqueAttributes.add(_Name);
    }
  }

  @Override
  public void setID(String _id) {
    id = _id;
  }

  @Override
  public void insertObject() {

    String ID = null;
    boolean noInsert = false;

    for (List<AbstractObject> list : this.childs.values()) {
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
                query.addWhereExprEqValue(element, this.id);

              }
              for (ForeignObject link : object.getLinks()) {
                if (link.getLinkAttribute().equals(element)) {
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

  private String UpdateOrInsert(final AbstractObject _Object, final Update _UpIn) {
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
        _UpIn.add(_Object.getParrentAttribute(), this.id);
      }
      for (ForeignObject link : _Object.getLinks()) {
        _UpIn.add(link.getLinkAttribute(), link.getID());
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
    // TODO warum war da ein this.id?
    return null;

  }

  /**
   * get the ID of the InsertObject
   * 
   * @return the ID of the InsertObject
   */
  public String getID() {
    return this.id;
  }

  @Override
  public String getType() {
    return this.type;

  }

  @Override
  public Map<String, Object> getAttributes() {
    for (Entry element : this.attributes.entrySet()) {

      Attribute attribute = Type.get(this.type).getAttribute(
          element.getKey().toString());

      if (attribute.getAttributeType().getClassRepr().getName().equals(
          "org.efaps.admin.datamodel.attributetype.DateTimeType")) {

        Date date = new SimpleDateFormat(RootObject.DATEFORMAT).parse(element
            .getValue().toString(), new ParsePosition(0));

        this.attributes.put((String) element.getKey(), new Timestamp(date
            .getTime()));
      }
    }
    return this.attributes;
  }

  @Override
  public Object getAttribute(final String _attribute) {

    return (this.attributes.get(_attribute));
  }

  @Override
  public String getParrentAttribute() {

    return this.parentAttribute;
  }

  @Override
  public Set<ForeignObject> getLinks() {

    return this.links;
  }

  @Override
  public Set<String> getUniqueAttributes() {
    return this.uniqueAttributes;
  }

  /**
   * method to Create a new {@link CheckinObject} and store it
   * 
   * @param _Name
   *          Name of the CheckinObject
   * @param _URL
   *          URL to the File of the CheckinObject
   */
  public void setCheckinObject(String _Name, String _URL) {
    this.ceckinobject = new CheckinObject(_Name, _URL);

  }

  @Override
  public boolean isCheckinObject() {
    if (this.ceckinobject != null) {
      return true;
    }
    return false;
  }

  @Override
  public void checkObjectin() {

    Checkin checkin = new Checkin(new Instance(this.type, this.id));

    try {
      checkin.executeWithoutAccessCheck(this.ceckinobject.getName(),
          this.ceckinobject.getInputStream(), -1);
    }
    catch (EFapsException e) {

      LOG.error("checkObjectin() " + this.toString(), e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {

    StringBuilder tmp = new StringBuilder();
    tmp.append("Type: ");
    tmp.append(this.type);
    tmp.append(" - ParentAttribute: ");
    tmp.append(this.parentAttribute);
    tmp.append(" - Attributes: ");
    tmp.append(this.attributes.toString());
    tmp.append(" - Links: ");
    tmp.append(this.links.toString());
    return tmp.toString();
  }

  /**
   * Class to store the Information, needed to Check in a InsertObject
   * 
   * @author jmo
   * 
   */
  public class CheckinObject {
    /**
     * Logger for this class
     */
    private Log    LOG  = LogFactory.getLog(CheckinObject.class);

    /**
     * contains the Filename of the CheckinObject
     */
    private String name = null;

    /**
     * contains the URL to the File
     */
    private String url  = null;

    /**
     * constructor setting the Filename and the URL of the CheckinObject
     * 
     * @param _name
     *          Filename of the CheckinObject
     * @param _url
     *          URL to the File
     */
    public CheckinObject(String _name, String _url) {
      this.name = _name.trim();
      this.url = _url.trim();
    }

    /**
     * get the Name of the CheckinObject
     * 
     * @return Filename of the CheckinObject
     */
    public String getName() {
      return this.name;
    }

    /**
     * get the URL of the CheckinObject
     * 
     * @return URL to the File
     */
    public String getURL() {
      return this.url;
    }

    /**
     * get an Inputstream of the File to check in
     * 
     * @return Inputstream of the File
     */
    public InputStream getInputStream() {
      try {
        InputStream inputstream = new FileInputStream(this.url);
        return inputstream;
      }
      catch (FileNotFoundException e) {

        LOG.error("getInputStream()", e);
      }

      return null;

    }
  }

}
