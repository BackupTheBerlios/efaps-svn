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

package org.efaps.teamwork;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.ParameterInterface;
import org.efaps.admin.event.ReturnInterface;
import org.efaps.admin.event.ParameterInterface.ParameterValues;
import org.efaps.db.Context;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.db.Update;
import org.efaps.util.EFapsException;

public class Member implements EventExecution {
  /**
   * Logger for this class
   */
  private static final Log LOG = LogFactory.getLog(Member.class);

  public ReturnInterface insertNewMember(ParameterInterface _parameter) {
    Iterator iter = ((Map) _parameter.get(ParameterValues.NEW_VALUES))
        .entrySet().iterator();
    Map<String, String> newValues = new HashMap<String, String>();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Attribute attr = (Attribute) entry.getKey();
      String attrName = attr.getName();
      String value = (String) entry.getValue().toString();
      newValues.put(attrName, value);
    }

    SearchQuery query = new SearchQuery();

    try {
      query.setQueryTypes("TeamWork_MemberRights");
      query.addWhereExprEqValue("UserAbstractLink", newValues
          .get("UserAbstractLink"));
      query.addWhereExprEqValue("AbstractLink", newValues.get("AbstractLink"));
      query.addSelect("OID");
      query.executeWithoutAccessCheck();

      if (query.next()) {

      } else {
        Insert insert = new Insert("TeamWork_Member");
        insert.add("AccessSetLink", newValues.get("AccessSetLink"));
        insert.add("AbstractLink", newValues.get("AbstractLink"));
        insert.add("UserAbstractLink", newValues.get("UserAbstractLink"));
        insert.executeWithoutAccessCheck();
      }

    } catch (EFapsException e) {

      LOG.error("insertNewMember(Map<TriggerKeys4Values,Object>)", e);
    } catch (Exception e) {

      LOG.error("insertNewMember(Map<TriggerKeys4Values,Object>)", e);
    }
    return null;

  }

  public ReturnInterface execute(ParameterInterface _parameter) {

    Iterator iter = ((Map) _parameter.get(ParameterValues.NEW_VALUES))
        .entrySet().iterator();
    Map<String, String> newValues = new HashMap<String, String>();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Attribute attr = (Attribute) entry.getKey();
      String attrName = attr.getName();
      String value = (String) entry.getValue().toString();
      newValues.put(attrName, value);
    }
    SearchQuery query = new SearchQuery();
    Update update;
    try {
      query.setQueryTypes("TeamWork_Member");
      query.addWhereExprEqValue("UserAbstractLink", newValues
          .get("UserAbstractLink"));
      query.addWhereExprEqValue("AbstractLink", newValues.get("AbstractLink"));
      query.addSelect("OID");
      query.executeWithoutAccessCheck();

      if (query.next()) {
        update = new Update(query.get("OID").toString());
      } else {
        update = new Insert("TeamWork_Member");

      }
      update.add("AccessSetLink", newValues.get("AccessSetLink"));
      update.add("AbstractLink", newValues.get("AbstractLink"));
      update.add("UserAbstractLink", newValues.get("UserAbstractLink"));
      update.executeWithoutAccessCheck();
    } catch (EFapsException e) {

      LOG.error("execute(Map<TriggerKeys4Values,Object>)", e);
    } catch (Exception e) {

      LOG.error("execute(Map<TriggerKeys4Values,Object>)", e);
    }
    return null;

  }

  public ReturnInterface insertCollectionCreator(ParameterInterface _parameter) {

    Instance instance = (Instance) _parameter.get(ParameterValues.INSTANCE);
    String abstractlink = ((Long) instance.getId()).toString();

    String accessSet = (String) ((Map) _parameter
        .get(ParameterValues.PROPERTIES)).get("AccessSet");

    try {

      SearchQuery query = new SearchQuery();
      query.setQueryTypes("Admin_Access_AccessSet");
      query.addWhereExprEqValue("Name", accessSet);
      query.addSelect("ID");
      query.executeWithoutAccessCheck();

      if (query.next()) {
        String accessID = query.get("ID").toString();
        Insert insert;
        insert = new Insert("TeamWork_Member");
        insert.add("AbstractLink", abstractlink);
        insert.add("AccessSetLink", accessID);
        insert.add("UserAbstractLink", ((Long) Context.getThreadContext()
            .getPerson().getId()).toString());
        insert.executeWithoutAccessCheck();
      } else {

        LOG.error("error in Definition of Propertie 'AccessSet'");
      }

    } catch (EFapsException e) {

      LOG.error("insertCollectionCreator(Map<TriggerKeys4Values,Object>)", e);
    }
    return null;

  }
}
