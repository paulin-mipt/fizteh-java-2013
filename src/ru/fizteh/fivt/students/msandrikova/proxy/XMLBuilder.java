package ru.fizteh.fivt.students.msandrikova.proxy;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



public class XMLBuilder {
    private StringWriter stringWriter;
    private XMLStreamWriter writer;
    private IdentityHashMap<Object, Boolean> checkCyclic;
    
    public XMLBuilder(Class<?> givenClass, Method givenMethod, Object[] args) throws XMLStreamException {
        this.checkCyclic = new IdentityHashMap<Object, Boolean>();
        this.stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        this.writer = factory.createXMLStreamWriter(stringWriter);
        this.writer.writeStartElement("invoke");
        this.writer.writeAttribute("timestamp", Long.toString(System.currentTimeMillis()));
        this.writer.writeAttribute("class", givenClass.getName());
        this.writer.writeAttribute("name", givenMethod.getName());
        this.writeArgs(args);

    }
    
    public void writeResult(Object res) throws XMLStreamException {
        this.writer.writeStartElement("return");
        if (res == null) {
            this.writer.writeStartElement("null");
            this.writer.writeEndElement();
        } else {
            this.writer.writeCharacters(res.toString());
        }
        this.writer.writeEndElement();
    }
    
    public void writeThrown(Throwable thrown) throws XMLStreamException {
        this.writer.writeStartElement("thrown");
        this.writer.writeCharacters(thrown.toString());
        this.writer.writeEndElement();
    }
    
    private void writeArgs(Object[] args) throws XMLStreamException {
        this.writer.writeStartElement("arguments");
        if (args != null) {
            for (Object o : args) {
                this.writer.writeStartElement("argument");
                if (o == null) {
                    this.writer.writeStartElement("null");
                    this.writer.writeEndElement();
                } else if (o instanceof List<?>) {
                    this.checkCyclic.clear();
                    this.checkCyclic.put(o, true);
                    this.writeList((List<?>) o);
                } else {
                    this.writer.writeCharacters(o.toString());
                }
                this.writer.writeEndElement();
            }
        }
        this.writer.writeEndElement();
    }
    
    private void writeList(List<?> list) throws XMLStreamException {
        this.writer.writeStartElement("list");
        for (Object o : list) {
            this.writer.writeStartElement("value");
            if (o == null) {
                this.writer.writeStartElement("null");
                this.writer.writeEndElement();
            } else if (o instanceof List<?>) {
                if (this.checkCyclic.get(o) != null) {
                    this.writer.writeCharacters("cyclic");
                } else {
                    this.checkCyclic.put(o, true);
                    this.writeList((List<?>) o);
                }
            } else {
                this.writer.writeCharacters(o.toString());
            }
            this.writer.writeEndElement();
        }
        this.writer.writeEndElement();
    }
    
    public void endXML() throws XMLStreamException {
        this.writer.writeEndElement();
        this.writer.flush();
    }
    
    
    @Override
    public String toString() {
        return this.stringWriter.toString();
    }
}
