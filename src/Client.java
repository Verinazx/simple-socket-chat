import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    public static void main(String[] args) {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Введите IP сервера: ");
            String host = consoleReader.readLine().trim();
            System.out.print("Введите порт сервера: ");
            int port = Integer.parseInt(consoleReader.readLine().trim());

            Socket socket = new Socket(host, port);
            System.out.println("Подключились к " + socket.getRemoteSocketAddress());

            AtomicBoolean running = new AtomicBoolean(true);


            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while (running.get() && (line = in.readLine()) != null) {
                        System.out.println("Сервер: " + line);
                        if ("close".equalsIgnoreCase(line.trim())) {
                            running.set(false);
                            break;
                        }
                    }
                } catch (IOException e) {
                    if (running.get()) e.printStackTrace();
                }
            }).start();


            new Thread(() -> {
                try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    String line;
                    while (running.get() && (line = consoleReader.readLine()) != null) {
                        out.println(line);
                        if ("close".equalsIgnoreCase(line.trim())) {
                            running.set(false);
                            break;
                        }
                    }
                } catch (IOException e) {
                    if (running.get()) e.printStackTrace();
                }
            }).start();


            while (running.get()) {
                Thread.sleep(100);
            }


            System.out.println("Закрываем соединение...");
            socket.close();
            consoleReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}







