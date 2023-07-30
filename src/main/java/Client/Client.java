package Client;



import java.io.*;
import java.net.Socket;
import java.util.logging.*;


public class Client extends Thread {
    private String host;
    private int port;
    private BufferedReader in, inputUser;
    private BufferedWriter out;
    private Socket socket;
    private String userName;
    public Logger logger = Logger.getLogger(Client.class.getName());

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            logger.log(Level.FINE, "При создании сокета возникла ошибка", e);
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(readMessage).start();
            new Thread(writeMessage).start();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Буферезированный поток вызвал IOException", e);
            downServices();
        }
    }


    Runnable readMessage = new Runnable() {
        @Override
        public void run() {
            try {
                String wordClients;
                while (true) {
                    wordClients = in.readLine();
                    if (wordClients.equals("/exit")) {
                        downServices();
                        break;
                    }
                    logger.log(Level.INFO, "Прочитано сообщение");
                    System.out.println(wordClients);
                }
            } catch (IOException e) {
                downServices();
            }
        }
    };

    Runnable writeMessage = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Введите ваше имя:");
                userName = inputUser.readLine();
                out.write(userName + "\n");
                logger.log(Level.INFO, "Введено имя", userName);
                out.flush();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Ошибка ввода имени в потоке записи", e);
            }
            try {
                while (true) {
                    String mes = inputUser.readLine();
                    out.write(mes + "\n");
                    out.flush();
                    logger.log(Level.INFO, "Введено сообщение");
                    if (mes.equals("/exit")) {
                        downServices();
                        break;
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Ошибка ввода сообщений в потоке записи", e);
            }
        }
    };

    public void downServices() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка закрытия потоков ввода/вывода и сокета", e);
        }
    }
}