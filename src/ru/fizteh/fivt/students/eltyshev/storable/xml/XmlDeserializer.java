package ru.fizteh.fivt.students.eltyshev.storable.xml;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.eltyshev.storable.TypesFormatter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class XmlDeserializer {
    String xmlRepresentation;
    XMLStreamReader xmlReader;

    public XmlDeserializer(String xmlRepresentation) throws ParseException {
        this.xmlRepresentation = xmlRepresentation;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!xmlReader.hasNext()) {
                throw new ParseException("xml presentation is empty", 0);
            }

            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("incorrect xml", 0);
            }

            if (!xmlReader.getName().getLocalPart().equals("row")) {
                throw new ParseException("incorrect xml", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("error while deserializing: " + e.getMessage(), 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException {
        Object value = null;
        try {
            int nodeType = xmlReader.next();
            if (nodeType == XMLStreamConstants.START_ELEMENT) {
                if (xmlReader.getName().getLocalPart().equals("col")) {
                    nodeType = xmlReader.next();
                    if (nodeType == XMLStreamConstants.CHARACTERS) {
                        value = TypesFormatter.parseByClass(xmlReader.getText(), expectedType);
                    } else {
                        throw new ParseException("incorrect xml: expected characters between cols", 0);
                    }
                    nodeType = xmlReader.next();
                    if (nodeType != XMLStreamConstants.END_ELEMENT) {
                        throw new ParseException("incorrect xml: end element expected", 0);
                    }
                } else if (xmlReader.getName().getLocalPart().equals("null")) {
                    value = null;
                    xmlReader.next();
                } else {
                    throw new ParseException("incorrect xml: col or null expected", 0);
                }
            } else {
                throw new ParseException("incorrect xml: start element expected", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("incorrect xml: " + e.getMessage(), 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int nodeType = xmlReader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT && nodeType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("incorrect xml", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("error while deserializing: " + e.getMessage());
        }
    }
}
