package Communication;

import Server.ChatRoom;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/// API like structure for server - client communication
public class Message {
    private static final Gson GSON = new Gson();
    private static final Map<String, Function<JsonObject, MessageData>> dataHandlers = createDataHandlers();

    private static Map<String, Function<JsonObject, MessageData>> createDataHandlers() {
        Map<String, Function<JsonObject, MessageData>> handlers = new HashMap<>();
        handlers.put("message", ChatMessage::fromJson);
        handlers.put("joining", JoinRequest::fromJson);
        handlers.put("private_message", PrivateMessage::fromJson);
        return handlers;
    }

    //region Receive/Read

    /// Receives general data
    public static MessageData receiveData(ObjectInputStream inputStream) throws IOException {
        try {
            Object received = inputStream.readObject();
            if (!(received instanceof String)) {
                return null;
            }

            Packet packet = GSON.fromJson((String) received, Packet.class);
            if (packet == null || packet.data == null || packet.type == null) {
                return null;
            }

            Function<JsonObject, MessageData> handler = dataHandlers.get(packet.type);
            return handler != null ? handler.apply(packet.data) : null;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //endregion
    //region Send/Write

    /// Generic packet sender
    public static void sendPacket(ObjectOutputStream outputStream, MessageData messageData) throws IOException {
        if (messageData == null) return;

        Packet packet = new Packet();
        packet.type = messageData.getType();
        packet.data = messageData.toJson();

        String json = GSON.toJson(packet);
        outputStream.writeObject(json);
        outputStream.flush();
    }

    /// Sends a general chat message
    public static void sendMessage(ObjectOutputStream outputStream, String username, String payload) throws IOException {
        if (username == null || payload == null) return;

        ChatMessage message = new ChatMessage(username, payload);
        sendPacket(outputStream, message);
    }

    /// Sends join request to server through outputStream
    public static void sendJoinRequest(ObjectOutputStream outputStream, String username) throws IOException {
        if (username == null) return;

        JoinRequest request = new JoinRequest(username);
        sendPacket(outputStream, request);
    }

    /// Sends a private message
    public static void sendPrivateMessage(ObjectOutputStream outputStream, String username, String destUsername, String payload) throws IOException {
        if (username == null || destUsername == null || payload == null) return;

        PrivateMessage message = new PrivateMessage(username, destUsername, payload);
        sendPacket(outputStream, message);
    }

    /// Passes data to chatroom for broadcasting
    public static void broadcastMessage(ObjectInputStream inputStream, Socket sender) throws IOException {
        MessageData data = receiveData(inputStream);

        if (data instanceof ChatMessage message) {
            ChatRoom.broadcastMessage(message.getUsername(), message.getPayload(), sender);
            return;
        }
        else if (data instanceof PrivateMessage message) {
            ChatRoom.privateMessage(message.getSourceUsername(), message.getDestUsername(), message.getPayload(), sender);
            return;
        }

        System.out.println("Broadcast error: Invalid message type!");
    }

    //endregion
}
