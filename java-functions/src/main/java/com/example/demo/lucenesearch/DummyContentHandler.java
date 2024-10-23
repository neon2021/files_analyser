package com.example.demo.lucenesearch;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * sourced from: [TIKA-1351] Parser implementations should accept null content handlers - ASF JIRA
 * https://issues.apache.org/jira/browse/TIKA-1351
 *
 * sourced from: Apache Tika - Parsing and extracting only metadata without reading content - Stack Overflow
 * https://stackoverflow.com/questions/37825349/apache-tika-parsing-and-extracting-only-metadata-without-reading-content
 *
 * @author neon2021 on 2024/10/22
 */
public class DummyContentHandler implements ContentHandler {

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }
}
