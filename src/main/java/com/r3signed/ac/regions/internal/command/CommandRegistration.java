package com.r3signed.ac.regions.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.core.ServerServices;
import com.r3signed.ac.regions.core.areas.AreaType;
import com.r3signed.ac.regions.core.areas.CuboidArea;
import com.r3signed.ac.regions.internal.network.packet.AddCuboidPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class CommandRegistration {
    private static final SimpleCommandExceptionType NOT_IMPLEMENTED =
            new SimpleCommandExceptionType(Text.literal("Feature not implemented yet"));
    private static final SimpleCommandExceptionType NOT_INITIALIZED =
            new SimpleCommandExceptionType(Text.literal("Data Structure not initialized yet"));

    public static void init() {
        CommandRegistrationCallback.EVENT.register(CommandRegistration::addRegion);
    }

    private static void addRegion(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess,
                                  CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> rootNode = CommandManager.literal("ardaregions")
                .requires(source -> source.hasPermissionLevel(2));

        LiteralArgumentBuilder<ServerCommandSource> createNode = CommandManager.literal("create");
        for (AreaType type : AreaType.values()) {
            LiteralArgumentBuilder<ServerCommandSource> typeNode = type.appendToCommand(CommandManager.literal(type.name()));
            createNode.then(typeNode);
        }
        rootNode.then(createNode);

        dispatcher.register(rootNode);
    }

    public static int createCuboid(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldAreaCache cache = ServerServices.AREAS.getCache(context.getSource().getWorld());
        if (cache == null) {
            throw NOT_INITIALIZED.create();
        }
        Vec3d pointA = Vec3ArgumentType.getVec3(context, "pointA");
        Vec3d pointB = Vec3ArgumentType.getVec3(context, "pointB");
        CuboidArea area = new CuboidArea();
        area.setPoint(pointA);
        area.setPoint(pointB);
        cache.add(area);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null && BoolArgumentType.getBool(context, "shouldSync")) {
            ServerPlayNetworking.send(player, new AddCuboidPacket(area));
        }
        return 1;
    }

    public static int createPolygon(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        throw NOT_IMPLEMENTED.create();
    }
}
