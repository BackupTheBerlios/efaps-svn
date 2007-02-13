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

package org.efaps.integration.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Class for a simple Search with only one Term, for the use of "AND" other
 * classes must be used
 * 
 * @author jmo
 * 
 */
public class OneTermContentSearch extends AbstractSearch {

  public OneTermContentSearch(String _IndexID) {
    super(_IndexID);
  }

  public List find(String queryString) {
    List<String> result = new ArrayList<String>();

    Query query = new TermQuery(new Term("contents", queryString));

    try {

      Hits hits = getSearcher().search(query);
      int startindex = 0;
      int thispage = 0;
      int maxpage = 50;
      if ((startindex + maxpage) > hits.length()) {
        thispage = hits.length() - startindex;
      }

      for (int i = startindex; i < (thispage + startindex); i++) {
        Document doc = hits.doc(i);
        result.add(doc.get("OID"));
        getLog().debug(doc.get("OID"));
      }
      return result;
    } catch (IOException e) {

      getLog().error("find(String)", e);
    }

    return null;

  }

}
