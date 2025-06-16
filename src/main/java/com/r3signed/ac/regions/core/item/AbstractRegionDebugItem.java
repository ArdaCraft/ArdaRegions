package com.r3signed.ac.regions.core.item;

import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.utils.NbtKeys;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public abstract class AbstractRegionDebugItem<T extends Area> extends Item implements AreaDebugging {
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
        Mode mode = Mode.getMode(stack);
        if (mode != null) {
            tooltip.add(Text.translatable("tooltip.arda-regions.debug_tool.mode", mode.asString().toUpperCase(Locale.ROOT)));
        }
        if (Screen.hasShiftDown()) {
            for (int i = 0; i < 2; i++) {
                tooltip.add(Text.translatable("tooltip.arda-regions.debug_tool.line" + i));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        useItemStack(stack, user, world, user.getPos());
        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        useItemStack(context.getStack(), player, context.getWorld(), context.getHitPos());
        return ActionResult.SUCCESS;
    }

    protected void useItemStack(ItemStack stack, @Nullable PlayerEntity user, World world, Vec3d pos) {
        if (user == null) {
            return;
        }
        if (stack.getNbt() == null || !stack.getNbt().contains(NbtKeys.MODE)) {
            Mode.setMode(stack, Mode.ADD, user);
        }
        if (user.isSneaking()) {
            Mode.next(stack, Mode.getMode(stack), user);
        } else {
            Mode mode = Mode.getMode(stack);
            if (mode != null) {
                switch (mode) {
                    case ADD -> addPointToStack(stack, world, user, pos);
                    case REMOVE -> removePointFromStack(stack, world, user, pos);
                    case SAVE -> {
                        finishRegion(stack, world, user instanceof ServerPlayerEntity player ? player : null);
                        Mode.setMode(stack, Mode.ADD, user);
                    }
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    public void cooldown(PlayerEntity player, int ticks) {
        player.getItemCooldownManager().set(this, ticks);
    }

    public void addPointToStack(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... points) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList pointsNbt = nbt.contains(NbtKeys.POINTS) ? nbt.getList(NbtKeys.POINTS, NbtElement.COMPOUND_TYPE) : new NbtList();
        for (Vec3d point : points) {
            NbtCompound pointNbt = new NbtCompound();
            pointNbt.putDouble("x", point.x);
            pointNbt.putDouble("y", point.y);
            pointNbt.putDouble("z", point.z);
            pointsNbt.add(pointNbt);

            if (player != null && !world.isClient()) {
                player.sendMessage(Text.translatable("info.arda-regions.point.add", point.x, point.y, point.z), false);
            }
        }
        nbt.put(NbtKeys.POINTS, pointsNbt);
    }

    public void removePointFromStack(ItemStack stack, World world, @Nullable PlayerEntity player, Vec3d... requestedPoints) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NbtKeys.POINTS)) return;
        List<Vec3d> storedPoints = getPoints(stack);
        Vec3d closestStoredPoint = null;
        double maxDistance = 15;

        for (Vec3d requestedPoint : requestedPoints) {
            for (Vec3d storedPoint : storedPoints) {
                if (requestedPoint.distanceTo(storedPoint) > maxDistance) {
                    continue;
                }
                if (closestStoredPoint == null) {
                    closestStoredPoint = storedPoint;
                    continue;
                }
                if (requestedPoint.distanceTo(closestStoredPoint) > requestedPoint.distanceTo(storedPoint)) {
                    closestStoredPoint = storedPoint;
                }
            }
        }
        if (closestStoredPoint == null) return;
        nbt.getList(NbtKeys.POINTS, NbtElement.COMPOUND_TYPE).remove(storedPoints.indexOf(closestStoredPoint));
    }

    public List<Vec3d> getPoints(ItemStack stack) {
        List<Vec3d> points = new ArrayList<>();
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NbtKeys.POINTS)) {
            return points;
        }
        NbtList pointsNbt = nbt.getList(NbtKeys.POINTS, NbtElement.COMPOUND_TYPE);
        for (NbtElement nbtElement : pointsNbt) {
            NbtCompound pointNbt = (NbtCompound) nbtElement;
            Vec3d point = new Vec3d(pointNbt.getDouble("x"), pointNbt.getDouble("y"), pointNbt.getDouble("z"));
            points.add(point);
        }
        return points;
    }

    public abstract Optional<T> getRegion(ItemStack stack);

    public abstract void finishRegion(ItemStack stack, World world, @Nullable ServerPlayerEntity syncTarget);

    public enum Mode implements StringIdentifiable {
        ADD,
        REMOVE,
        SAVE;

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }

        @Nullable
        public static Mode fromString(String name) {
            for (Mode mode : Mode.values()) {
                if (mode.asString().equals(name)) return mode;
            }
            return null;
        }

        @Nullable
        public static Mode getMode(ItemStack stack) {
            NbtCompound nbt = stack.getNbt();
            if (nbt == null || !nbt.contains(NbtKeys.MODE)) return null;
            return fromString(nbt.getString(NbtKeys.MODE));
        }

        /**
         * Sets the new Debug Mode on an ItemStack
         *
         * @param stack  Stack, which will be modified
         * @param mode   new Mode for the ItemStack
         * @param player if not null, will display info message for them and set the item on short cooldown
         */
        public static void setMode(ItemStack stack, Mode mode, @Nullable PlayerEntity player) {
            stack.getOrCreateNbt().putString(NbtKeys.MODE, mode.asString());
            if (player != null && !player.getWorld().isClient()) {
                player.sendMessage(Text.translatable("info.arda-regions.debug_tool.mode", mode.asString().toUpperCase(Locale.ROOT)), true);
                if (stack.getItem() instanceof AbstractRegionDebugItem<?> debugItem) {
                    debugItem.cooldown(player, 5);
                }
            }
        }

        /**
         * @param printStack use null if you don't want to print the next mode to a specific ItemStack
         * @return next entry in the {@link Mode} enum
         */
        @SuppressWarnings("UnusedReturnValue")
        public static Mode next(@Nullable ItemStack printStack, @Nullable Mode mode, @Nullable PlayerEntity player) {
            int newOrdinal = mode == null ? 0 : mode.ordinal() + 1;
            if (newOrdinal >= Mode.values().length) {
                newOrdinal = 0;
            }
            Mode newMode = Mode.values()[newOrdinal];
            if (printStack != null) {
                setMode(printStack, newMode, player);
            }
            return newMode;
        }
    }
}
