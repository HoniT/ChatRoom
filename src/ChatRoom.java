import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatRoom {
    private static final String SERVER_NAME = "CHATROOM_SERVER";
    private static final String WELCOME_MESSAGE = "Welcome to the chatroom!";


    private static int activeUsers = 0;
    public static int getActiveUsers() { return activeUsers; }

    public static void main(String[] args) throws InterruptedException {
        ServerSocket serverSocket = startServer();
        System.out.println("Info: Chatroom server started.");

        Thread acceptUserThread = new Thread(() -> {
           while(true) acceptUser(serverSocket);
        });

        acceptUserThread.start();

        acceptUserThread.join();
    }

    /// Accepts a user into the chatroom
    private static void acceptUser(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            Message.sendMessage(new DataOutputStream(socket.getOutputStream()), WELCOME_MESSAGE, SERVER_NAME);
        } catch (IOException e) {
            System.out.println("IOException in server while accepting user!");
        }

        activeUsers++;
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
