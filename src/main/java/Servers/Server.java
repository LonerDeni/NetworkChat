package Servers;

import java.io.*;
import java.net.Socket;

public class Server extends Thread {
    protected Socket socket;
    public BufferedReader in;
    public BufferedWriter out;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
        MainServer.historyMessage.viewMessage(out);
    }

    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            out.write("Добро пожаловать: " + word + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
        try {
            while (true) {
                word = in.readLine();
                if (word.equals("/exit")) {
                    this.downService();
                    break;
                }
                MainServer.historyMessage.addMessage(word);
                for (Server mr : MainServer.clients) {
                    mr.sendAll(word);
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void sendAll(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
    }

    public void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Server vr : MainServer.clients) {
                    if (vr.equals(this))
                        vr.interrupt();
                    MainServer.clients.remove(this);
                }
            }
        } catch (IOException ignored) {
        }
    }
}