package org.efaps.integration.lucene.indexer;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.document.Field;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlIndexer extends AbstractIndexer {

    public static String parse(InputStream _inputstream) {
	org.w3c.dom.Document document = null;
	DocumentBuilder builder = null;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	try {
	    builder = factory.newDocumentBuilder();

	    document = builder.parse(_inputstream);
	} catch (ParserConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	catch (SAXException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return parse(document);
    }

    public static String parse(org.w3c.dom.Document _document) {

	StringBuffer XMLStream = new StringBuffer();
	NodeList ls = _document.getChildNodes();
	for (int i = 0; i < ls.getLength(); i++) {

	    String text = "";

	    short NodType = ls.item(i).getNodeType();

	    if (NodType == org.w3c.dom.Node.ELEMENT_NODE
		    || NodType == org.w3c.dom.Node.TEXT_NODE) {
		text = ls.item(i).getTextContent().trim();
	    } else if (NodType == org.w3c.dom.Node.ATTRIBUTE_NODE) {
		text = ls.item(i).getNodeValue().trim();
	    } else if (NodType == org.w3c.dom.Node.CDATA_SECTION_NODE) {
		text = ls.item(i).getTextContent().trim();
	    } else if (NodType == org.w3c.dom.Node.COMMENT_NODE) {

	    } else if (NodType == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE) {

	    } else if (NodType == org.w3c.dom.Node.ENTITY_NODE) {

	    }
	    if (text != "") {
		XMLStream.append(text);

	    }

	}
	return XMLStream.toString().trim();
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
