package Servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class MainServer {
    public static List<Server> clients = new LinkedList<>();
    public static HistoryMessage historyMessage;
    public static int port;

    public static void main(String[] args) throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("config.properties"));
        port = Integer.parseInt(System.getProperty("server.port"));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            historyMessage = new HistoryMessage();
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    clients.add(new Server(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}