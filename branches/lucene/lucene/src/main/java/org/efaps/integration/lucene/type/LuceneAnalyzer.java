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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;

import org.efaps.db.Insert;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;

public class LuceneAnalyzer {
    String OID;

    String ID;

    Analyzer ANALYZER;

    Set STOPWORDS;

    public LuceneAnalyzer(String _OID) {
	setOID(_OID);

	setStopWords();
	setAnalyzer();
    }

    public LuceneAnalyzer() {

    }

    public void setOID(String _OID) {
	OID = _OID;
    }

    public String getOID() {
	return OID;
    }

    public Analyzer getAnalyzer() {
	return ANALYZER;

    }

    public String getID() {
	StringTokenizer i = new StringTokenizer(OID, ".");
	i.nextToken();

	return i.nextToken();

    }

    public void setStopWords() {

	STOPWORDS = LuceneStopWords.getStopWords(getID());
    }

    public boolean hasStopWords() {
	return !STOPWORDS.isEmpty();

    }

    public static String createNew(String _className) {
	try {
	    Insert insert = new Insert("Lucene_Analyzer");
	    insert.add("Analyzer", _className);
	    insert.execute();
	    String LuceneAnalyzerID = insert.getId();
	    insert.close();
	    return LuceneAnalyzerID;
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;

    }

    private void setAnalyzer() {
	SearchQuery query = new SearchQuery();

	String className = null;

	Class AnalyzerClass = null;
	try {

	    query.setObject(getOID());
	    query.addSelect("Analyzer");

	    query.execute();
	    query.next();

	    className = (String) query.get("Analyzer");
	    AnalyzerClass = Class.forName(className);

	    if (hasStopWords()) {

		Constructor constructor = AnalyzerClass
			.getConstructor(new Class[] { Set.class });

		ANALYZER = (Analyzer) constructor
			.newInstance(new Object[] { STOPWORDS });
	    } else {
		ANALYZER = (Analyzer) AnalyzerClass.newInstance();
	    }

	    query.close();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (EFapsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
