/*
 * Copyright 2003 - 2007 The eFaps Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.integration.lucene.indexer;

import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.lucene.document.Field;

/**
 * Class for getting the content out of an "Microsoft-Exel"-File (97-XP)
 * 
 * @author jmo
 * 
 */
public class ExcelIndexer extends AbstractIndexer {

  public static String parse(InputStream is) {

    String content = "";
    StringBuffer sb = new StringBuffer();

    Workbook workbook = null;
    try {
      workbook = Workbook.getWorkbook(is);
    } catch (BiffException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Sheet[] sheets = workbook.getSheets();
    for (int i = 0; i < sheets.length; i++) {
      Sheet sheet = sheets[i];
      int nbCol = sheet.getColumns();
      for (int j = 0; j < nbCol; j++) {
        Cell[] cells = sheet.getColumn(j);
        for (int k = 0; k < cells.length; k++) {
          sb.append(cells[k].getContents() + " ");
        }
      }
    }

    content = sb.toString();
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
