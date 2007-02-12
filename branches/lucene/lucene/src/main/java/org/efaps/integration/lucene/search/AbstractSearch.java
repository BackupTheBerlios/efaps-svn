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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.efaps.db.SearchQuery;
import org.efaps.integration.lucene.AbstractTransaction;
import org.efaps.integration.lucene.type.LuceneIndex;
import org.efaps.integration.lucene.type.LuceneIndex2Type;
import org.efaps.util.EFapsException;

public abstract class AbstractSearch extends AbstractTransaction {
  /**
   * Logger for this class
   */
  private static final Log     LOG      = LogFactory
                                            .getLog(AbstractSearch.class);

  static Query                 query;

  private static Analyzer      ANALYZER = null;

  private static IndexSearcher SEARCHER = null;

  private static LuceneIndex   INDEX;

  public AbstractSearch(String _IndexID) {

    initialise(_IndexID);
  }

  public LuceneIndex getIndex() {
    return INDEX;
  }

  public void setIndex(String _OID) {
    INDEX = new LuceneIndex(_OID);
    INDEX.initialise();
  }

  public IndexSearcher getSearcher() {
    if (SEARCHER == null) {
      setSearcher(getIndex().getIndexPath());
    }
    return SEARCHER;
  }

  public void setSearcher(String _Path) {
    try {
      SEARCHER = new IndexSearcher(_Path);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void initialise(String _IndexID) {

    if (!initDatabase()) {
      LOG.error("Database Connection could not be initialised!");
    } else {

      try {
        login("Administrator", "");
        reloadCache();
        startTransaction();
        SearchQuery query = new SearchQuery();
        query.setQueryTypes("Lucene_Index");
        query.addSelect("OID");
        query.execute();
        if (query.next()) {
          setIndex(query.get("OID").toString());

        } else {
          LOG.error("can't find the index");
        }
        abortTransaction();
      } catch (IOException e) {
        LOG.error("initialize()", e);
      } catch (EFapsException e) {
        LOG.error("initialize()", e);
      } catch (Exception e) {
        LOG.error("initialize()", e);
      }

    }

  }

  public Analyzer getAnalyzer() {
    if (ANALYZER == null) {
      Map types = getIndex().getTypeList();

      for (Object elem : types.values()) {
        LuceneIndex2Type type = (LuceneIndex2Type) elem;
        ANALYZER = type.getLuceneAnalyzer().getAnalyzer();
      }
    }
    return ANALYZER;

  }

  public List find(String queryString) {
    List<String> result = new ArrayList();

    QueryParser test = new QueryParser("contents", getAnalyzer());
    try {
      query = test.parse(queryString);
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
        LOG.debug(doc.get("OID"));
      }
      return result;
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      LOG.error("find(String)", e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      LOG.error("find(String)", e);
    }

    return null;

  }

}
