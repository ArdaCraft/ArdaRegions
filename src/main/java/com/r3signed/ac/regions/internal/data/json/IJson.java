package com.r3signed.ac.regions.internal.data.json;

import com.google.gson.JsonElement;

public interface IJson<T extends JsonElement, U> extends ISerializable<T>, IDeserializable<U> {}
