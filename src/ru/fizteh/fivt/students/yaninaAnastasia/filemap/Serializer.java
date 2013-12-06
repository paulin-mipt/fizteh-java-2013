package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class Serializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter streamWriter = null;

    public Serializer() throws IOException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            streamWriter = factory.createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("error while serializing: " + e.getMessage());
        }
    }

    public void write(Object value) throws IOException, ParseException {
        try {
            if (value == null) {
                streamWriter.writeEmptyElement("null");
            } else {
                streamWriter.writeStartElement("col");
                switch (ColumnTypes.fromTypeToName(value.getClass())) {
                    case "String":
                        String str = (String) value;
                        if (str.trim().isEmpty()) {
                            throw new ParseException("Incorrect value: it can not be null", 0);
                        }
                        break;
                    default:
                }
                streamWriter.writeCharacters(value.toString());
                streamWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new IOException("Error with serializing: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        try {
            streamWriter.writeEndElement();
            streamWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException("Error with serializing : " + e.getMessage());
        }
    }

    public String getRepresentation() {
        return stringWriter.getBuffer().toString();
    }
}
