package Communication;

import com.google.gson.JsonObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PrivateMessage implements MessageData {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TYPE = "private_message";

    private final String sourceUsername;
    private final String destUsername;
    private final String time;
    private final String payload;

    public PrivateMessage(String sourceUsername, String destUsername, String payload) {
        this.sourceUsername = sourceUsername;
        this.destUsername = destUsername;
        this.time = LocalTime.now().format(TIME_FORMATTER);
        this.payload = payload;
    }

    public PrivateMessage(String sourceUsername, String destUsername, String time, String payload) {
        this.sourceUsername = sourceUsername;
        this.destUsername = destUsername;
        this.time = time;
        this.payload = payload;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("sourceUsername", sourceUsername);
        json.addProperty("destUsername", destUsername);
        json.addProperty("time", time);
        json.addProperty("payload", payload);
        return json;
    }

    public static PrivateMessage fromJson(JsonObject json) {
        if (json == null) return null;

        String sourceUsername = json.has("sourceUsername") ? json.get("sourceUsername").getAsString() : "";
        String destUsername = json.has("destUsername") ? json.get("destUsername").getAsString() : "";
        String time = json.has("time") ? json.get("time").getAsString() : "";
        String payload = json.has("payload") ? json.get("payload").getAsString() : "";

        return new PrivateMessage(sourceUsername, destUsername, time, payload);
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public String getDestUsername() {
        return destUsername;
    }

    public String getPayload() {
        return payload;
    }

    public String toUnifiedString() {
        return sourceUsername + " " + time + " PRIVATE_CHAT> " + payload;
    }
}
