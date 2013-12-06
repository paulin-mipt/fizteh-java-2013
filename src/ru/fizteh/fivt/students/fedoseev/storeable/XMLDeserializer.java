package ru.fizteh.fivt.students.fedoseev.storeable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class XMLDeserializer {
    String xmlRepresentation;
    XMLStreamReader xmlStreamReader;

    public XMLDeserializer(String s) throws ParseException {
        this.xmlRepresentation = s;

        try {
            xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!xmlStreamReader.hasNext()) {
                throw new ParseException("DESERIALIZE ERROR: empty xml stream", 0);
            }
            if (xmlStreamReader.next() != XMLStreamConstants.START_ELEMENT
                    || !xmlStreamReader.getName().getLocalPart().equals("row")) {
                throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("DESERIALIZE ERROR", 0);
        }
    }

    public Object getNext(Class<?> requiredType) throws ParseException {
        Object value;

        try {
            int eventType = xmlStreamReader.next();

            if (xmlStreamReader.getLocalName().equals("null")) {
                xmlStreamReader.next();

                return null;
            }

            if (eventType != XMLStreamConstants.START_ELEMENT
                    || !xmlStreamReader.getName().getLocalPart().equals("col")) {
                throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
            }

            if (xmlStreamReader.next() == XMLStreamConstants.CHARACTERS) {
                value = ColumnTypes.commonParseValue(xmlStreamReader.getText(), requiredType);
            } else {
                throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
            }

            if (xmlStreamReader.next() != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
        }

        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int eventType = xmlStreamReader.next();

            xmlStreamReader.close();

            if (eventType != XMLStreamConstants.END_ELEMENT && eventType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("DESERIALIZE ERROR");
        }
    }
}
