package com.r3signed.ac.regions.core.item;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.core.ServerServices;
import com.r3signed.ac.regions.core.areas.PolygonArea;
import com.r3signed.ac.regions.internal.network.packet.AddPolygonPacket;
import com.r3signed.ac.regions.utils.NbtKeys;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PolygonDebugItem extends AbstractRegionDebugItem<PolygonArea> {

    @Override
    public Optional<PolygonArea> getRegion(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NbtKeys.POINTS)) {
            return Optional.empty();
        }
        PolygonArea area = new PolygonArea();
        area.setPoints(getPoints(stack));
        return Optional.of(area);
    }

    @Override
    public void finishRegion(ItemStack stack, World world, @Nullable ServerPlayerEntity syncTarget) {
        if (world.isClient()) {
            return;
        }
        this.getRegion(stack).ifPresent(polygonArea -> {
            WorldAreaCache cache = ServerServices.AREAS.getCache(world);
            if (cache == null) {
                ArdaRegions.LOGGER.error("World not found in cache");
            } else {
                cache.add(polygonArea);
                if (syncTarget != null) {
                    ServerPlayNetworking.send(syncTarget, new AddPolygonPacket(polygonArea));
                    syncTarget.sendMessage(Text.translatable("info.arda-regions.polygon.save"), false);
                }
            }
        });
        if (stack.getNbt() != null && stack.getNbt().contains(NbtKeys.POINTS)) {
            stack.getNbt().remove(NbtKeys.POINTS);
        }
    }
}
