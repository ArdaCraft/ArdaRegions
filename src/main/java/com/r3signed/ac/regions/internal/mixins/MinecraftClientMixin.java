package com.r3signed.ac.regions.internal.mixins;

import com.r3signed.ac.regions.core.events.world.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientWorld world;

    /**
     * Fires {@link ClientWorldEvents#UNLOAD} event when the client disconnects from a server.
     */
    @Inject(
            method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resource/ServerResourcePackProvider;clear()Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private void onDisconnect(Screen screen, CallbackInfo ci) {
        ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.world);
    }

    /**
     * Fires {@link ClientWorldEvents#LOAD} and {@link ClientWorldEvents#UNLOAD} events, depending on
     * the context, when the world is set.
     */
    @Inject(
            method = "setWorld",
            at = @At("HEAD")
    )
    private void onSetWorld(ClientWorld world, CallbackInfo ci) {
        if (this.world != null && this.world != world) {
            ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.world);
        } else if (world != null) {
            ClientWorldEvents.LOAD.invoker().onWorldLoad((MinecraftClient) (Object) this, world);
        }
    }
}
