package com.r3signed.ac.regions.client.core.services;

import com.r3signed.ac.regions.internal.events.Events;
import com.r3signed.ac.regions.internal.events.SubscribeEvent;
import com.r3signed.ac.regions.internal.services.AreaService;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ClientAreaService extends AreaService {
    @SubscribeEvent(Events.CLIENT_WORLD_LOAD)
    private void onClientWorldLoad(MinecraftClient client, ClientWorld world) {
        onWorldLoad(world);
    }

    @SubscribeEvent(Events.CLIENT_WORLD_UNLOAD)
    private void onClientWorldUnload(MinecraftClient client, ClientWorld world) {
        onWorldUnload(world);
    }

    @SubscribeEvent(Events.CLIENT_CHUNK_LOAD)
    private void onClientChunkLoad(ClientWorld world, WorldChunk chunk) {
        onChunkLoad(world, chunk.getPos());
    }

    @SubscribeEvent(Events.CLIENT_CHUNK_UNLOAD)
    private void onClientChunkUnload(ClientWorld world, WorldChunk chunk) {
        onChunkUnload(world, chunk.getPos());
    }
}
