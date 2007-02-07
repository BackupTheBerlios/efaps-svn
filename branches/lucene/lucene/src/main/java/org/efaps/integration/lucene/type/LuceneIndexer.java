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

import org.efaps.admin.datamodel.Type;
import org.efaps.db.Insert;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

public class LuceneIndexer {
    private String ID;

    private String OID;

    private Class<?> INDEXER;

    private String FILETYP;

    public LuceneIndexer(String _ID) {
	setID(_ID);
	Long i = Type.get("Lucene_Indexer").getId();
	setOID(i.toString() + "." + getID());
	initialise();

    }

    private void initialise() {

	SearchQuery query = new SearchQuery();

	try {
	    query.setObject(getOID());
	    query.addSelect("FileTyp");
	    query.addSelect("Indexer");
	    query.execute();
	    if (query.next()) {
		INDEXER = Class.forName(query.get("Indexer").toString());
		FILETYP = query.get("FileTyp").toString();
	    }

	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public String getOID() {
	return OID;
    }

    public void setOID(String _OID) {
	OID = _OID;
    }

    public void setID(String _ID) {
	ID = _ID;
    }

    public String getID() {
	return ID;
    }
    
    public String getFileTyp(){
	return FILETYP;
    }
    
    public Class<?> getIndexer(){
	return INDEXER;
    }
    
    public static String createNew(String _FileTyp, String _Indexer){
	
	Insert insert;

	try {
	    insert = new Insert("Lucene_Indexer");
	    insert.add("FileTyp", _FileTyp);
	    insert.add("Indexer", _Indexer);
	   
	    insert.execute();
	    String IndexID = insert.getId();
	    insert.close();
	    return IndexID;

	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
	
	
    }
    
}
