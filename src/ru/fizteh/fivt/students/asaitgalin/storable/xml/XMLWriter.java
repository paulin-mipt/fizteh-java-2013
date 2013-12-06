package ru.fizteh.fivt.students.asaitgalin.storable.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

public class XMLWriter {
    StringWriter stringWriter;
    XMLStreamWriter writer;

    public XMLWriter() {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        stringWriter = new StringWriter();
        try {
            writer = factory.createXMLStreamWriter(stringWriter);
            writer.writeStartElement("row");
        } catch (XMLStreamException e) {
            throw new RuntimeException("xmlwriter: failed to create XMLStreamWriter", e);
        }
    }

    public void writeValue(Object value) {
        try {
            writer.writeStartElement("col");
            if (value == null) {
                writer.writeStartElement("null");
                writer.writeEndElement();
            } else {
                writer.writeCharacters(value.toString());
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException("xmlwriter: failed to write value", e);
        }
    }

    public void close() {
        try {
            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException("xmlwriter: failed to finalize file", e);
        }
    }

    public String getString() {
        return stringWriter.toString();
    }
}
