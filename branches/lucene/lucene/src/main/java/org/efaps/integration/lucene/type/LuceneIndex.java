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

package org.efaps.integration.lucene.type;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.efaps.db.Insert;
import org.efaps.db.SearchQuery;
import org.efaps.integration.lucene.Action;
import org.efaps.integration.lucene.TypeDocFactory;
import org.efaps.integration.lucene.indexer.AbstractIndexer;
import org.efaps.util.EFapsException;

/**
 * This represent one Lucene Index Database
 * 
 * @author janmoxter
 * 
 */
public class LuceneIndex {
    private static String ID = null;

    private static String OID = null;

    protected static File INDEX_DIR;

    private static String NAME;

    private static Map<String, Object> INDEXTYPES = new HashMap<String, Object>();

    private int DELETED;

    /**
         * Defines a new Index into the given Folder
         * 
         * @param _Path
         *                Path to the Folder
         * @param _Name
         * @return ID of the Index
         */
    public static String createNew(String _Path, String _Name) {
	Insert insert;

	try {
	    insert = new Insert("Lucene_Index");
	    insert.add("Path", _Path);
	    insert.add("Name", _Name);
	    insert.execute();
	    String ID = insert.getId();
	    insert.close();
	    return ID;

	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;

    }

    private void setIndex2Type() {
	SearchQuery query = new SearchQuery();
	try {
	    query.setExpand(getOID(), "Lucene_Index2Type\\Index");
	    query.addSelect("OID");
	    query.addSelect("Type");
	    query.execute();

	    while (query.next()) {
		INDEXTYPES.put(query.get("Type").toString(),
			new LuceneIndex2Type(query.get("OID").toString()));

	    }

	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void initialise() {

	setIndex2Type();
    }

    public File getIndexDir() {
	return INDEX_DIR;
    }

    public String getName() {
	return NAME;

    }

    public void setName(String _Name) {
	NAME = _Name;
    }

    public void setIndexDir(File IndexDir) {
	INDEX_DIR = IndexDir;
    }

    public void setIndexDir(String _OID) {
	SearchQuery query = new SearchQuery();
	try {
	    query.setObject(_OID);
	    query.addSelect("Path");
	    query.addSelect("Name");
	    query.execute();
	    query.next();
	    INDEX_DIR = new File((String) query.get("Path"));
	    NAME = (String) query.get("Name");
	    query.close();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void setIndexDir_Name(String _Name) {
	SearchQuery query = new SearchQuery();
	try {
	    query.setQueryTypes("Lucene_Index");
	    query.addSelect("OID");
	    query.addSelect("ID");
	    query.addSelect("Path");
	    query.addSelect("Name");
	    query.addWhereExprEqValue("Name", _Name);

	    query.execute();
	    query.next();
	    // TODO vieleicht ein falscher Name
	    INDEX_DIR = new File((String) query.get("Path"));
	    if (OID == null) {
		this.setOID(query.get("OID").toString());
		this.setID(query.get("ID").toString());
	    }

	    query.close();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static String getOID() {
	return OID;
    }

    public void setOID(String _OID) {
	OID = _OID;
    }

    public String getID() {
	return ID;
    }

    public void setID(String _ID) {
	ID = _ID;

    }

    public Map getTypeList() {
	return INDEXTYPES;
    }

    public LuceneIndex2Type getIndex2Type(String _TypID) {
	return (LuceneIndex2Type) INDEXTYPES.get(_TypID);
    }

    public LuceneIndex2Type getIndex2Type(long _TypID) {
	return (LuceneIndex2Type) INDEXTYPES.get(_TypID);
    }

    public void addObject(String _OID) {
	TypeDocFactory typedocfactory = new TypeDocFactory(_OID);

	LuceneIndex2Type index2type = getIndex2Type(typedocfactory.getTypeID());

	Class<?> cl = index2type.getDocIndexer(typedocfactory.getDocType());

	try {
	    AbstractIndexer indexer = (AbstractIndexer) cl.newInstance();
	    indexer.setIndex(this);
	    indexer.setTypeDocFactory(typedocfactory);
	    indexer.indexUnique();
	    DELETED = indexer.getDeleted();

	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public int getDeleted() {
	return DELETED;

    }

    public void optimize() {
	Action.optimize(getIndexDir());

    }

    public void close() {
	Action.unLock(getIndexDir());
    }
}
