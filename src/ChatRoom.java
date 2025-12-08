import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ChatRoom {
    public static final String SERVER_NAME = "CHATROOM_SERVER";
    private static final String WELCOME_MESSAGE = "Welcome to the chatroom!";

    // List to keep all outputs
    private static final List<ObjectOutputStream> clientOutputStreams = Collections.synchronizedList(new ArrayList<>());

    // Server info
    private static String ip;
    public static String getIp() { return ip; }
    private static int port;
    public static int getPort() { return port; }

    public static void main(String[] args) throws InterruptedException {
        ServerSocket serverSocket = startServer();
        ip = serverSocket.getInetAddress().toString();
        port = serverSocket.getLocalPort();
        System.out.println("Info: Chatroom server started at " + ip + ":" + port);

        // We will accept users endlessly, so we'll start a thread
        Thread acceptUserThread = new Thread(() -> {
           while(true) acceptUser(serverSocket);
        });
        acceptUserThread.start();

        acceptUserThread.join();
    }

    /// Broadcasts message to every user except sender
    public static synchronized void broadcastMessage(Message message, ObjectOutputStream userOutput) {
        List<ObjectOutputStream> disconnectedClients = new ArrayList<>();

        for (ObjectOutputStream clientOutput : clientOutputStreams) {
            if(clientOutput == userOutput) continue;
            try {
                Message.sendMessage(clientOutput, message);
            } catch (Exception e) {
                // Mark for removal if client disconnected
                disconnectedClients.add(clientOutput);
            }
        }

        // Remove disconnected clients
        clientOutputStreams.removeAll(disconnectedClients);
    }

    /// Accepts a user into the chatroom
    private static void acceptUser(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            // Add client to the list
            synchronized (clientOutputStreams) {
                clientOutputStreams.add(output);
            }

            Message.sendMessage(output, new Message(SERVER_NAME, WELCOME_MESSAGE));

            // Only start a reader thread - server just receives and broadcasts
            Thread readerThread = new SocketReader(socket, true, output);
            readerThread.start();

            // Broadcasting that a new user joined
            broadcastMessage(new Message(SERVER_NAME, "A new user joined!"), output);

        } catch (IOException e) {
            System.out.println("IOException in server while accepting user!");
        }

        System.out.println("Info: User joined the chatroom.");
    }

    /// Asks for a port and starts the server
    private static ServerSocket startServer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("----- Chatroom Server Config -----");

        int port;
        // Getting valid server port
        while(true) {
            System.out.print("Enter a port for the server: ");

            if(!scanner.hasNextInt()) {
                System.out.println("Invalid port number! Please enter a port within the range 1025-65535");
                scanner.next();
                continue;
            }
            port = scanner.nextInt();

            if(port < 1025 || port > 65535) {
                System.out.println("Invalid port number! Please enter a port within the range 1025-65535");
                continue;
            }

            // Creating the server socket
            try {
                return new ServerSocket(port);
            } catch (IOException e) {
                System.out.println("IOException in server!");
            }
        }
    }
}
