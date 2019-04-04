package club;

import com.google.gson.*;

import java.util.*;

public final class JSONTemplate {
  private JsonObject json = new JsonObject();

  private JSONTemplate() {}

  public static JSONTemplate create() {
    return new JSONTemplate();
  }

  public JSONTemplate add(String propertyName, String value) {
    json.addProperty(propertyName, value);
    return this;
  }

  public JSONTemplate add(String propertyName, Number value) {
    json.addProperty(propertyName, value);
    return this;
  }

  public JSONTemplate add(String propertyName, Boolean value) {
    json.addProperty(propertyName, value);
    return this;
  }

  public JSONTemplate add(String propertyName, Character value) {
    json.addProperty(propertyName, value);
    return this;
  }

  public JSONTemplate add(String propertyName, String... list) {
    JsonArray arr = new JsonArray();
    Arrays.asList(list).stream().forEach(l -> arr.add(new JsonPrimitive(l)));
    json.add(propertyName, arr);
    return this;
  }

  public JsonObject getJson() {
    return json;
  }

  public static JsonObject fromString(String json) {
    return new Gson().fromJson(json, JsonObject.class);
  }

  @Override
  public String toString() {

    return getJson().toString();
  }
}
