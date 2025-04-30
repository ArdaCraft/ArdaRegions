package com.r3signed.ac.regions.internal.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.internal.data.json.serializers.Vec3dSerializer;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Json {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Vec3d.class, new Vec3dSerializer())
            .create();

    /**
     * Loads a JSON file and returns the parsed object.
     *
     * @param file The file to load
     * @return The parsed object
     */
    public static JsonObject loadFile(File file) {
        if (!file.exists()) {
            return new JsonObject();
        }

        try (var reader = new FileReader(file)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            ArdaRegions.LOGGER.error("Could not load file {}", file.getAbsolutePath(), e);
            return new JsonObject();
        }
    }

    /**
     * Saves a JSON object to a file.
     *
     * @param file The file to save to
     * @param json The JSON object to save
     */
    public static void saveFile(File file, JsonObject json) {
        try (var writer = new FileWriter(file)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            ArdaRegions.LOGGER.error("Could not save file {}", file.getAbsolutePath(), e);
        }
    }

    public static JsonElement to(Object object) {
        return GSON.toJsonTree(object);
    }

    public static <T> T from(JsonElement element, Class<T> clazz) {
        return GSON.fromJson(element, clazz);
    }
}
