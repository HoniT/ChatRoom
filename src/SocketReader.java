import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketReader extends Thread {
    private final Socket socket;
    private final ObjectInputStream input;
    private final boolean isServer;

    private final ObjectOutputStream userOutput; // User output that sends data

    public SocketReader(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new ObjectInputStream(socket.getInputStream());
        this.isServer = false;
        this.userOutput = null;
    }

    public SocketReader(Socket socket, boolean isServer, ObjectOutputStream userOutput) throws IOException {
        this.socket = socket;
        this.input = new ObjectInputStream(socket.getInputStream());
        this.isServer = isServer;
        this.userOutput = userOutput;
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (isServer) {
                    // Server side: parse the message and broadcast to all clients
                    String message = input.readUTF();
                    ChatRoom.broadcastMessage(new Message(message), userOutput);
                } else {
                    // Client side: just display the message
                    Message.receiveMessage(input);
                }
            } catch (IOException e) {
                System.out.println("Connection lost!");
                // Trying to reconnect
                ChatUser.TryReconnect(socket);

                break;
            }
        }
    }
}
