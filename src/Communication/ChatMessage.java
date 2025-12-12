package Communication;

import com.google.gson.JsonObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage implements MessageData {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TYPE = "message";

    private final String username;
    private final String time;
    private final String payload;

    public ChatMessage(String username, String payload) {
        this.username = username;
        this.time = LocalTime.now().format(TIME_FORMATTER);
        this.payload = payload;
    }

    public ChatMessage(String username, String time, String payload) {
        this.username = username;
        this.time = time;
        this.payload = payload;
    }

    public static ChatMessage fromJson(JsonObject json) {
        if (json == null) return null;

        String username = json.has("username") ? json.get("username").getAsString() : "";
        String time = json.has("time") ? json.get("time").getAsString() : "";
        String payload = json.has("payload") ? json.get("payload").getAsString() : "";

        return new ChatMessage(username, time, payload);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("time", time);
        json.addProperty("payload", payload);
        return json;
    }

    public String getUsername() {
        return username;
    }

    public String getPayload() {
        return payload;
    }

    public String toUnifiedString() {
        return username + " " + time + "> " + payload;
    }
}