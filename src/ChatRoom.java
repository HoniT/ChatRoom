import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/// Chatroom server
public class ChatRoom {
    private static final String SERVER_NAME = "SERVER MESSAGE";
    private static final String WELCOME_MESSAGE = "Welcome to the chatroom ";

    private static ServerSocket serverSocket;

    private static final List<ClientConnection> clientConnections = Collections.synchronizedList(new ArrayList<>());
    private static final List<SocketReader> readerThreads = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws InterruptedException {
        // Getting port, then starting server
        int port = getServerPort(new Scanner(System.in));
        startServer(port);

        // Starting user accept thread
        Thread userAcceptThread = new Thread(new AcceptUsers());
        userAcceptThread.start();

        userAcceptThread.join();
        for(SocketReader sr : readerThreads) sr.join();
    }

    /// Broadcasts message to every user except sender
    public static synchronized void broadcastMessage(String username, String payload, Socket sender) {
        List<ClientConnection> disconnectedClients = new ArrayList<>();

        for (ClientConnection connection : clientConnections) {
            try {
                if(connection.socket == sender) continue;
                Message.sendMessage(connection.outputStream, username, payload);
            } catch (IOException e) {
                // Mark for removal if client disconnected
                disconnectedClients.add(connection);
            }
        }

        // Remove disconnected clients
        clientConnections.removeAll(disconnectedClients);
    }

    /// Endlessly waits for a valid port to be provided via scanner
    private static int getServerPort(Scanner scanner) {
        while(true) {
            System.out.print("Enter a port for the server: ");
            if(!scanner.hasNextInt()) {
                System.out.println("Please provide a port within the range: 1025-65535");
                scanner.nextLine();
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

    /// Starts the server on localhost:<port>
    private static void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Info: Server started on localhost:" + port);
        } catch (IOException e) {
            System.out.println("Couldn't start server do to IOException: " + e.getMessage());
        }
    }

    /// Endlessly accepts users. Runs on separate thread!
    static class AcceptUsers implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    // Saving username
                    String username = Message.getUsername(Objects.requireNonNull(Message.receiveData(new ObjectInputStream(socket.getInputStream()))));

                    // Saving output stream for later broadcasting
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ClientConnection connection = new ClientConnection(socket, objectOutputStream);
                    synchronized (clientConnections) {
                        clientConnections.add(connection);
                    }

                    // Sending welcome message to new user
                    Message.sendMessage(objectOutputStream, SERVER_NAME, WELCOME_MESSAGE + username);
                    // Broadcasting join message to other users
                    broadcastMessage(SERVER_NAME, username + " just joined, say hi!", socket);

                    // Only starting reader thread
                    SocketReader reader = new SocketReader(socket, true);
                    readerThreads.add(reader);
                    reader.start();
                } catch (IOException e) {
                    System.out.println("Couldn't accept user do to an IOException: " + e.getMessage());
                }
            }
        }
    }

    record ClientConnection(Socket socket, ObjectOutputStream outputStream) { }
}