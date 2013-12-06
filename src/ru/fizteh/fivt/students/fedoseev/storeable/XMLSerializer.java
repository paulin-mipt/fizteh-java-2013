package ru.fizteh.fivt.students.fedoseev.storeable;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class XMLSerializer implements Closeable {
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter = null;

    public XMLSerializer() throws IOException {
        try {
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
            xmlStreamWriter.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new IOException("SERIALIZE ERROR: invalid format, creating serializer failed");
        }
    }

    public void write(Object value) throws IOException, ParseException {
        try {
            if (value == null) {
                xmlStreamWriter.writeEmptyElement("null");
            } else {
                xmlStreamWriter.writeStartElement("col");

                String stringValue = value.toString();

                xmlStreamWriter.writeCharacters(stringValue);
                xmlStreamWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new IOException("SERIALIZE ERROR: writing failed");
        }
    }

    public void close() throws IOException {
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.flush();
            xmlStreamWriter.close();
        } catch (XMLStreamException e) {
            throw new IOException("SERIALIZE ERROR: closing failed");
        }
    }

    public String getStringWriterContents() {
        return stringWriter.getBuffer().toString();
    }
}
