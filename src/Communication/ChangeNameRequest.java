package Communication;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.ObjectOutputStream;

public record ChangeNameRequest(String oldUsername, String newUsername) implements MessageData {
    static final String TYPE = "name_request";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("oldUsername", oldUsername);
        json.addProperty("newUsername", newUsername);
        return json;
    }

    public static ChangeNameRequest fromJson(JsonObject json) {
        if (json == null) return null;

        String oldUsername = json.has("oldUsername") ? json.get("oldUsername").getAsString() : "";
        String newUsername = json.has("newUsername") ? json.get("newUsername").getAsString() : "";
        return new ChangeNameRequest(oldUsername, newUsername);
    }

    /// Sends name change request to server
    public static void sendNameChangeRequest(ObjectOutputStream outputStream, String oldUsername, String newUsername) throws IOException {
        if (oldUsername == null || newUsername == null) return;

        ChangeNameRequest request = new ChangeNameRequest(oldUsername, newUsername);
        Message.sendPacket(outputStream, request);
    }
}
