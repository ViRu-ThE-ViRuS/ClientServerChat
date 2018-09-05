
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

    //construct and run the server
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

    //create and start new server instance
    public static void main(String[] args) {
        new ServerMT();
    }

    //setup the server socket
    private void setupServer() throws IOException {
        server = new ServerSocket(PORT);
        System.out.println("Server started, waiting for client to connect...");
    }

    //cleanup the server and client sockets
    private void cleanupServer() throws IOException {
        server.close();
        client.close();
    }

    //start the communication sequence
    private void start() throws IOException, InterruptedException {
        client = server.accept();
        System.out.println("Client connected, opening up communication channels...");
        setupStreams();
        cleanupStreams();
        System.out.println("Connection terminated...");
    }

    //setup the communication streams
    private void setupStreams() throws IOException {
        out = new ServerOutConnection(client);
        in = new ServerInConnection(client);

        out.start();
        in.start();
    }

    //cleanup the streams by closing down the connections
    private void cleanupStreams() throws InterruptedException {
        out.join();
        in.join();
    }

    //handles the out-connection to the client
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

//            writer.close();
//            scanner.close();
        }
    }

    //handles the in-connection to the client
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

//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
