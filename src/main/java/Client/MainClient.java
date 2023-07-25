package Client;

import java.io.IOException;

public class MainClient {
    private static String host = "localhost";
    private static int port;

    public static void main(String[] args) throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("config.properties"));
        port = Integer.parseInt(System.getProperty("server.port"));
        new Client(host,port);
    }
}
