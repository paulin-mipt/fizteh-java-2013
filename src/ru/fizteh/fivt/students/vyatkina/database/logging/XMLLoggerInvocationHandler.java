package ru.fizteh.fivt.students.vyatkina.database.logging;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

public class XMLLoggerInvocationHandler implements InvocationHandler {

    private final XMLStreamWriter writer;
    private final Object implementation;

    private static final String INVOKE = "invoke";
    private static final String TIMESTAMP = "timestamp";
    private static final String CLASS = "class";
    private static final String METHOD_NAME = "name";
    private static final String ARGUMENTS = "arguments";
    private static final String ARGUMENT = "argument";
    private static final String RETURN = "return";
    private static final String THROWN = "thrown";
    private static final String NULL = "null";
    private static final String LIST = "list";
    private static final String VALUE = "value";
    private static final String CYCLIC = "cyclic";
    private final Set<Object> identitySet = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    public XMLLoggerInvocationHandler(XMLStreamWriter writer, Object implementation) {
        this.implementation = implementation;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            try {
                return method.invoke(implementation, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        writer.writeStartElement(INVOKE);
        writer.writeAttribute(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        writer.writeAttribute(CLASS, implementation.getClass().getName());
        writer.writeAttribute(METHOD_NAME, method.getName());
        writeArgs(args);
        try {
            if (method.getReturnType().equals(Void.TYPE)) {
                return method.invoke(implementation, args);

            } else {
                Object returnValue = method.invoke(implementation, args);
                writer.writeStartElement(RETURN);
                identitySet.clear();
                writeValue(returnValue, identitySet);
                writer.writeEndElement();
                return returnValue;
            }
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            writer.writeStartElement(THROWN);
            writer.writeCharacters(targetException.toString());
            writer.writeEndElement();
            throw targetException;
        }
        catch (Throwable e) {
            throw new IllegalStateException(e);
        }

        finally {
            writer.writeEndElement();
            writer.writeCharacters(System.lineSeparator());
            writer.flush();
        }
    }

    private void writeArgs(Object[] args) throws XMLStreamException {
        if (args == null || args.length == 0) {
            writer.writeEmptyElement(ARGUMENTS);
            return;
        }
        writer.writeStartElement(ARGUMENTS);
        for (int i = 0; i < args.length; ++i) {
            writer.writeStartElement(ARGUMENT);
            writeArg(args[i]);
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeArg (Object arg) throws XMLStreamException {
        if (arg == null) {
            writer.writeEmptyElement(NULL);
        } else if (Iterable.class.isAssignableFrom(arg.getClass())) {
                identitySet.clear();
                identitySet.add(arg);
                writeIterable((Iterable) arg, identitySet);
        } else {
            writer.writeCharacters(arg.toString());
        }
    }

    private void writeValue(Object value, Set<Object> identitySet) throws XMLStreamException {
        if (value == null) {
            writer.writeEmptyElement(NULL);
        } else if (Iterable.class.isAssignableFrom(value.getClass())) {
            if (identitySet.contains(value)) {
                writer.writeCharacters(CYCLIC);
            } else {
                writeIterable((Iterable) value, identitySet);
            }
        } else {
            writer.writeCharacters(value.toString());
        }
    }

    private void writeIterable(Iterable<?> iterable, Set<Object> identitySet) throws XMLStreamException {
        Iterator<?> it = iterable.iterator();
        identitySet.add(this);
        if (!it.hasNext()) {
            writer.writeEmptyElement(LIST);
            return;
        }
        writer.writeStartElement(LIST);
        for (; ; ) {
            Object value = it.next();
            writer.writeStartElement(VALUE);
            writeValue(value, identitySet);
            writer.writeEndElement();
            if (!it.hasNext()) {
                writer.writeEndElement();
                return;
            }
        }
    }


    public static void main(String[] args)  {

    }
}
