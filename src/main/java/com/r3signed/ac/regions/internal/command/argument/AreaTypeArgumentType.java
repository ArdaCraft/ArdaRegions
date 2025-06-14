package com.r3signed.ac.regions.internal.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.r3signed.ac.regions.core.areas.AreaType;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

public class AreaTypeArgumentType extends EnumArgumentType<AreaType> {
    private static final StringIdentifiable.Codec<AreaType> CODEC = StringIdentifiable.createCodec(
            AreaType::values, name -> name
    );

    private AreaTypeArgumentType() {
        super(CODEC, AreaType::values);
    }

    public static AreaTypeArgumentType areaType() {
        return new AreaTypeArgumentType();
    }

    public static AreaType getareaType(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, AreaType.class);
    }
}
