package com.r3signed.ac.regions.core.init;

import com.r3signed.ac.regions.ArdaRegions;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class ArdaRegionsItemGroups {
    public static final RegistryKey<ItemGroup> DEBUG = register("debug",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(Items.STICK))
                    .displayName(Text.translatable("itemGroup.arda-regions.debug"))
                    .build());

    static {
        ItemGroupEvents.modifyEntriesEvent(DEBUG).register(entries -> {
            for (Item registeredItem : ArdaRegionsItems.ALL_ITEMS) {
                entries.add(registeredItem);
            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(entries -> {
            for (Item registeredItem : ArdaRegionsItems.ALL_ITEMS) {
                entries.add(registeredItem);
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryKey<ItemGroup> register(String name, ItemGroup group) {
        Registry.register(Registries.ITEM_GROUP, ArdaRegions.getId(name), group);
        return RegistryKey.of(Registries.ITEM_GROUP.getKey(), ArdaRegions.getId(name));
    }

    public static void init() {
        // static initialisation
    }
}
