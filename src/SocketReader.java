import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;

public class SocketReader extends Thread {
    private ObjectInputStream inputStream;

    private final boolean isServer;
    private final Socket sender;

    public SocketReader(Socket socket) {
        this(socket, false);
    }

    public SocketReader(Socket socket, boolean isServer) {
        try {
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Couldn't start reading! IOException: " + e.getMessage());
        }
        this.isServer = isServer;
        this.sender = socket;
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Printing received message for clients
                if(!isServer) System.out.println(Message.getUnifiedMessage(Objects.requireNonNull(Message.receiveData(inputStream))));
                // If the server received a message, we'll broadcast it to every other user
                else Message.broadcastMessage(inputStream, sender);
            } catch (IOException e) {
                if(!isServer) {
                    System.out.println("Lost connection to serve do to " + e.getMessage());
                    // Try to reconnect
                }
                else System.out.println("Client lost connection: " + e.getMessage());
                break;
            }
        }
    }
}
