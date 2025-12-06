import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUser {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Creating temporary username for welcome message
        String name = "Anonymous User";

        Socket socket = connectToServer();

        // Starting threads
        Thread writerThread = new SocketWriter(socket, name, new Scanner(System.in));
        Thread readerThread = new SocketReader(socket);

        writerThread.start();
        readerThread.start();
        writerThread.join();
        readerThread.join();
    }

    /// Gets server IP and Port and connects to it
    private static Socket connectToServer() {
        Scanner scanner = new Scanner(System.in);
        Socket socket;

        while(true) {
            // Getting IP
            System.out.print("Enter server IP: ");
            String ip = scanner.next();
            String ipRegex = "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$";
            Matcher m = Pattern.compile(ipRegex).matcher(ip);
            if(!Objects.equals(ip, "localhost") && !m.find()) {
                System.out.println("Invalid IP!");
                continue;
            }

            // Getting port
            System.out.print("Enter serve Port: ");
            int port;
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

            // Connecting
            try {
                socket = new Socket(ip, port);
                if(socket.isConnected()) {
                    System.out.println("Connected to chatroom!");
                    break;
                }
            } catch (IOException e) {
                System.out.println("Couldn't connect to " + ip + ":" + port);
            }
        }

        return socket;
    }
}
