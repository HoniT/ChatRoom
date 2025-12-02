import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message {
    public static void receiveMessage(DataInputStream input) {
        try {
            String message = input.readUTF();
            System.out.println(message);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void sendMessage(DataOutputStream output, String username, String message) {
        try {
            output.writeUTF(new Message(username, message).createMessage());
            output.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private final String username;
    private final String payload;

    public Message(String payload, String username) {
        this.payload = payload;
        this.username = username;
    }

    /// Creates a unified message with metadata + payload for server-user communication
    public String createMessage() {
        return username + "> " + payload;
    }
}
