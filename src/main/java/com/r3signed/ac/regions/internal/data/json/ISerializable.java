package com.r3signed.ac.regions.internal.data.json;

import com.google.gson.JsonElement;

public interface ISerializable<T extends JsonElement> {
    /**
     * @return The serialized string of the object
     */
    T toJson();
}
