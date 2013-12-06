package ru.fizteh.fivt.students.elenav.storeable;

import java.io.StringReader;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class Deserializer {
    
    public static Storeable run(Table table, String value) throws XMLStreamException, ParseException {
        
        if (value == null) {
            return null;
        }
        
        MyStoreable st = new MyStoreable(table);
        StringReader r = new StringReader(value);
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(r);
        int i = 0;
        reader.next();
        if (!reader.isStartElement() || !reader.getName().getLocalPart().equals("row")) {
            throw new ParseException("parse error: expected <row>", i);
        }
        
        while (i < table.getColumnsCount()) {
            reader.next();
            if (reader.isStartElement() && reader.getName().getLocalPart().equals("col")) {
                reader.next();
                if (reader.isCharacters()) {
                    String smth = reader.getText();
                    st.setColumnAt(i, TypeClass.parse(smth, table.getColumnType(i)));
                    ++i;
                } else {
                    throw new ParseException("parse error: empty value", i);
                }  
            } else {
                if (reader.isStartElement() && reader.getName().getLocalPart().equals("null")) {
                    st.setColumnAt(i, null);
                    ++i;
                } else {
                    throw new ParseException("parse exception: expected next element", i);
                }
            }
            reader.next();
            if (!reader.isEndElement()) {
                throw new ParseException("parse error3", i);
            }
            
        }

        reader.next();
        if (!reader.isEndElement()) {
            throw new ParseException("last element not found", i);
        }
        
        return st;
        
    }
    
}
