import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    public static void main(String[] args) {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите порт для сервера: ");
        try {
            int port = Integer.parseInt(consoleReader.readLine().trim());
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен, ожидаем клиента...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Клиент подключился: " + clientSocket.getRemoteSocketAddress());

            AtomicBoolean running = new AtomicBoolean(true);


            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String line;
                    while (running.get() && (line = in.readLine()) != null) {
                        System.out.println("Клиент: " + line);
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
                try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
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
            clientSocket.close();
            serverSocket.close();
            consoleReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


