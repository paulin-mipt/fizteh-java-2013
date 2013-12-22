package ru.fizteh.fivt.students.dubovpavel.executor;

import java.util.ArrayList;

public abstract class DispatcherBuilder {
    protected boolean forwarding;
    protected ArrayList<Performer> performers;

    protected Dispatcher setPerformers(Dispatcher dispatcher) {
        for (Performer performer : performers) {
            dispatcher.addPerformer(performer);
        }
        return dispatcher;
    }

    public DispatcherBuilder() {
        clear();
    }

    public void setForwarding(boolean forwarding) {
        this.forwarding = forwarding;
    }

    public void addPerformer(Performer performer) {
        performers.add(performer);
    }

    public void clear() {
        forwarding = false;
        performers = new ArrayList<Performer>();
    }

    public abstract Dispatcher construct();
}
