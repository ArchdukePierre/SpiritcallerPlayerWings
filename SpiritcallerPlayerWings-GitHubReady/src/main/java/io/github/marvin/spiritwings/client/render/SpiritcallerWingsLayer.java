
package io.github.marvin.spiritwings.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.marvin.spiritwings.server.WingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@OnlyIn(Dist.CLIENT)
public class SpiritcallerWingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    // Debug placeholder texture (1x1 white pixel) - replaced by Respillaged assets later
    private static final ResourceLocation WHITE = new ResourceLocation("minecraft", "textures/misc/white.png");

    public SpiritcallerWingsLayer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {

        if (!WingData.clientHasWings(player)) return;

        // Example gating: if player wears chestplate, still render (server decides state)
        // Align to player back
        poseStack.pushPose();

        // Translate to back
        getParentModel().body.translateAndRotate(poseStack);
        poseStack.translate(0.0D, 0.55D, 0.20D);

        // Animate flap
        float t = player.tickCount + partialTick;
        float flap = Mth.sin(t * 0.25f) * 25f; // degrees
        float spread = 50f;

        // LEFT WING placeholder quad
        poseStack.pushPose();
        poseStack.translate(0.18, 0.05, 0.05);
        poseStack.mulPose(net.minecraft.util.Mth.YP.rotationDegrees(spread));
        poseStack.mulPose(net.minecraft.util.Mth.ZP.rotationDegrees(-flap));
        renderWingQuad(poseStack, buffer, 1.2f, 0xF000F0);
        poseStack.popPose();

        // RIGHT WING placeholder quad
        poseStack.pushPose();
        poseStack.translate(-0.18, 0.05, 0.05);
        poseStack.mulPose(net.minecraft.util.Mth.YP.rotationDegrees(-spread));
        poseStack.mulPose(net.minecraft.util.Mth.ZP.rotationDegrees(flap));
        renderWingQuad(poseStack, buffer, 1.2f, 0xF000F0);
        poseStack.popPose();

        // HALO placeholder: simple ring made of two quads; will be replaced by exact model later
        poseStack.pushPose();
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0, -0.15, 0.0);
        poseStack.mulPose(net.minecraft.util.Mth.XP.rotationDegrees(90));
        renderRing(poseStack, buffer, 0.22f, 0.02f, 0xF000F0);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderWingQuad(PoseStack ps, MultiBufferSource buf, float scale, int light) {
        var vc = buf.getBuffer(RenderType.entityTranslucentEmissive(WHITE));
        PoseStack.Pose p = ps.last();
        // Draw a simple glowing rectangle as placeholder
        float w = 0.35f * scale, h = 0.6f * scale;
        // Four corners
        addVertex(vc, p, -w, 0, 0, 0,0, light);
        addVertex(vc, p,  w, 0, 0, 1,0, light);
        addVertex(vc, p,  w, h, 0, 1,1, light);
        addVertex(vc, p, -w, h, 0, 0,1, light);
    }

    private void renderRing(PoseStack ps, MultiBufferSource buf, float radius, float thickness, int light) {
        var vc = buf.getBuffer(RenderType.entityTranslucentEmissive(WHITE));
        PoseStack.Pose p = ps.last();
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            double a0 = (i    ) * (Math.PI * 2 / segments);
            double a1 = (i + 1) * (Math.PI * 2 / segments);
            float x0 = (float)Math.cos(a0), y0 = (float)Math.sin(a0);
            float x1 = (float)Math.cos(a1), y1 = (float)Math.sin(a1);
            float r0 = radius - thickness, r1 = radius + thickness;
            addVertex(vc, p, x0*r0, y0*r0, 0, 0,0, light);
            addVertex(vc, p, x1*r0, y1*r0, 0, 1,0, light);
            addVertex(vc, p, x1*r1, y1*r1, 0, 1,1, light);
            addVertex(vc, p, x0*r1, y0*r1, 0, 0,1, light);
        }
    }

    private void addVertex(VertexConsumer vc, PoseStack.Pose pose, float x, float y, float z, float u, float v, int light) {
        vc.vertex(pose.pose(), x, y, z)
          .color(255, 255, 255, 200)
          .uv(u, v)
          .overlayCoords(0, 10)
          .uv2(light)
          .normal(pose.normal(), 0, 0, 1)
          .endVertex();
    }

    /* === TODO: EXACT VISUALS ===
     * Once Respillaged is present clientside, replace the placeholder quads above with calls into either:
     *  - GeckoLib model/animation loader for the Spiritcaller (recommended), or
     *  - Direct draw of Respillaged's wing model class if exposed.
     *
     * This layer is intentionally isolated so we only need to modify this file to wire the exact visuals.
     */
}
