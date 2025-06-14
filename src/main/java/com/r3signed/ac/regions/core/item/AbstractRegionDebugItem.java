package com.r3signed.ac.regions.core.item;

import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.utils.NbtKeys;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRegionDebugItem<T extends Area> extends Item implements AreaDebugging<T> {
    public AbstractRegionDebugItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound nbt = stack.getNbt();
        boolean wasSelected = nbt != null && nbt.contains(NbtKeys.SELECTED_BUFFER) && nbt.getBoolean(NbtKeys.SELECTED_BUFFER);

        if (wasSelected != selected) {
            finishRegion(stack, world, entity instanceof ServerPlayerEntity player ? player : null);
            stack.getOrCreateNbt().putBoolean(NbtKeys.SELECTED_BUFFER, selected);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        for (int i = 0; i < 4; i++ ) {
            tooltip.add(Text.translatable("tooltip.arda-regions.debug_tool.line" + i));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            ItemStack stack = user.getStackInHand(hand);
            if (user.isSneaking()) {
                finishRegion(stack, world, player);
            } else {
                addPoint(stack, world, player, user.getPos());
            }
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isSneaking()) {
            return ActionResult.PASS;
        }
        if (!context.getWorld().isClient()) {
            Vec3d pos = context.getHitPos();
            addPoint(context.getStack(), context.getWorld(), player, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void addPoint(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... points) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList pointsNbt = nbt.contains(NbtKeys.POINTS) ? nbt.getList(NbtKeys.POINTS, NbtElement.COMPOUND_TYPE) : new NbtList();
        for (Vec3d point : points) {
            NbtCompound pointNbt = new NbtCompound();
            pointNbt.putDouble("x", point.x);
            pointNbt.putDouble("y", point.y);
            pointNbt.putDouble("z", point.z);
            pointsNbt.add(pointNbt);

            if (player != null) {
                player.sendMessage(Text.translatable("info.arda-regions.point.add", point.x, point.y, point.z), false);
            }
        }
        nbt.put(NbtKeys.POINTS, pointsNbt);
    }

    @Override
    public HashSet<Vec3d> getPoints(ItemStack stack) {
        HashSet<Vec3d> points = new HashSet<>();
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NbtKeys.POINTS)) {
            return points;
        }
        NbtList pointsNbt = nbt.getList(NbtKeys.POINTS, NbtElement.COMPOUND_TYPE);
        pointsNbt.stream().map(nbtElement -> (NbtCompound) nbtElement).forEach(pointNbt -> {
            Vec3d point = new Vec3d(pointNbt.getDouble("x"), pointNbt.getDouble("y"), pointNbt.getDouble("z"));
            points.add(point);
        });
        return points;
    }

    @Override
    public abstract Optional<T> getRegion(ItemStack stack);

    @Override
    public abstract void finishRegion(ItemStack stack, World world, @Nullable ServerPlayerEntity syncTarget);
}
