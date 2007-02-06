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

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Class witch implements actions with a LuceneDocuemnt in a given LuceneIndex
 * like delete and unlocks a locked LuceneIndex
 * 
 * @author janmoxter
 * 
 */
public class Action {

    /**
         * Deletes a LuceneDocument from an Index
         * 
         * @param _IndexDir
         *                Directory wich contains the Index
         * @param _FieldName
         *                Fieldname is used together with _Fieldvalue to
         *                identifier for the documents witch will be deleted
         * @param _FieldValue
         *                Value for the Field
         * @return the number of documents deleted
         */

    public static int delete(File _IndexDir, String _FieldName,
	    String _FieldValue) {

	try {
	    Directory directory = FSDirectory.getDirectory(_IndexDir, false);
	    IndexReader reader = IndexReader.open(directory);

	    int deleted = reader.deleteDocuments(new Term(_FieldName,
		    _FieldValue));

	    reader.close();
	    directory.close();
	    return deleted;
	} catch (Exception e) {
	    System.out.println(" caught a " + e.getClass()
		    + "\n with message: " + e.getMessage());
	}
	return 0;
    }

    /**
         * Checks if a Lucene Index is locked and unlockes it in Case of
         * 
         * @param indexDir
         *                Directory wich contains the Index
         */
    public static void unLock(File _indexDir) {

	try {

	    Directory directory = FSDirectory.getDirectory(_indexDir, false);

	    IndexReader.open(directory);

	    if (IndexReader.isLocked(directory)) {

		IndexReader.unlock(directory);

	    }

	} catch (IOException e) {

	    e.getMessage();

	}

    }

    /**
         * Optimizes a LucenIndex
         * 
         * @param _indexDir
         *                Directory wich contains the Index
         */
    public static void optimize(File _indexDir) {
	try {
	    IndexWriter writer = new IndexWriter(_indexDir,
		    new StandardAnalyzer(), false);
	    writer.optimize();
	    writer.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
