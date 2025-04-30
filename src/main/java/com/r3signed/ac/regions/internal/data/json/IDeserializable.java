package com.r3signed.ac.regions.internal.data.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public interface IDeserializable<T> {
    /**
     * @param json The JSON object to deserialize
     */
    T fromJson(JsonObject json) throws JsonParseException;
}
