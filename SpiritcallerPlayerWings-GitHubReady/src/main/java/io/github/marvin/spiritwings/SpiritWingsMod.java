
package io.github.marvin.spiritwings;

import io.github.marvin.spiritwings.command.WingsCommands;
import io.github.marvin.spiritwings.network.NetworkHandler;
import io.github.marvin.spiritwings.server.WingData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(SpiritWingsMod.MODID)
public class SpiritWingsMod {
    public static final String MODID = "spiritwings";

    public SpiritWingsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        // Allow clients with different networking versions to connect (our packet is versioned anyway)
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a,b)->true));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Client-only init is in ClientInit
        io.github.marvin.spiritwings.client.ClientInit.init();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent evt) {
        WingsCommands.register(evt.getDispatcher());
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone evt) {
        // Keep wings state on death/respawn
        WingData.copy(evt.getOriginal(), evt.getEntity());
    }
}
