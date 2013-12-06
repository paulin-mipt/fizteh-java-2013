package ru.fizteh.fivt.students.vyatkina;

public class State {

    protected IOStreams ioStreams = new IOStreams();

    public IOStreams getIoStreams() {
        return ioStreams;
    }

    public void setIOStreams(IOStreams ioStreams) {
        this.ioStreams = ioStreams;
    }

    public void printUserMessage(String message) {
        ioStreams.out.println(message);
    }

    public void printErrorMessage(String message) {
        ioStreams.err.println(message);
    }

    public void printInvitation() {
        ioStreams.out.print("$ ");
    }

}
