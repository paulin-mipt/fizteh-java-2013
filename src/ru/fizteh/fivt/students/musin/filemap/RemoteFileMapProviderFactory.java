package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RemoteFileMapProviderFactory implements RemoteTableProviderFactory {

    public RemoteFileMapProvider connect(String hostname, int port) throws IOException {
        if (hostname == null) {
            throw new IllegalArgumentException("Null hostname as argument");
        }
        Socket socket = new Socket(hostname, port);   //UnknownHostException, IllegalArgumentException, IOException
        if (!socket.isConnected()) {
            new RuntimeException("Failed to connect");
        }
        return new RemoteFileMapProvider(socket, hostname, port);
    }
}
