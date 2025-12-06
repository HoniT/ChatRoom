import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Message {
    public static void receiveMessage(ObjectInputStream input) {
        try {
            String message = input.readUTF();
            System.out.println(message);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void sendMessage(ObjectOutputStream output, Message message) {
        try {
            output.writeUTF(message.createMessage());
            output.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private final String username;
    private final String messageTime;
    private final String payload;

    private final ObjectOutputStream outputStream;

    public Message(String username, String messageTime, String payload) {
        this.payload = payload;
        this.messageTime = messageTime;
        this.username = username;
        this.outputStream = null;
    }

    public Message(String username, String messageTime, String payload, ObjectOutputStream outputStream) {
        this.payload = payload;
        this.messageTime = messageTime;
        this.username = username;
        this.outputStream = outputStream;
    }

    /// Converts a whole unified text into a message object
    public Message(String message) {
        // Message format: "username HH:mm> payload"
        String[] parts = message.split(" ", 2);
        if (parts.length >= 2) {
            String usernamePart = parts[0];
            String rest = parts[1];
            String[] timeParts = rest.split("> ", 2);
            if (timeParts.length >= 2) {
                String time = timeParts[0];
                String payload = timeParts[1];

                this.username = usernamePart;
                this.messageTime = time;
                this.payload = payload;
                this.outputStream = null;
                return;
            }
        }

        // If it made it to here place filler values
        this.username = "USER_NOT_FOUND";
        this.messageTime = "";
        this.payload = "";
        this.outputStream = null;
    }

    /// Creates a unified message with metadata + payload for server-user communication
    public String createMessage() {
        return username + " " + messageTime + "> " + payload;
    }
}
