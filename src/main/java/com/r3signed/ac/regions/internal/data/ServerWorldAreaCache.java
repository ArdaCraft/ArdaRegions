package com.r3signed.ac.regions.internal.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.internal.data.json.Json;
import com.r3signed.ac.regions.utils.WorldUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ServerWorldAreaCache extends WorldAreaCacheImpl {
    public ServerWorldAreaCache(World world) {
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

            JsonObject json = new JsonObject();
            json.add("areas", areaData);
            Json.saveFile(saveFile, json);
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

            JsonArray areaData = json.getAsJsonArray("areas");
            for (int i = 0; i < areaData.size(); i++) {
                JsonObject areaJson = areaData.get(i).getAsJsonObject();
                Area area = Area.from(areaJson);
                if (area != null) {
                    areas.add(area);
                }
            }
        }

        return areas;
    }

    private @Nullable File getSaveFile() {
        File worldFolder = WorldUtils.getFolder(world);
        if (worldFolder == null) {
            return null;
        }
        return new File(worldFolder, "ac_areas.json");
    }
}
