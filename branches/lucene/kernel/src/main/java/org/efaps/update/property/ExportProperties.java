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

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

public class ExportProperties  {

  private static Properties              PROP;

  private static TreeMap<String, String> TM = new TreeMap<String, String>();

  private static TreeMap<String, String> TM2 ;

  public ExportProperties() {
    SearchQuery query = new SearchQuery();
    PROP = new Properties();

    try {
      query.setQueryTypes("Admin_Properties");
      query.addSelect("Key");
      query.addSelect("Default");

      query.execute();
      while (query.next()) {
        TM.put(query.get("Key").toString(), query.get("Default").toString());

      }

      MyStringComparator myStringComparator = new MyStringComparator();
      TM2 = new TreeMap<String, String>(myStringComparator);
      TM2.putAll( TM );
      
      Iterator it = TM2.entrySet().iterator();
      while( it.hasNext() )
      {
        Map.Entry me = (Map.Entry)it.next();
        System.out.println(me.getKey().toString()) ;
        PROP.setProperty(me.getKey().toString(), me.getValue().toString());
        
      }
      
      
      
      
      try {
        PROP.storeToXML((System.out), "gehtdas");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } catch (EFapsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  
  class MyStringComparator implements Comparator
  {
    public int compare( Object o1, Object o2 )
    {
      int i = prepairForCompare( o1 ).compareTo( prepairForCompare( o2 ) );
      return ( 0 != i ) ? i : ((String)o2).compareTo( (String)o1 );
    }

    private String prepairForCompare( Object o )
    {
      return ((String)o).toLowerCase().replace( 'Š', 'a' )
                                      .replace( 'š', 'o' )
                                      .replace( 'Ÿ', 'u' )
                                      .replace( '§', 's' );
    }
  }

}
