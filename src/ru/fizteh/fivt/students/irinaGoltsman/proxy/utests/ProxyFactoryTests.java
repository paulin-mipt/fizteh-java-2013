package ru.fizteh.fivt.students.irinaGoltsman.proxy.utests;

import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.irinaGoltsman.proxy.LoggingProxyFactoryImplementation;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProxyFactoryTests {
    LoggingProxyFactory factory = new LoggingProxyFactoryImplementation();

    @Test(expected = IllegalArgumentException.class)
    public void nullImplementationShouldFail() {
        factory.wrap(new PrintWriter(System.out), null, Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInterfaceClassShouldFail() {
        factory.wrap(new PrintWriter(System.out), new HashSet<Integer>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullWriterShouldFail() {
        factory.wrap(null, new HashSet<Integer>(), Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void targetNotImplementingInterfaceClassShouldFail() {
        factory.wrap(new PrintWriter(System.out), new HashSet<Integer>(), List.class);
    }


}
