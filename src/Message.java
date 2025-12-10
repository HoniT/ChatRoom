import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/// API like structure for server - client communication
public class Message {

    // Maps actions to specific handlers (allows multiple actions for server <-> client communication
    static Map<String, Function<JsonObject, JsonObject>> actionHandlers = new HashMap<>();
    static {
        actionHandlers.put("getMessage", Message::getMessage);
    }

    /// Receives general data
    public static JsonObject receiveData(ObjectInputStream inputStream) throws IOException {
        Gson gson = new Gson();

        try {
            Object received = inputStream.readObject();
            if (!(received instanceof String json)) {
                return null;
            }

            Packet packet = gson.fromJson(json, Packet.class);
            if (packet == null || packet.data == null || packet.action == null) {
                return null;
            }

            Function<JsonObject, JsonObject> handler = actionHandlers.get(packet.action);
            if (handler == null) {
                return null;
            }

            return handler.apply(packet.data);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /// Generic packet sender
    public static void sendPacket(ObjectOutputStream outputStream, String action, JsonObject data) throws IOException {
        Gson gson = new Gson();

        Packet packet = new Packet();
        packet.action = action;
        packet.data = data;

        String json = gson.toJson(packet);

        outputStream.writeObject(json);
        outputStream.flush();
    }

    /// Receives a general chat message
    public static JsonObject getMessage(JsonObject jsonObject) {
        JsonObject response = new JsonObject();

        if (jsonObject == null) return response;

        if (jsonObject.has("username"))
            response.addProperty("username", jsonObject.get("username").getAsString());

        if (jsonObject.has("time"))
            response.addProperty("time", jsonObject.get("time").getAsString());

        if (jsonObject.has("payload"))
            response.addProperty("payload", jsonObject.get("payload").getAsString());

        return response;
    }

    /// Turns message JSON into a unified string
    public static String getUnifiedMessage(JsonObject jsonObject) {
        String username = jsonObject.has("username") ? jsonObject.get("username").getAsString() : "";
        String time = jsonObject.has("time") ? jsonObject.get("time").getAsString() : "";
        String payload = jsonObject.has("payload") ? jsonObject.get("payload").getAsString() : "";
        return (username + " " + time + "> " + payload);
    }

    /// Send a general chat message
    public static void sendMessage(ObjectOutputStream outputStream, String username, String payload) throws IOException {
        if(username == null || payload == null) return;

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        data.addProperty("time", time);
        data.addProperty("payload", payload);

        sendPacket(outputStream, "getMessage", data);
    }

    /// Passes data to chatroom for broadcasting
    public static void broadcastMessage(ObjectInputStream inputStream, Socket sender) throws IOException {
        JsonObject jsonObject = receiveData(inputStream);
        if (jsonObject == null) {
            System.out.println("Broadcast error: Couldn't receive JSON!");
            return;
        }

        if (!jsonObject.has("username") || !jsonObject.has("payload")) {
            System.out.println("Broadcast error: Invalid JSON!");
            return;
        }

        String username = jsonObject.get("username").getAsString();
        String payload = jsonObject.get("payload").getAsString();

        ChatRoom.broadcastMessage(username, payload, sender);
    }

    /// Class for sending and receiving data though JSON
    static class Packet {
        String action;
        JsonObject data;
    }
}
