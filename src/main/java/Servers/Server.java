package Servers;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    public Socket socket;
    public BufferedReader in;
    public BufferedWriter out;
    private String userName;
    public Logger logger = Logger.getLogger(Server.class.getName());


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
            userName = in.readLine();
            out.write("Добро пожаловать: " + userName + "\n");
            out.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка ввода/вывода имени на сервере", e);
        }
        try {
            while (true) {
                Date time = new Date();
                SimpleDateFormat dateForm = new SimpleDateFormat("HH:mm:ss");
                String mesDate = dateForm.format(time);
                word = in.readLine();
                logger.log(Level.FINE, "Получено сообщение на сервере");
                if (word.equals("/exit")) {
                    this.downService();
                    break;
                }
                word = "(" + mesDate + ") " + userName + ": " + word;
                MainServer.historyMessage.addMessage(word);
                for (Server mr : MainServer.clients) {
                    mr.sendAll(word);
                    logger.log(Level.INFO, "Отправлено сообщение с сервера клиенту", mr);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка ввода имени в потоке записи", e);
        }
    }

    public void sendAll(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
        logger.log(Level.FINE, "Отправлено сообщение клиенту");
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
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка закрытия потоков ввода/вывода и сокета клиента", e);
        }
    }
}