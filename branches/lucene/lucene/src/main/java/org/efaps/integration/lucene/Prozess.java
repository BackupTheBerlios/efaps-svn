/*
 * Copyright 2007 The eFaps Team
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
 * Author:          jmo
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.integration.lucene;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * das ist momentan nur die Testclasse
 * 
 * @author janmoxter
 * 
 */
public class Prozess extends AbstractTransaction {

    /**
         * @param args
         */
    public static void main(String[] args) {

	(new Prozess()).run();

//	 (new Prozess()).createNewIndex();
    }

    public void createNewIndex() {
	if (!initDatabase()) {
	    getLog().error("Database Connection could not be initialised!");
	} else {

	    try {
		login("Administrator", "");

		reloadCache();
		startTransaction();

		String LuceneAnalyzerID = LuceneAnalyzer
			.createNew("org.apache.lucene.analysis.standard.StandardAnalyzer");

		LuceneStopWords.insertNew(LuceneAnalyzerID, "by");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "the");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "and");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "but");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "at");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "as");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "for");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "into");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "be");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "in");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "a");
		LuceneStopWords.insertNew(LuceneAnalyzerID, "are");

		List<String> LIndexer = new ArrayList<String>();
		LIndexer.add(LuceneIndexer.createNew("html",
			"org.efaps.integration.lucene.indexer.HtmIndexer"));
		LIndexer.add(LuceneIndexer.createNew("odt",
			"org.efaps.integration.lucene.indexer.OOIndexer"));
		LIndexer.add(LuceneIndexer.createNew("xls",
			"org.efaps.integration.lucene.indexer.ExcelIndexer"));
		LIndexer.add(LuceneIndexer.createNew("xml",
			"org.efaps.integration.lucene.indexer.XmlIndexer"));
		LIndexer.add(LuceneIndexer.createNew("txt",
			"org.efaps.integration.lucene.indexer.TxtIndexer"));
		LIndexer.add(LuceneIndexer.createNew("doc",
			"org.efaps.integration.lucene.indexer.MSWordIndexer"));
		LIndexer.add(LuceneIndexer.createNew("rtf",
			"org.efaps.integration.lucene.indexer.RtfIndexer"));
		LIndexer.add(LuceneIndexer.createNew("pdf",
			"org.efaps.integration.lucene.indexer.PDFIndexer"));
		LIndexer.add(LuceneIndexer.createNew("ppt",
			"org.efaps.integration.lucene.indexer.PptIndexer"));
		String IndexID = LuceneIndex
			.createNew(
				"/Users/janmoxter/Documents/workspace/eFaps/lucene/target/lucene/indexMain/",
				"test");
		Type testType = Type.get("TeamWork_SourceVersion");
		Long TypeID = testType.getId();

		String IndexTypesID = LuceneIndex2Type.createNew(IndexID,
			TypeID.toString());

		List<String> LucenFieldID = new ArrayList<String>();

		LucenFieldID.add(LuceneField.createNew(
			"org.efaps.admin.user.Person", "getName", "YES",
			"UN_TOKENIZED"));
		LucenFieldID.add(LuceneField.createNew(
			"org.efaps.admin.user.Person", "getId", "YES",
			"UN_TOKENIZED"));
		LucenFieldID.add(LuceneField.createNew("", "", "YES",
			"UN_TOKENIZED"));

		Long AttributeID;

		AttributeID = testType.getAttribute("Creator").getId();
		LuceneAttribute2Field.createNew(IndexTypesID, AttributeID
			.toString(), LucenFieldID.get(0));

		AttributeID = testType.getAttribute("Created").getId();
		LuceneAttribute2Field.createNew(IndexTypesID, AttributeID
			.toString(), LucenFieldID.get(2));

		AttributeID = testType.getAttribute("Modifier").getId();
		LuceneAttribute2Field.createNew(IndexTypesID, AttributeID
			.toString(), LucenFieldID.get(1));

		AttributeID = testType.getAttribute("Modified").getId();
		LuceneAttribute2Field.createNew(IndexTypesID, AttributeID
			.toString(), LucenFieldID.get(2));

		AttributeID = testType.getAttribute("Name").getId();
		LuceneAttribute2Field.createNew(IndexTypesID, AttributeID
			.toString(), LucenFieldID.get(2));

		LuceneType2Analyzer.createNew(LuceneAnalyzerID, IndexTypesID);

		for (String object : LIndexer) {
		    LuceneType2Indexer.createNew(IndexTypesID, object);

		}

		commitTransaction();
	    } catch (EFapsException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    public void run() {
	LuceneIndex index = null;
	LuceneLog log = new LuceneLog();
	if (!initDatabase()) {
	    getLog().error("Database Connection could not be initialised!");
	} else {

	    try {
		login("Administrator", "");

		reloadCache();
		startTransaction();
		index = new LuceneIndex();
		index.setIndexDir_Name("test");

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
		    // if (x.laststart().before(compareDate)) {

		    index.addObject(query.get("OID").toString());

		    inD++;
		    // }
		}

		query.close();
		if (inD > 0) {
		    index.optimize();
		    log.end(index.getDeleted(), inD);
		} else {

		    log.end(0, 0);
		}

		System.err.println("juhu hat geklappt");

	    } catch (EFapsException e) {
		log.terminate();
		e.printStackTrace();
	    } catch (Exception e) {
		log.terminate();
		e.printStackTrace();
	    } finally {
		index.close();

		try {
		    commitTransaction();
		} catch (EFapsException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	}
    }
}
