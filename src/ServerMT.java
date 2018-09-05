
/*
 * Copyright (c) 2018. This program is made and owned by Viraat Chandra [THE GREAT].
 * You MUST solemnly accept that he is Smarter than you.
 * He is the smartest man in the entire World.
 */

/*
 *
 * Created by Viraat_Chandra on 05/09/18.
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerMT {
    private static final int PORT = 4444;
    private static final String TERMINATE = "END";
    private ServerSocket server;
    private Socket client;

    private ServerOutConnection out;
    private ServerInConnection in;

    private ServerMT() {
        try {
            setupServer();
            start();
            cleanupServer();
            System.out.println("Program Ended...");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerMT();
    }

    private void setupServer() throws IOException {
        server = new ServerSocket(PORT);
        System.out.println("Server started, waiting for client to connect...");
    }

    private void cleanupServer() throws IOException {
        server.close();
    }

    private void start() throws IOException, InterruptedException {
        client = server.accept();
        System.out.println("Client connected, opening up communication channels...");
        setupStreams();
        System.out.println("Terminating connections...");
        cleanupStreams();
        System.out.println("Connection terminated...");
    }

    private void setupStreams() throws IOException {
        out = new ServerOutConnection(client);
        in = new ServerInConnection(client);

        out.start();
        in.start();
    }

    private void cleanupStreams() throws InterruptedException {
        out.join();
        in.join();
    }

    private class ServerOutConnection extends Thread {
        private PrintWriter writer;
        private Scanner scanner;

        ServerOutConnection(Socket socket) throws IOException {
            writer = new PrintWriter(socket.getOutputStream());
            scanner = new Scanner(System.in);
        }

        @Override
        public void run() {
            super.run();

            String message = "";
            while (!message.equalsIgnoreCase(ServerMT.TERMINATE)) {
                message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            }
        }
    }

    private class ServerInConnection extends Thread {
        private BufferedReader reader;

        ServerInConnection(Socket client) throws IOException {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }

        @Override
        public void run() {
            super.run();

            String message = "";
            while (!message.equalsIgnoreCase(ServerMT.TERMINATE)) {
                try {
                    message = reader.readLine();
                    System.out.println("CLIENT : " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
