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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.efaps.db.Insert;
import org.efaps.util.EFapsException;

public class RootObject extends AbstractObject  {
  /**
   * Logger for this class
   */
  private static final Log       LOG        = LogFactory
                                                .getLog(RootObject.class);

  final List<AbstractObject>     CHILDS     = new ArrayList<AbstractObject>();

  static String                  DATEFORMAT = null;

  final static Map<String, OrderObject> ORDER      = new HashMap<String, OrderObject>();

  
  
  
  public void setDateFormat(String _DateFormat) {
    DATEFORMAT = _DateFormat;
  }

  public void addOrder(OrderObject _order) {

    ORDER.put(_order.getType(), _order);
  }

  public static OrderObject getOrder(final String _type) {
    return ORDER.get(_type);
  }

  @Override
  public Map<String, Object> getAttributes() {

    return null;
  }

  @Override
  public String getType() {

    return null;
  }

  @Override
  public void insertObject() {

  }

  @Override
  public void setID(String _ID) {

  }

  public void addChild(AbstractObject _Object) {
    CHILDS.add(_Object);
  }

  public void insertDB() {
    for (AbstractObject object : this.CHILDS) {
      try {
        Insert insert = new Insert(object.getType());

        for (Entry element : object.getAttributes().entrySet()) {
          if (element.getValue() instanceof Timestamp) {
            insert.add(element.getKey().toString(), (Timestamp) element
                .getValue());

          } else {
            insert.add(element.getKey().toString(), element.getValue()
                .toString());
          }
        }
        for (ForeignObject link : object.getLinks()) {
          insert.add(link.getAttribute(), link.getID());
        }
        insert.executeWithoutAccessCheck();
        String ID = insert.getId();
        insert.close();

        object.setID(ID);
        object.insertObject();

      }
      catch (EFapsException e) {

        LOG.error("insertDB()", e);
      }
      catch (Exception e) {

        LOG.error("insertDB()", e);
      }

    }
  }

  @Override
  public String getParrentAttribute() {

    return null;
  }

  @Override
  public Set<ForeignObject> getLinks() {

    return null;
  }

  @Override
  public boolean isCheckinObject() {

    return false;
  }

  @Override
  public void checkObjectin() {

  }

  @Override
  public Set<String> getUniqueAttributes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getAttribute(String _attribute) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasChilds() {
    // TODO Auto-generated method stub
    return false;
  }

}
