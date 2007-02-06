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

import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.lucene.document.Field;

public class RtfIndexer extends AbstractIndexer {

    public static String parse(InputStream is) {
	String content = "";
	DefaultStyledDocument dsd = new DefaultStyledDocument();
	RTFEditorKit RTFEkit = new RTFEditorKit();
	try {
	    RTFEkit.read(is, dsd, 0);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BadLocationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    content = dsd.getText(0, dsd.getLength());
	} catch (BadLocationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return content;
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