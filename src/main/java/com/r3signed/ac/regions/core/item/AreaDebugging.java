package com.r3signed.ac.regions.core.item;

import com.r3signed.ac.regions.core.areas.Area;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface AreaDebugging<T extends Area> {
    void addPointToStack(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... points);

    void removePointFromStack(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... points);

    List<Vec3d> getPoints(ItemStack stack);

    Optional<T> getRegion(ItemStack stack);

    void finishRegion(ItemStack stack, World world, @Nullable ServerPlayerEntity syncTarget);
}
