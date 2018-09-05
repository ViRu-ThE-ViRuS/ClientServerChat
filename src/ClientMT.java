
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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientMT {
    private static final int PORT = 4444;
    private static final String TERMINATE = "END";
    private Socket socket;

    private ClientOutConnection out;
    private ClientInConnection in;

    private ClientMT() {
        try {
            setupClient();
            start();
            cleanupClient();
            System.out.println("Program Ended...");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new ClientMT();
    }

    private void setupClient() throws IOException {
        socket = new Socket(InetAddress.getLocalHost(), PORT);
        System.out.println("Client started, connecting to server...");
    }

    private void cleanupClient() throws IOException {
        socket.close();
    }

    private void start() throws IOException, InterruptedException {
        System.out.println("Setup connected, opening up communication channels...");
        setupStreams();
        System.out.println("Terminating connections...");
        cleanupStreams();
        System.out.println("Connection terminated...");
    }

    private void setupStreams() throws IOException {
        out = new ClientOutConnection(socket);
        in = new ClientInConnection(socket);

        out.start();
        in.start();
    }

    private void cleanupStreams() throws InterruptedException {
        out.join();
        in.join();
    }

    private class ClientOutConnection extends Thread {
        private PrintWriter writer;
        private Scanner scanner;

        ClientOutConnection(Socket socket) throws IOException {
            writer = new PrintWriter(socket.getOutputStream());
            scanner = new Scanner(System.in);
        }

        @Override
        public void run() {
            super.run();

            String message = "";
            while (!message.equalsIgnoreCase(ClientMT.TERMINATE)) {
                message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            }
        }
    }

    private class ClientInConnection extends Thread {
        private BufferedReader reader;

        ClientInConnection(Socket socket) throws IOException {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            super.run();

            String message = "";
            while (!message.equalsIgnoreCase(ClientMT.TERMINATE)) {
                try {
                    message = reader.readLine();
                    System.out.println("SERVER: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
