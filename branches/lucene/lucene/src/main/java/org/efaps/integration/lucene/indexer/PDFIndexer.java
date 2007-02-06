package org.efaps.integration.lucene.indexer;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.lucene.document.Field;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

public class PDFIndexer extends AbstractIndexer {
    private PDFTextStripper STRIPPER = null;

    @Override
    public String getContent() {
	PDDocument pdfDocument = null;

	try {
	    pdfDocument = PDDocument.load(getStream());

	    if (pdfDocument.isEncrypted()) {
		pdfDocument.decrypt("");
	    }

	    StringWriter writer = new StringWriter();

	    STRIPPER = new PDFTextStripper();

	    STRIPPER.writeText(pdfDocument, writer);

	    String contents = writer.getBuffer().toString();

	    return contents;

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CryptographyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvalidPasswordException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    @Override
    public Field getContentField() {

	return new Field("contents", getContent(), Field.Store.NO,
		Field.Index.TOKENIZED);
    }

}
