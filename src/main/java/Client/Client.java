package Client;


import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Thread {
    private String host;
    private int port;
    private BufferedReader in, inputUser;
    private BufferedWriter out;
    private Socket socket;
    private String userName;
    private Date time;
    private SimpleDateFormat dateForm;
    private String mesDate;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(readMessage).start();
            new Thread(writeMessage).start();
        } catch (IOException e) {
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
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    time = new Date();
                    dateForm = new SimpleDateFormat("HH:mm:ss");
                    mesDate = dateForm.format(time);
                    String mes = inputUser.readLine();
                    out.write("(" + mesDate + ") " + userName + ": " + mes + "\n");
                    out.flush();
                    if (mes.equals("/exit")) {
                        downServices();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        } catch (IOException ignored) {
        }
    }
}