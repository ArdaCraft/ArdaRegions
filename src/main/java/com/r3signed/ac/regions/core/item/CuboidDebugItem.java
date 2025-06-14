package com.r3signed.ac.regions.core.item;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.core.ServerServices;
import com.r3signed.ac.regions.core.areas.CuboidArea;
import com.r3signed.ac.regions.internal.network.packet.AddCuboidPacket;
import com.r3signed.ac.regions.utils.NbtKeys;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CuboidDebugItem extends AbstractRegionDebugItem<CuboidArea> {

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return super.use(world, user, hand);
        }
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            finishRegion(stack, world, player);
        } else {
            this.addPoint(stack, world, player, user.getPos());
        }
        return TypedActionResult.success(stack);
    }

    @Override
    public void addPoint(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... points) {
        super.addPoint(stack, world, player, points);
        if (getPoints(stack).size() > 1) {
            finishRegion(stack, world, player instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
        }
    }

    @Override
    public Optional<CuboidArea> getRegion(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NbtKeys.POINTS)) {
            return Optional.empty();
        }
        CuboidArea area = new CuboidArea();
        getPoints(stack).forEach(area::setPoint);
        return Optional.of(area);
    }

    @Override
    public void finishRegion(ItemStack stack, World world, @Nullable ServerPlayerEntity syncTarget) {
        if (world.isClient()) {
            return;
        }
        this.getRegion(stack).ifPresent(cuboidArea -> {
            WorldAreaCache cache = ServerServices.AREAS.getCache(world);
            if (cache == null) {
                ArdaRegions.LOGGER.error("World not found in cache");
            } else {
                cache.add(cuboidArea);
                if (syncTarget != null) {
                    ServerPlayNetworking.send(syncTarget, new AddCuboidPacket(cuboidArea));
                    syncTarget.sendMessage(Text.translatable("info.arda-regions.cuboid.save"), false);
                }
            }
        });
        if (stack.getNbt() != null && stack.getNbt().contains(NbtKeys.POINTS)) {
            stack.getNbt().remove(NbtKeys.POINTS);
        }
    }
}
