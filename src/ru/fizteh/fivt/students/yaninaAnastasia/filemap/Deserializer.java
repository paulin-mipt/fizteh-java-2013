package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class Deserializer {
    String xmlRepresentation;
    XMLStreamReader reader;

    public Deserializer(String str) throws ParseException {
        this.xmlRepresentation = str;
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlRepresentation));

            if (!reader.hasNext()) {
                throw new ParseException("string in xml format is empty", 0);
            }

            int nodeType = reader.next();
            if (nodeType != XMLStreamConstants.START_ELEMENT) {
                throw new ParseException("Incorrect XML format01", 0);
            }

            if (!reader.getName().getLocalPart().equals("row")) {
                throw new ParseException("Incorrect XML format0", 0);
            }

        } catch (XMLStreamException e) {
            throw new ParseException("Error with deserializing", 0);
        }
    }

    public Object getNext(Class<?> expectedType) throws ColumnFormatException, ParseException {
        Object value = null;
        try {
            int nodeType = reader.next();
            if (reader.getLocalName().equals("null")) {
                reader.next();
                return null;
            }
            if (nodeType != XMLStreamConstants.START_ELEMENT || !reader.getName().getLocalPart().equals("col")) {
                throw new ParseException("Incorrect XML format1", 0);
            }
            nodeType = reader.next();
            if (nodeType == XMLStreamConstants.CHARACTERS) {
                value = ColumnTypes.parsingValue(reader.getText(), expectedType);
            } else {
                throw new ParseException("Incorrect XML format2", 0);
            }
            nodeType = reader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT) {
                throw new ParseException("Incorrect XML format3", 0);
            }
        } catch (XMLStreamException e) {
            throw new ParseException("Incorrect XML format4", 0);
        }
        return value;
    }

    public void close() throws IOException, ParseException {
        try {
            int nodeType = reader.next();
            if (nodeType != XMLStreamConstants.END_ELEMENT && nodeType != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("Incorrect XML format5", 0);
            }
        } catch (XMLStreamException e) {
            throw new IOException("Error with deserializing6");
        }
    }
}
