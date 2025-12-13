package User;

import Communication.JoinRequest;
import Communication.SocketReader;
import Communication.SocketWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Chatroom user
public class ChatUser {
    private static String username = "";
    private static SocketWriter socketWriter;
    private static SocketReader socketReader;
    private static Socket socket;

    public static String getUsername() { return username; }

    private static String ip;
    private static int port;

    public static void main(String[] args) throws InterruptedException {
        // Random username
        username = "Anonymous_User_" + new Random().nextInt(0, 1000);

        // Connecting to server
        socket = connectToServer();

        // Starting I/O threads
        socketReader = new SocketReader(socket);
        socketReader.start();
        socketWriter = new SocketWriter(socket, new Scanner(System.in), username);
        socketWriter.start();

        socketWriter.join();
        socketReader.join();
    }

    //region Terminal Functions

    public static void changeUsername(String newName) {
        username = newName;
        socketWriter.updateUsername(username);
    }

    public static void exitChatroom() {
        // Closing connection and interrupting I/O threads
        try {
            socket.close();
            socketReader.interrupt();
            socketWriter.interrupt();
        } catch (IOException e) {
            System.out.println("IOException while exiting chatroom: " + e.getMessage());
        }
    }

    public static void rejoinChatroom() {
        // Stopping old threads
        if(socketReader != null) socketReader.interrupt();
        if(socketWriter != null) socketWriter.interrupt();

        try {
            if(socket != null) socket.close();
            socket = new Socket(ip, port);
            if(!socket.isConnected()) throw new IOException();
            // Sending join info
            JoinRequest.sendJoinRequest(new ObjectOutputStream(socket.getOutputStream()), username);

            // Restart threads for I/O
            socketReader = new SocketReader(socket);
            socketReader.start();
            socketWriter = new SocketWriter(socket, new Scanner(System.in), username);
            socketWriter.start();

        } catch (IOException e) {
            System.out.println("Couldn't reconnect to server do to IOException: " + e.getMessage());
        }
    }

    //endregion

    /// Endlessly waits for a valid IP to be provided via scanner
    private static String getServerIp(Scanner scanner) {
        while(true) {
            System.out.print("Enter server IP: ");
            String ip = scanner.next();
            String ipRegex = "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$";
            Matcher m = Pattern.compile(ipRegex).matcher(ip);
            if (!ip.contentEquals("localhost") && !m.find()) {
                System.out.println("Invalid IP!");
                continue;
            }
            return ip;
        }
    }

    /// Endlessly waits for a valid port to be provided via scanner
    private static int getServerPort(Scanner scanner) {
        while(true) {
            System.out.print("Enter a port for the server: ");
            if(!scanner.hasNextInt()) {
                System.out.println("Please provide a port within the range: 1025-65535");
                scanner.next();
                continue;
            }
            int port = scanner.nextInt();
            if(port < 1025 || port > 65535) {
                System.out.println("Please provide a port within the range: 1025-65535");
                continue;
            }
            return port;
        }
    }

    /// Connects to the chatroom server
    private static Socket connectToServer() {
        while(true) {
            Scanner scanner = new Scanner(System.in);
            // Getting IP and port
            ip = getServerIp(scanner);
            port = getServerPort(scanner);

            // Trying to connect
            try {
                Socket socket = new Socket(ip, port);
                if(!socket.isConnected()) throw new IOException();
                // Sending join info
                JoinRequest.sendJoinRequest(new ObjectOutputStream(socket.getOutputStream()), username);

                return socket;
            } catch (IOException e) {
                System.out.println("Couldn't connect to server do to IOException: " + e.getMessage());
            }
        }
    }
}
