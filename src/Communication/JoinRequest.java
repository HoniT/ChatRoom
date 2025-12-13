package Communication;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class JoinRequest implements MessageData {
    static final String TYPE = "joining";

    private final String username;

    public JoinRequest(String username) {
        this.username = username;
    }

    public static JoinRequest fromJson(JsonObject json) {
        if (json == null) return null;

        String username = json.has("username") ? json.get("username").getAsString() : "";
        return new JoinRequest(username);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        return json;
    }

    public String getUsername() {
        return username;
    }

    /// Sends join request to server through outputStream
    public static void sendJoinRequest(ObjectOutputStream outputStream, String username) throws IOException {
        if (username == null) return;

        JoinRequest request = new JoinRequest(username);
        Message.sendPacket(outputStream, request);
    }
}