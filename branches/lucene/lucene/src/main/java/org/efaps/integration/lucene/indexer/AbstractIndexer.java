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

package org.efaps.integration.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import org.efaps.integration.lucene.TypeDocFactory;
import org.efaps.integration.lucene.Action;
import org.efaps.integration.lucene.type.LuceneIndex;
import org.efaps.util.EFapsException;

public abstract class AbstractIndexer {
    private static int DELETED;

    private boolean CREATE_INDEX;

    private TypeDocFactory TYPEDOCFACTORY;

    private InputStream STREAM = null;

    private String FILE_NAME;

    private String OID;

    private IndexWriter WRITER;

    protected LuceneIndex INDEX;

    public abstract String getContent();

    public abstract Field getContentField();

   
    
    
    public Document getLuceneDocument() throws EFapsException {

	Document doc;
	doc = getTypeDocFactory().getLuceneDocument(this.getIndex());
	doc.add(this.getContentField());

	return doc;

    }

    public IndexWriter getWriter() {
	return WRITER;
    }

    public void setWriter(Analyzer ana) {
	try {
	    WRITER = new IndexWriter(getIndexDir(), ana, createIndex());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public String getOID() {
	return OID;
    }

    public void setOID(String oid) {
	this.OID = oid;
    }

    public String getFileName() {
	return FILE_NAME;
    }

    public void setFileName(String fileName) {
	this.FILE_NAME = fileName;
    }

    public InputStream getStream() {
	if (!(STREAM == null)) {
	    return STREAM;
	} else {
	    this.setStream(this.getTypeDocFactory().getStream());
	}
	return STREAM;

    }

    public void setStream(InputStream is) {
	this.STREAM = is;
    }

    public File getIndexDir() {
	return INDEX.getIndexDir();
    }

    public boolean indexExists() {
	return IndexReader.indexExists(INDEX.getIndexDir());
    }

    public boolean createIndex() {
	if (indexExists()) {
	    return CREATE_INDEX;
	}
	return true;
    }

    public synchronized void indezies(Analyzer _Analyzer) {

	if (getWriter() == null) {
	    setWriter(_Analyzer);
	}

	try {
	    getWriter().addDocument(getLuceneDocument());
	    getWriter().close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public synchronized void indexUnique(Analyzer _Analyzer) {
	if (this.indexExists()) {
	    DELETED += Action.delete(INDEX.getIndexDir(), "OID",
		    getTypeDocFactory().getOID());
	}
	indezies(_Analyzer);

    }

    public void optimize() {
	try {
	    getWriter().optimize();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void setTypeDocFactory(TypeDocFactory _typedocfactory) {
	TYPEDOCFACTORY = _typedocfactory;

    }

    public TypeDocFactory getTypeDocFactory() {

	return TYPEDOCFACTORY;
    }

    public int getDeleted() {
	return DELETED;
    }

    public void setIndex(LuceneIndex _LuceneIndex) {
	INDEX = _LuceneIndex;
    }

    public LuceneIndex getIndex() {
	return INDEX;
    }

    public void indexUnique() {
	if (this.indexExists()) {
	    DELETED += Action.delete(INDEX.getIndexDir(), "OID",
		    getTypeDocFactory().getOID());
	}

	indezies(getIndex().getIndex2Type(getTypeDocFactory().getTypeID().toString())
		.getLuceneAnalyzer().getAnalyzer());

    }
}
