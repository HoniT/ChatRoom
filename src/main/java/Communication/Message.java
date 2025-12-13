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
        handlers.put(ChatMessage.TYPE, ChatMessage::fromJson);
        handlers.put(JoinRequest.TYPE, JoinRequest::fromJson);
        handlers.put(PrivateMessage.TYPE, PrivateMessage::fromJson);
        handlers.put(ChangeNameRequest.TYPE, ChangeNameRequest::fromJson);
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
    static void sendPacket(ObjectOutputStream outputStream, MessageData messageData) throws IOException {
        if (messageData == null) return;

        Packet packet = new Packet();
        packet.type = messageData.getType();
        packet.data = messageData.toJson();

        String json = GSON.toJson(packet);
        outputStream.writeObject(json);
        outputStream.flush();
    }

    /// Passes data to chatroom for broadcasting/management
    public static void manageServerData(ObjectInputStream inputStream, Socket sender) throws IOException {
        MessageData data = receiveData(inputStream);

        if (data instanceof ChatMessage message) {
            ChatRoom.broadcastMessage(message.getUsername(), message.getPayload(), sender);
            return;
        }
        else if (data instanceof PrivateMessage message) {
            ChatRoom.privateMessage(message.getSourceUsername(), message.getDestUsername(), message.getPayload(), sender);
            return;
        }
        else if (data instanceof ChangeNameRequest(String oldUsername, String newUsername)) {
            ChatRoom.changeName(oldUsername, newUsername);
            return;
        }

        System.out.println("Broadcast error: Invalid message type!");
    }

    //endregion
}
