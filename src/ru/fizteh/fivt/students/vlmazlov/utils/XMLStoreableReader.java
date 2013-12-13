package ru.fizteh.fivt.students.vlmazlov.utils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.text.ParseException;

public class XMLStoreableReader {
    private XMLStreamReader reader;

    private Object stringToObject(Class<?> type, String value) throws ValidityCheckFailedException {

        if (TypeName.getByClass(type) == null) {
            return null;
        }

        switch (TypeName.getByClass(type)) {
            case INTEGER:
                return Integer.valueOf(value);
            case LONG:
                return Long.valueOf(value);
            case DOUBLE:
                return Double.valueOf(value);
            case FLOAT:
                return Float.valueOf(value);
            case BYTE:
                return Byte.valueOf(value);
            case BOOLEAN:
                return Boolean.valueOf(value);
            case STRING:
                ValidityChecker.checkTableValue((String) value);
                return (String) value;
            default:
                //never reached
                return null;
        }
    }

    private void closeReader() {
        try {
            reader.close();
        } catch (XMLStreamException ex) {
            System.err.println("Unable to close XMLStreamReader: " + ex.getMessage());
        }
    }

    public XMLStoreableReader(String serialized) throws ParseException, XMLStreamException {
        reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(serialized));
        try {
            if (!reader.hasNext()) {
                throw new ParseException("Unable to read start row tag", 0);
            }

            reader.next();

            if ((!reader.isStartElement()) || (!reader.getLocalName().equals("row"))) {
                throw new ParseException("Invalid start row tag " + reader.getLocalName(), 0);
            }
        } catch (XMLStreamException ex) {
            closeReader();
            throw ex;
        } catch (ParseException ex) {
            closeReader();
            throw ex;
        }
    }

    public Object readColumn(Class<?> type) throws ParseException, XMLStreamException {
        try {
            if (!reader.hasNext()) {
                throw new ParseException("Unexpected end of serialized string", 0);
            }

            reader.next();

            if ((!reader.isStartElement()) || ((!reader.getLocalName().equals("col"))
                    && (!reader.getLocalName().equals("null")))) {
                throw new ParseException("Invalid start column tag "  + reader.getLocalName(), 0);
            }

            String tagName = reader.getLocalName();
            Object curValue = null;

            if (!(reader.getLocalName().equals("null"))) {
                if (!reader.hasNext()) {
                    throw new ParseException("Unexpected end of serialized string", 0);
                }

                reader.next();

                if (!reader.isCharacters()) {
                    throw new ParseException("Unable to deserialize value", 0);
                }

                try {
                    curValue = stringToObject(type, reader.getText());
                } catch (ValidityCheckFailedException ex) {
                    throw new ParseException("Invalid string value " + reader.getText(), 0);
                }

                if (curValue == null) {
                    throw new ParseException("Wrong type " + type + " for " + reader.getText(), 0);
                }
            }

            if (!reader.hasNext()) {
                throw new ParseException("Unexpected end of serialized string", 0);
            }
            reader.next();

            if ((!reader.isEndElement()) || (!(reader.getLocalName().equals(tagName)))) {
                throw new ParseException("Invalid end column tag", 0);
            }

            return curValue;
        } catch (XMLStreamException ex) {
            closeReader();
            throw ex;
        } catch (ParseException ex) {
            closeReader();
            throw ex;
        }
    }

    public void close() throws ParseException, XMLStreamException {
        try {
            if (!reader.hasNext()) {
                throw new ParseException("Unexpected end of serialized string", 0);
            }

            reader.next();

            if ((!reader.isEndElement()) || (!reader.getLocalName().equals("row"))) {
                throw new ParseException("Invalid end row tag " + reader.getLocalName(), 0);
            }

            if (reader.hasNext()) {
                throw new ParseException("Serialized string is too long", 0);
            }

        } finally {
            closeReader();
        }
    }
}
