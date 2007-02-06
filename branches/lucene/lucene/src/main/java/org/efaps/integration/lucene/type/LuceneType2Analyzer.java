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

import org.efaps.db.Insert;
import org.efaps.util.EFapsException;

public class LuceneType2Analyzer {

    
    public static String  createNew(String _AnalyzerID,String _IndexTyp){
	Insert insert;
	try {
	    insert = new Insert("Lucene_Type2Analyzer");
	    insert.add("Analyzer",_AnalyzerID);
	    insert.add("IndexType", _IndexTyp);
	    insert.execute();
	    String Type2AnalyzerID = insert.getId();
	    insert.close();
	    return Type2AnalyzerID;
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
