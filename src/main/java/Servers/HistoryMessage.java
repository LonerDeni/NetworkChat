package Servers;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryMessage {
    protected List<String> historyM = new LinkedList<>();
    private static final StringBuilder logs = new StringBuilder();
    private static String path = "/NetworkChat/src/main/resources/";
    private static String nameFile = "saveMessageServer.txt";
    public Logger logger = Logger.getLogger(HistoryMessage.class.getName());

    public void addMessage(String message) {
        historyM.add(message);
        createFile(path, nameFile);
        logs.append(message + "\n");
        writeFile(path + nameFile, message);
    }

    public void viewMessage(BufferedWriter writer) throws IOException {
        if (historyM.size() > 0) {
            for (String str : historyM) {
                writer.write(str + "\n");
                writer.flush();
            }
        } else {
            readFile(path + nameFile, writer);
        }
    }

    public void createFile(String path, String name) {
        File file = new File(path, name);
        try {
            if (!file.exists()) {
                file.createNewFile();
                logger.log(Level.FINE,"Создан новый файл " , name);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,"Ошибка при создании файла" , e);
        }
    }

    public void writeFile(String path, String message) {
        try (FileWriter fileWriter = new FileWriter(path, true)) {
            fileWriter.write(message + "\n");
            fileWriter.flush();
            logger.log(Level.INFO,"В файл записана строка: " , message);
        } catch (IOException ex) {
            logger.log(Level.WARNING,"Ошибка при записи в файла" , ex);
        }
    }

    public void readFile(String path, BufferedWriter writer) {
        try {
            if (new File(path).exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line = reader.readLine();
                while (line != null) {
                    writer.write(line + "\n");
                    line = reader.readLine();
                    logger.log(Level.INFO,"Из файла прочитана строчка " , line);
                }
                reader.close();
            } else {
                writer.write("Сообщений нет" + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при чтении из файла", e);
        }
    }
}