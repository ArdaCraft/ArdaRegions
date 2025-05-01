package com.r3signed.ac.regions.core.services;

import com.r3signed.ac.regions.internal.events.Events;
import com.r3signed.ac.regions.internal.events.SubscribeEvent;
import com.r3signed.ac.regions.internal.services.AreaService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ServerAreaService extends AreaService {
    @SubscribeEvent(Events.SERVER_WORLD_LOAD)
    private void onServerWorldLoad(MinecraftServer server, ServerWorld world) {
        onWorldLoad(world);
    }

    @SubscribeEvent(Events.SERVER_WORLD_UNLOAD)
    private void onServerWorldUnload(MinecraftServer server, ServerWorld world) {
        onWorldUnload(world);
    }

    @SubscribeEvent(Events.SERVER_CHUNK_LOAD)
    private void onServerChunkLoad(ServerWorld world, WorldChunk chunk) {
        onChunkLoad(world, chunk.getPos());
    }

    @SubscribeEvent(Events.SERVER_CHUNK_UNLOAD)
    private void onServerChunkUnload(ServerWorld world, WorldChunk chunk) {
        onChunkUnload(world, chunk.getPos());
    }
}
