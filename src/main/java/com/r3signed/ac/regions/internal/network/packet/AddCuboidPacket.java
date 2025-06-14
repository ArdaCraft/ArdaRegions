package com.r3signed.ac.regions.internal.network.packet;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.client.core.ClientServices;
import com.r3signed.ac.regions.core.areas.CuboidArea;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public record AddCuboidPacket(CuboidArea area) implements FabricPacket {
    public static final PacketType<AddCuboidPacket> TYPE = PacketType.create(ArdaRegions.getId("add_cuboid_packet"), AddCuboidPacket::new);

    public AddCuboidPacket(PacketByteBuf buf) {
        this(read(buf));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static CuboidArea read(PacketByteBuf buf) {
        // FIXME: avoid this ctor since UUIDs should most likely match up on both sides
        CuboidArea area = new CuboidArea();
        if (buf.readBoolean()) {
            area.setPoint(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        if (buf.readBoolean()) {
            area.setPoint(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return area;
    }

    @Override
    public void write(PacketByteBuf buf) {
        if (area.getMin() != null) {
            buf.writeBoolean(true);
            buf.writeDouble(area.getMin().x);
            buf.writeDouble(area.getMin().y);
            buf.writeDouble(area.getMin().z);
        } else {
            buf.writeBoolean(false);
        }
        if (area.getMax() != null) {
            buf.writeBoolean(true);
            buf.writeDouble(area.getMax().x);
            buf.writeDouble(area.getMax().y);
            buf.writeDouble(area.getMax().z);
        } else {
            buf.writeBoolean(false);
        }
    }

    public void handlePacket(ClientPlayerEntity player, PacketSender sender) {
        WorldAreaCache cache = ClientServices.AREAS.getCache(player.clientWorld);
        if (cache == null) {
            throw new NullPointerException("Client sided Region World cache was null");
        }
        cache.add(this.area);
    }
}
