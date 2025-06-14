package com.r3signed.ac.regions.core.areas;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.r3signed.ac.regions.internal.command.CommandRegistration;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;

public enum AreaType implements StringIdentifiable {
    CUBOID(builder -> builder.then(argument("pointA", Vec3ArgumentType.vec3())
            .then(argument("pointB", Vec3ArgumentType.vec3())
                    .then(argument("shouldSync", BoolArgumentType.bool())
                            .executes(CommandRegistration::createCuboid))
            )
    )),
    POLYGON(builder -> builder.executes(CommandRegistration::createPolygon));

    private final Consumer<LiteralArgumentBuilder<ServerCommandSource>> commandSupplier;

    AreaType(Consumer<LiteralArgumentBuilder<ServerCommandSource>> supplier) {
        this.commandSupplier = supplier;
    }

    @Override
    public String asString() {
        return this.name();
    }

    public LiteralArgumentBuilder<ServerCommandSource> appendToCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        this.commandSupplier.accept(builder);
        return builder;
    }
}
