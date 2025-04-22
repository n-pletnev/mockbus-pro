package ru.altacloud;

import ru.altacloud.server.ModbusTcpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            ModbusTcpServer modbusTcpServer = new ModbusTcpServer();
            modbusTcpServer.run();
            Runtime.getRuntime().addShutdownHook(new Thread(modbusTcpServer::stop));

            ServerSocket serverSocket = new ServerSocket(65535);
            serverSocket.accept();

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}