package com.r3signed.ac.regions.core.init;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.core.item.CuboidDebugItem;
import com.r3signed.ac.regions.core.item.PolygonDebugItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ArdaRegionsItems {
    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static final PolygonDebugItem DEBUG_POLYGON = register("debug_polygon", new PolygonDebugItem());
    public static final CuboidDebugItem DEBUG_CUBOID = register("debug_cuboid", new CuboidDebugItem());

    private static <T extends Item> T register(String name, T entry) {
        T registeredEntry = Registry.register(Registries.ITEM, ArdaRegions.getId(name), entry);
        ALL_ITEMS.add(registeredEntry);
        return registeredEntry;
    }

    public static void init() {
        // static initialisation
    }
}
