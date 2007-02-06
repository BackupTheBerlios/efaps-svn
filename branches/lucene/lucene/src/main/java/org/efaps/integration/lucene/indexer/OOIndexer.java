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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.lucene.document.Field;

public class OOIndexer extends AbstractIndexer {

    public static String parse(InputStream is) {
	List XMLFiles = unzip(is);
	String test = XmlIndexer.parse((InputStream) XMLFiles.get(0));
	return test;

    }

    public static List unzip(InputStream is) {
	List<InputStream> res = new ArrayList<InputStream>();
	try {
	    ZipInputStream in = new ZipInputStream(is);
	    ZipEntry entry = null;
	    while ((entry = in.getNextEntry()) != null) {
		if (entry.getName().equals("content.xml")) {
		    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
			stream.write(buf, 0, len);
		    }
		    InputStream isEntry = new ByteArrayInputStream(stream
			    .toByteArray());
		    res.add(isEntry);
		}
	    }
	    in.close();
	} catch (IOException e) {

	}
	return res;
    }

    @Override
    public String getContent() {
	return parse(getStream());
    }

    @Override
    public Field getContentField() {

	return new Field("contents", getContent(), Field.Store.NO,
		Field.Index.TOKENIZED);
    }

   
}