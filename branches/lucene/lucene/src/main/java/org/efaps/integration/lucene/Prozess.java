package org.efaps.integration.lucene;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.efaps.admin.datamodel.Type;
import org.efaps.db.SearchQuery;
import org.efaps.integration.lucene.log.LuceneLog;
import org.efaps.integration.lucene.type.LuceneAnalyzer;
import org.efaps.integration.lucene.type.LuceneAttribute2Field;
import org.efaps.integration.lucene.type.LuceneField;
import org.efaps.integration.lucene.type.LuceneIndex;
import org.efaps.integration.lucene.type.LuceneIndex2Type;
import org.efaps.integration.lucene.type.LuceneIndexer;
import org.efaps.integration.lucene.type.LuceneStopWords;
import org.efaps.integration.lucene.type.LuceneType2Analyzer;
import org.efaps.integration.lucene.type.LuceneType2Indexer;
import org.efaps.util.EFapsException;

/**
 * Class for executing the whole prozess of indexing, this class can be used to
 * execute the index from other programms
 * 
 * @author jmo
 * 
 */
public class Prozess extends AbstractTransaction implements ProzessInterface {
  /**
   * Logger for this class
   */
  private static final Log LOG = LogFactory.getLog(Prozess.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    String usage = "java org.efaps.integration.lucene.Prozess <action>";

    if (args.length == 0) {
      LOG.error("main(String[]) - Usage: " + usage, null);
      System.exit(1);
    }
    if (args[0].equals("new")) {
      (new Prozess()).createNewIndex();
    } else if (args[0].equals("run")) {
      (new Prozess()).executeAll();
    }

  }

  public void createNewIndex() {
    initialize();
    try {

      startTransaction();
      CreateLuceneIndex
          .create("/Users/janmoxter/Documents/workspace/eFaps/lucene/index.xml");
      commitTransaction();
    } catch (EFapsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void initialize() {

    if (!initDatabase()) {
      getLog().error("Database Connection could not be initialised!");
    } else {

      try {
        login("Administrator", "");
        reloadCache();

      } catch (EFapsException e) {
        // TODO Auto-generated catch block
        LOG.error("initialize()", e);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        LOG.error("initialize()", e);
      }

    }
  }

  private List getAllIndexOID() {
    List<String> oidlist = new ArrayList<String>();
    SearchQuery query = new SearchQuery();
    try {

      startTransaction();
      query.setQueryTypes("Lucene_Index");
      query.addSelect("OID");
      query.execute();

      while (query.next()) {
        oidlist.add(query.get("OID").toString());

      }

      abortTransaction();
      return oidlist;
    } catch (EFapsException e) {
      // TODO Auto-generated catch block
      LOG.error("getAllIndexOID()", e);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      LOG.error("getAllIndexOID()", e);
    }
    return null;
  }

  public void executeAll() {

    initialize();
    for (Iterator iter = getAllIndexOID().iterator(); iter.hasNext();) {
      execute((String) iter.next());

    }
  }

  public void execute(String _OID) {
    LuceneIndex index = new LuceneIndex();
    LuceneLog log = new LuceneLog();
    try {
      startTransaction();
      index.setIndex(_OID);
      log.setIndexID(index.getID());
      log.initialise();

      commitTransaction();

      startTransaction();

      int inD = 0;

      index.initialise();

      SearchQuery query = new SearchQuery();
      query.setQueryTypes("TeamWork_SourceVersion");
      query.addSelect("OID");
      query.addSelect("Modified");
      query.execute();
      while (query.next()) {
        Date compareDate = (Date) query.get("Modified");
        if (log.getLastStart().before(compareDate)) {

          index.addObject(query.get("OID").toString());

          inD++;
        }
      }
      query.close();
      if (inD > 0) {
        index.optimize();
        log.end(index.getDeleted(), inD);
      } else {

        log.end(0, 0);
      }

    } catch (EFapsException e) {
      log.terminate();
      LOG.error("execute(String)", e);
    } catch (Exception e) {
      log.terminate();
      LOG.error("execute(String)", e);
    }
    index.close();

    try {
      commitTransaction();
    } catch (EFapsException e1) {
      // TODO Auto-generated catch block
      LOG.error("execute(String)", e1);
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      LOG.error("execute(String)", e1);
    }

  }

  public void reset(String _OID) {
    LuceneIndex index = new LuceneIndex();
    LuceneLog log = new LuceneLog();

    try {
      startTransaction();

      index.setIndex(_OID);
      index.setNewIndexFiles(true);
      log.setIndexID(index.getID());
      log.initialise();

      commitTransaction();

      startTransaction();

      int inD = 0;

      index.initialise();

      SearchQuery query = new SearchQuery();
      query.setQueryTypes("TeamWork_SourceVersion");
      query.addSelect("OID");
      query.addSelect("Modified");
      query.execute();
      while (query.next()) {

        index.addObject(query.get("OID").toString());

        inD++;
      }

      query.close();
      if (inD > 0) {
        index.optimize();
        log.end("Reset", index.getDeleted(), inD);
      } else {

        log.end("Reset", 0, 0);
      }
    } catch (EFapsException e) {
      // TODO Auto-generated catch block
      LOG.error("reset(String)", e);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      LOG.error("reset(String)", e);
    }

    index.close();
    try {
      commitTransaction();
    } catch (EFapsException e1) {
      // TODO Auto-generated catch block
      LOG.error("reset(String)", e1);
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      LOG.error("reset(String)", e1);
    }
  }

  public void resetAll() {
    initialize();
    for (Iterator iter = getAllIndexOID().iterator(); iter.hasNext();) {
      reset((String) iter.next());

    }

  }
}
