package com.r3signed.ac.regions.client.rendering;

import com.r3signed.ac.regions.api.data.WorldAreaCache;
import com.r3signed.ac.regions.client.core.ClientServices;
import com.r3signed.ac.regions.core.areas.Area;
import com.r3signed.ac.regions.core.areas.CuboidArea;
import com.r3signed.ac.regions.core.areas.PolygonArea;
import com.r3signed.ac.regions.core.item.AreaDebugging;
import com.r3signed.ac.regions.internal.geometry.Triangle;
import com.r3signed.ac.regions.utils.UnorderedPair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class RegionRenderer {
    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(RegionRenderer::renderRegionDebug);
    }

    private static void renderRegionDebug(WorldRenderContext context) {
        WorldAreaCache cache = ClientServices.AREAS.getCache(context.world());
        if (cache == null) return;
        Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();
        VertexConsumerProvider vertexConsumerProvider = context.consumers();
        if (vertexConsumerProvider == null) return;

        for (Area area : cache.getAreas()) {
            if (area instanceof PolygonArea polygonArea) {
                RegionRenderer.renderPolygon(polygonArea, matrix, context.camera(), vertexConsumerProvider);
            } else if (area instanceof CuboidArea cuboidArea) {
                RegionRenderer.renderCuboid(cuboidArea, matrix, context.camera(), vertexConsumerProvider);
            }
        }

        RegionRenderer.renderTempPoints(matrix, context.camera(), vertexConsumerProvider);
    }

    private static void renderCuboid(CuboidArea area, Matrix4f matrix, Camera camera, VertexConsumerProvider provider) {
        if (area.getMin() == null || area.getMax() == null) return;
        VertexConsumer consumer = provider.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        int color = 0xFF00FF00;
        Vec3d[] points = new Vec3d[8];
        HashSet<UnorderedPair<Vec3d>> edges = new HashSet<>();

        for (int i = 0; i < 8; i++) {
            double x = ((i & 1) == 0) ? area.getMin().x : area.getMax().x;
            double y = ((i & 2) == 0) ? area.getMin().y : area.getMax().y;
            double z = ((i & 4) == 0) ? area.getMin().z : area.getMax().z;
            points[i] = new Vec3d(x, y, z);
        }
        for (Vec3d cornerA : points) {
            for (Vec3d cornerB : points) {
                if (cornerA.equals(cornerB)) {
                    continue;
                }
                edges.add(new UnorderedPair<>(cornerA, cornerB));
            }
        }

        for (UnorderedPair<Vec3d> edge : edges) {
            Vec3d start = edge.first().subtract(camera.getPos());
            Vec3d end = edge.second().subtract(camera.getPos());

            consumer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z).color(color).next();
            consumer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z).color(color).next();
        }
    }

    private static void renderPolygon(PolygonArea area, Matrix4f matrix, Camera camera, VertexConsumerProvider provider) {
        VertexConsumer consumer = provider.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        int color = 0xFF00FF00;

        HashSet<UnorderedPair<Vec3d>> edges = new HashSet<>();
        for (Triangle face : area.getFaces()) {
            edges.add(new UnorderedPair<>(face.a(), face.b()));
            edges.add(new UnorderedPair<>(face.a(), face.c()));
            edges.add(new UnorderedPair<>(face.b(), face.c()));
        }
        for (UnorderedPair<Vec3d> edge : edges) {
            Vec3d start = edge.first().subtract(camera.getPos());
            Vec3d end = edge.second().subtract(camera.getPos());
            consumer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z).color(color).next();
            consumer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z).color(color).next();
        }
    }

    private static void renderTempPoints(Matrix4f matrix, Camera camera, VertexConsumerProvider provider) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof AreaDebugging<?> debugTool)) return;
        double crossSize = 0.25;
        int color = 0xFF0000FF;

        for (Vec3d center : debugTool.getPoints(stack)) {
            for (UnorderedPair<Vec3d> entry : getCross(center, crossSize)) {
                VertexConsumer consumer = provider.getBuffer(RenderLayer.getDebugLineStrip(1.0));
                Vec3d start = entry.first().subtract(camera.getPos());
                Vec3d end = entry.second().subtract(camera.getPos());
                consumer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z).color(color).next();
                consumer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z).color(color).next();
            }
        }
    }

    private static @NotNull HashSet<UnorderedPair<Vec3d>> getCross(Vec3d center, double crossSize) {
        HashSet<UnorderedPair<Vec3d>> cross = new HashSet<>();
        cross.add(new UnorderedPair<>(
                new Vec3d(center.x + crossSize, center.y, center.z),
                new Vec3d(center.x - crossSize, center.y, center.z)
        ));
        cross.add(new UnorderedPair<>(
                new Vec3d(center.x, center.y + crossSize, center.z),
                new Vec3d(center.x, center.y - crossSize, center.z)
        ));
        cross.add(new UnorderedPair<>(
                new Vec3d(center.x, center.y, center.z + crossSize),
                new Vec3d(center.x, center.y, center.z - crossSize)
        ));
        return cross;
    }
}
