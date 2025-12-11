package Message;

import com.google.gson.JsonObject;

public interface MessageData {
    String getType();
    JsonObject toJson();
}
