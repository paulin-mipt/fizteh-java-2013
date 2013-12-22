package ru.fizteh.fivt.students.kislenko.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

public class MyInvocationHandler implements InvocationHandler {
    ThreadLocal<Writer> w = new ThreadLocal<Writer>();
    ThreadLocal<Object> implementation = new ThreadLocal<Object>();
    static ThreadLocal<XMLStreamWriter> writer = new ThreadLocal<XMLStreamWriter>();
    ThreadLocal<Integer> invokeCounter = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return 0;
        }
    };
    ThreadLocal<IdentityHashMap<Object, Boolean>> identityHashMap
            = new ThreadLocal<IdentityHashMap<Object, Boolean>>() {
        public IdentityHashMap<Object, Boolean> initialValue() {
            return new IdentityHashMap<Object, Boolean>();
        }
    };

    public MyInvocationHandler(Writer writer, Object impl) {
        w.set(writer);
        implementation.set(impl);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(implementation.get(), args);
        }
        Object result;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        if (invokeCounter.get() == 0) {
            MyInvocationHandler.writer.set(factory.createXMLStreamWriter(w.get()));
        }
        invokeCounter.set(invokeCounter.get() + 1);

        writer.get().writeStartElement("invoke");
        writer.get().writeAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
        writer.get().writeAttribute("class", implementation.get().getClass().getName());
        writer.get().writeAttribute("name", method.getName());
        if (args != null && args.length > 0) {
            writer.get().writeStartElement("arguments");
            for (Object arg : args) {
                writer.get().writeStartElement("argument");
                logArgument(writer.get(), arg);
                writer.get().writeEndElement();
            }
            writer.get().writeEndElement();
        } else {
            writer.get().writeEmptyElement("arguments");
        }
        try {
            result = method.invoke(implementation.get(), args);
            if (!method.getReturnType().isAssignableFrom(void.class)) {
                writer.get().writeStartElement("return");
                logArgument(writer.get(), result);
                writer.get().writeEndElement();
            }
        } catch (Exception e) {
            writer.get().writeStartElement("thrown");
            logArgument(writer.get(), e.getCause());
            writer.get().writeEndDocument();
            writer.get().writeCharacters("\n");
            writer.get().flush();
            throw e.getCause();
        }
        writer.get().writeEndElement();
        invokeCounter.set(invokeCounter.get() - 1);
        if (invokeCounter.get() == 0) {
            writer.get().writeCharacters("\n");
        }
        writer.get().flush();
        return result;
    }

    private void logArgument(XMLStreamWriter w, Object arg) throws XMLStreamException {
        if (arg == null) {
            w.writeEmptyElement("null");
            return;
        }
        if (List.class.isAssignableFrom(arg.getClass())) {
            w.writeStartElement("list");
            List list = (List) arg;
            for (Object e : list) {
                w.writeStartElement("value");
                if (identityHashMap.get().get(e) != null && identityHashMap.get().get(arg)) {
                    w.writeCharacters("cyclic");
                } else {
                    identityHashMap.get().put(e, true);
                    logArgument(w, e);
                    if (identityHashMap.get().containsKey(e)) {
                        identityHashMap.get().remove(e);
                    }
                }
                w.writeEndElement();
            }
            w.writeEndElement();
            return;
        }
        if (Method.class.isAssignableFrom(arg.getClass())) {
            Method method = (Method) arg;
            w.writeCharacters(method.getName());
            return;
        }
        w.writeCharacters(arg.toString());
        identityHashMap.get().remove(arg);
    }
}
