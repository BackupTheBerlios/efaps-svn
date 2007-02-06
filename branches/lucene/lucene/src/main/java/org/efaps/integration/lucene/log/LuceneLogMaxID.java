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

import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

/**
 * This class represents the ID of an Lucene_Log for a Lucene_Index with the
 * highest Date
 * 
 * @author janmoxter
 * 
 */
public class LuceneLogMaxID {
    private String LOGMAXID;

    public void setLogMaxID(String _LogMaxID) {
	LOGMAXID = _LogMaxID;
    }

    public String getLogMaxID() {
	return LOGMAXID;
    }

    public LuceneLogMaxID(final String _IndexID) {
	final SearchQuery query = new SearchQuery();

	try {
	    query.setQueryTypes("Lucene_LogMaxID");
	    query.addSelect("LuceneLogId");
	    query.addWhereExprEqValue("Index", _IndexID);
	    query.execute();
	    if (query.next()) {
		setLogMaxID((String) query.get("LuceneLogId").toString());
	    } else {
		setLogMaxID("0");
	    }
	} catch (final EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
}
