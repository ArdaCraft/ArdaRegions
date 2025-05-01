package com.r3signed.ac.regions.client.internal.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.internal.data.WorldAreaCacheImpl;
import com.r3signed.ac.regions.internal.data.json.Json;
import com.r3signed.ac.regions.utils.ClientUtils;
import com.r3signed.ac.regions.utils.WorldUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClientWorldAreaCache extends WorldAreaCacheImpl {
    public ClientWorldAreaCache(World world) {
        super(world);
    }

    @Override
    protected void save() {
        File saveFile = getSaveFile();

        if (saveFile != null) {
            JsonArray areaData = new JsonArray();
            for (Area area : areas) {
                areaData.add(area.toJson());
            }

            if (ClientUtils.isSinglePlayer()) {
                JsonObject json = new JsonObject();
                json.add("areas", areaData);
                Json.saveFile(saveFile, json);
            } else {
                String worldKey = WorldUtils.getKey(world);
                JsonObject json = Json.loadFile(saveFile);
                JsonObject areasJson = json.has("areas") ? json.getAsJsonObject("areas") : new JsonObject();
                areasJson.add(worldKey, areaData);
                json.add("areas", areasJson);
                Json.saveFile(saveFile, json);
            }
        }
    }

    @Override
    protected Set<Area> load() {
        Set<Area> areas = new HashSet<>();
        File saveFile = getSaveFile();

        if (saveFile != null) {
            JsonObject json = Json.loadFile(saveFile);
            if (!json.has("areas")) {
                return areas;
            }

            if (ClientUtils.isSinglePlayer()) {
                JsonArray areaData = json.getAsJsonArray("areas");
                for (int i = 0; i < areaData.size(); i++) {
                    Area area = Area.from(areaData.get(i).getAsJsonObject());
                    if (area != null) {
                        areas.add(area);
                    }
                }
            } else {
                String worldKey = WorldUtils.getKey(world);
                JsonObject areasJson = json.getAsJsonObject("areas");
                if (areasJson.has(worldKey)) {
                    JsonArray areaData = areasJson.getAsJsonArray(worldKey);
                    for (int i = 0; i < areaData.size(); i++) {
                        Area area = Area.from(areaData.get(i).getAsJsonObject());
                        if (area != null) {
                            areas.add(area);
                        }
                    }
                }
            }
        }

        return areas;
    }

    private @Nullable File getSaveFile() {
        File saveFile = null;

        if (ClientUtils.isSinglePlayer()) {
            File worldFolder = WorldUtils.getFolder(world);
            if (worldFolder != null) {
                saveFile = new File(worldFolder, "ac_areas.json");
            }
        } else {
            String host = ClientUtils.getHostAddress();
            if (host != null) {
                saveFile = new File(ArdaRegions.MOD_DIR, host + "/data.json");
            }
        }

        if (saveFile != null) {
            File parentFile = saveFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                return null;
            } else {
                return saveFile;
            }
        }

        return null;
    }
}
