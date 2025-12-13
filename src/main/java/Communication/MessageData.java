package Communication;

import com.google.gson.JsonObject;

public interface MessageData {
    String getType();
    JsonObject toJson();
}
