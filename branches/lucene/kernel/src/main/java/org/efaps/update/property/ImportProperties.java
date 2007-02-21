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

package org.efaps.update.property;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.db.Update;
import org.efaps.util.EFapsException;

/**
 * Class for importing Properties from a propertie into the Database for use as
 * eFaps-Admin_Properties
 * 
 * @author jmo
 * 
 */
public class ImportProperties {
  /**
   * Logger for this class
   */
  private static final Log LOG         = LogFactory
                                           .getLog(ImportProperties.class);

  private static String    LANGUAGE;

  private static String    LANGUAGE_ID = null;

  public static void setLanguage(String _Language) {
    LANGUAGE = _Language;
  }

  public static String getLanguage() {
    return LANGUAGE;
  }

  private static String getLanguageId() {
    if (LANGUAGE_ID == null) {

      SearchQuery query = new SearchQuery();
      try {
        query.setQueryTypes("Admin_Language");
        query.addSelect("ID");
        query.addWhereExprEqValue("Language", getLanguage());
        query.execute();
        if (query.next()) {
          LANGUAGE_ID = query.get("ID").toString();
        }
        query.close();
        return LANGUAGE_ID;
      } catch (EFapsException e) {

        LOG.error("getLanguageId()", e);
      }
    }
    return LANGUAGE_ID;

  }

  public static void importFromProperties(String _filename) {

    try {

      FileInputStream propInFile = new FileInputStream(_filename);
      Properties p2 = new Properties();
      p2.load(propInFile);
      Iterator<Entry<Object, Object>> x = p2.entrySet().iterator();

      while (x.hasNext()) {
        Entry<Object, Object> element = x.next();
        String OID = getExistingKey(element.getKey().toString());
        if (OID == null) {
          insertNewProp(element.getKey().toString(), element.getValue()
              .toString());
        } else {
          updateDefault(OID, element.getValue().toString());
        }
      }

    } catch (FileNotFoundException e) {
      LOG.error("ImportFromProperties() - Can’t find " + _filename, e);
    } catch (IOException e) {
      LOG.error("ImportFromProperties() - I/O failed.", e);
    }
  }

  public static void importFromProperties(String _filename, String _language) {
    setLanguage(_language);
    String propOID;
    String propID;
    String localOID;
    try {

      FileInputStream propInFile = new FileInputStream(_filename);
      Properties p2 = new Properties();
      p2.load(propInFile);
      Iterator<Entry<Object, Object>> x = p2.entrySet().iterator();

      while (x.hasNext()) {
        Entry<Object, Object> element = x.next();
        propOID = getExistingKey(element.getKey().toString());

        if (propOID == null) {
          propID = insertNewProp(element.getKey().toString(), element
              .getValue().toString());
        } else {
          propID = getId(propOID);
        }

        localOID = getExistingLocale(propID);
        if (localOID == null) {
          insertNewLocal(propID, element.getValue().toString());
        } else {

          updateLocale(localOID, element.getValue().toString());
        }
      }

    } catch (FileNotFoundException e) {
      LOG.error("ImportFromProperties() - Can’t find " + _filename, e);
    } catch (IOException e) {
      LOG.error("ImportFromProperties() - I/O failed.", e);
    }
  }

  private static String getExistingLocale(String _PropertyID) {
    SearchQuery query = new SearchQuery();
    String OID = null;
    try {
      query.setQueryTypes("Admin_Properties_Local");
      query.addSelect("OID");
      query.addWhereExprEqValue("PropertyID", _PropertyID);
      query.addWhereExprEqValue("LanguageID", getLanguageId());
      query.execute();
      if (query.next()) {
        OID = (String) query.get("OID");
      }
      query.close();

      return OID;
    } catch (EFapsException e) {
      // TODO Auto-generated catch block
      LOG.error("getExistingLocale(String)", e);
    }

    return null;
  }

  private static void insertNewLocal(String PropertyID, String _value) {
    try {
      Insert insert = new Insert("Admin_Properties_Local");
      insert.add("Value", _value);
      insert.add("PropertyID", PropertyID);
      insert.add("LanguageID", getLanguageId());
      insert.execute();
      insert.close();

    } catch (EFapsException e) {

      LOG.error("insertNewLocal(String)", e);
    } catch (Exception e) {

      LOG.error("insertNewLocal(String)", e);
    }

  }

  private static void updateLocale(String _OID, String _value) {
    try {
      Update update = new Update(_OID);
      update.add("Value", _value);
      update.execute();

    } catch (EFapsException e) {

      LOG.error("updateLocale(String, String)", e);
    } catch (Exception e) {

      LOG.error("updateLocale(String, String)", e);
    }

  }

  private static String getExistingKey(String _key) {
    String OID = null;
    SearchQuery query = new SearchQuery();
    try {
      query.setQueryTypes("Admin_Properties");
      query.addSelect("OID");
      query.addWhereExprEqValue("Key", _key);
      query.execute();
      if (query.next()) {
        OID = (String) query.get("OID");

      }

      query.close();
      return OID;
    } catch (EFapsException e) {
      LOG.error("getExisting()", e);
    }

    return null;
  }

  private static void updateDefault(String _OID, String _value) {
    try {
      Update update = new Update(_OID);
      update.add("Default", _value);
      update.execute();

    } catch (EFapsException e) {

      LOG.error("updateDefault(String, String)", e);
    } catch (Exception e) {

      LOG.error("updateDefault(String, String)", e);
    }

  }

  private static String insertNewProp(String _key, String _value) {
    try {
      Insert insert = new Insert("Admin_Properties");
      insert.add("Key", _key);
      insert.add("Default", _value);
      insert.execute();
      String Id = insert.getId();
      insert.close();
      return Id;
    } catch (EFapsException e) {

      LOG.error("InsertNew(String, String)", e);
    } catch (Exception e) {

      LOG.error("InsertNew(String, String)", e);
    }

    return null;

  }

  private static String getId(String OID) {
    Long id = new Instance(OID).getId();
    return id.toString();

  }

}
