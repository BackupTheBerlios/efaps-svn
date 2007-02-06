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

package org.efaps.integration.lucene.log;

import java.util.Date;

import org.efaps.db.Insert;

import org.efaps.db.SearchQuery;
import org.efaps.db.Update;
import org.efaps.util.EFapsException;

/**
 * 
 * @author janmoxter
 *
 */
public class LuceneLog {

    private Date LASTSTART;

    private Date LASTEND;

    private String OID;

    private String INDEXID;

    public void start() {

	Insert insert;
	try {

	    insert = new Insert("Lucene_Log");
	    insert.add("log", "Indiziervorgang gestartet");
	    insert.add("Index", this.getIndexID());
	    insert.execute();

	    OID = insert.getInstance().getOid();
	    insert.close();

	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public LuceneLog(String _IndexID) {
	setIndexID(_IndexID);
	initialise();
    }

    public LuceneLog() {

    }

    public void initialise() {
	this.getRuntime();
	this.start();
    }

    public void setIndexID(String _IndexID) {
	INDEXID = _IndexID;
    }

    public String getIndexID() {
	return INDEXID;
    }

    public void end(int _deleted, int _indexed) {

	try {
	    Update update = new Update(OID);
	    String log = "updated: " + _deleted + ", total: " + _indexed;
	    update.add("log", log);

	    update.execute();
	    update.close();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void getRuntime() {

	SearchQuery query = new SearchQuery();
	try {

	    query.setQueryTypes("Lucene_Log");
	    query.addSelect("Created");
	    query.addSelect("Modified");

	    query.addWhereExprEqValue("ID", new LuceneLogMaxID(this
		    .getIndexID()).getLogMaxID());
	    query.execute();
	    if (query.next()) {
		LASTSTART = (Date) query.get("Created");
		LASTEND = (Date) query.get("Modified");

	    } else {
		LASTSTART = new Date(0);
		LASTEND = new Date(System.currentTimeMillis());
	    }
	    query.close();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public Date laststart() {
	if (LASTSTART == null) {
	    this.getRuntime();
	}
	return LASTSTART;

    }

    public Date lastend() {
	if (LASTSTART == null) {
	    this.getRuntime();
	}
	return LASTEND;

    }

    public void terminate() {
	try {
	    Update update = new Update(OID);
	   
	    update.add("log", "failure");

	    update.execute();
	    update.close();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
