package com.r3signed.ac.regions.internal.network.packet;

import com.r3signed.ac.regions.ArdaRegions;
import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.client.core.ClientServices;
import com.r3signed.ac.regions.core.areas.PolygonArea;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public record AddPolygonPacket(PolygonArea area) implements FabricPacket {
    public static final PacketType<AddPolygonPacket> TYPE = PacketType.create(ArdaRegions.getId("add_polygon_packet"), AddPolygonPacket::new);

    public AddPolygonPacket(PacketByteBuf buf) {
        this(read(buf));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static PolygonArea read(PacketByteBuf buf) {
        // FIXME: avoid this ctor since UUIDs should most likely match up on both sides
        PolygonArea area = new PolygonArea();
        int size = buf.readVarInt();
        HashSet<Vec3d> points = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            points.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        area.setPoints(points);
        return area;
    }

    @Override
    public void write(PacketByteBuf buf) {
        Set<Vec3d> points = area.getPoints();
        buf.writeVarInt(points.size());
        for (Vec3d point : points) {
            buf.writeDouble(point.x);
            buf.writeDouble(point.y);
            buf.writeDouble(point.z);
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
