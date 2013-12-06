package ru.fizteh.fivt.students.elenav.storeable;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class Serializer {
    
    public static String run(Table table, Storeable storeable) throws XMLStreamException {
        
        if (storeable == null) {
            return null;
        }
        
        StringWriter string = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(string);
        try {
            int n = table.getColumnsCount();
            writer.writeStartElement("row");
            for (int i = 0; i < n; ++i) {
                Object field = storeable.getColumnAt(i);
                if (field != null) {
                    writer.writeStartElement("col");
                    if (field.getClass() != table.getColumnType(i)) {
                        throw new ColumnFormatException("column type is not similar");
                    }
                    writer.writeCharacters(field.toString());
                    writer.writeEndElement();
                } else {
                    writer.writeEmptyElement("null");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("size is not similar");
        }
        writer.writeEndElement();
        
        return string.toString();
    }
    
}
