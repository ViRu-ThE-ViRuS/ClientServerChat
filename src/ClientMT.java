
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

    //construct and run the client
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

    //create and start new client instance
    public static void main(String args[]) {
        new ClientMT();
    }

    //setup the client socket
    private void setupClient() throws IOException {
        socket = new Socket(InetAddress.getLocalHost(), PORT);
        System.out.println("Client started, connecting to server...");
    }

    //cleanup the client socket
    private void cleanupClient() throws IOException {
        socket.close();
    }

    //start the communication sequence
    private void start() throws IOException, InterruptedException {
        System.out.println("Setup connected, opening up communication channels...");
        setupStreams();
        cleanupStreams();
        System.out.println("Connection terminated...");
    }

    //start the communication streams
    private void setupStreams() throws IOException {
        out = new ClientOutConnection(socket);
        in = new ClientInConnection(socket);

        out.start();
        in.start();
    }

    //cleanup the streams by closing down the connections
    private void cleanupStreams() throws InterruptedException {
        out.join();
        in.join();
    }

    //handle the out-connection to the server
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

//            writer.close();
//            scanner.close();
        }
    }

    //handle the in-connection to the server
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

//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
