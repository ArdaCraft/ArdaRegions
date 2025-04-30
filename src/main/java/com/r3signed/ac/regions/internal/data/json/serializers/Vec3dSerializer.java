package com.r3signed.ac.regions.internal.data.json.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Type;

public class Vec3dSerializer implements JsonSerializer<Vec3d>, JsonDeserializer<Vec3d> {
    @Override
    public Vec3d deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            double x = json.get("x").getAsDouble();
            double y = json.get("y").getAsDouble();
            double z = json.get("z").getAsDouble();

            return new Vec3d(x, y, z);
        } else {
            throw new JsonParseException("Invalid Vec3d format");
        }
    }

    @Override
    public JsonElement serialize(Vec3d vec3d, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty("x", vec3d.x);
        json.addProperty("y", vec3d.y);
        json.addProperty("z", vec3d.z);

        return json;
    }
}
