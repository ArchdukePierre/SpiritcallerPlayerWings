
package io.github.marvin.spiritwings.client;

import io.github.marvin.spiritwings.SpiritWingsMod;
import io.github.marvin.spiritwings.client.render.SpiritcallerWingsLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpiritWingsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

    public static void init() {
        // Nothing to do yet; we hook via AddLayers below
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers evt) {
        // Add our cosmetic layer to both player skins
        var def = evt.getSkin("default");
        var slim = evt.getSkin("slim");
        if (def != null) def.addLayer(new SpiritcallerWingsLayer(def));
        if (slim != null) slim.addLayer(new SpiritcallerWingsLayer(slim));
    }
}
