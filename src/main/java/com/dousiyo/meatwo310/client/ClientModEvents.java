package com.dousiyo.meatwo310.client;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.client.hud.Valine3gDvdOverlay;
import com.dousiyo.meatwo310.client.renderer.ConversationNpcRenderer;
import com.dousiyo.meatwo310.client.renderer.EnhancedGrapplerHookRenderer;
import com.dousiyo.meatwo310.client.renderer.GrapplerHookRenderer;
import com.dousiyo.meatwo310.client.screen.Meatwo310TerminalScreen;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.registry.ModFluids;
import com.dousiyo.meatwo310.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.MEATWO310_TERMINAL.get(), Meatwo310TerminalScreen::new));
        event.enqueueWork(ClientModEvents::registerFluidRenderLayers);

        EntityRenderers.register(ModEntities.GRAPPLER_PROJECTILE.get(), GrapplerHookRenderer::new);
        EntityRenderers.register(ModEntities.ENHANCED_GRAPPLER_PROJECTILE.get(), EnhancedGrapplerHookRenderer::new);
        EntityRenderers.register(ModEntities.IMPULSE_GRENADE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.ICE_GRENADE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.HOP_GRENADE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.MEATWO310_ENTITY.get(), ConversationNpcRenderer::new);
        EntityRenderers.register(ModEntities.MEATWO310_NPC.get(), ConversationNpcRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("valine3g_dvd", (gui, graphics, partialTick, screenWidth, screenHeight) ->
                Valine3gDvdOverlay.render(graphics, screenWidth, screenHeight, partialTick));
    }

    private static void registerFluidRenderLayers() {
        RenderType translucent = RenderType.translucent();
        ItemBlockRenderTypes.setRenderLayer(ModFluids.GREEN_LIQUID.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.GREEN_LIQUID_FLOWING.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.PURPLE_LIQUID.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.PURPLE_LIQUID_FLOWING.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.LIGHT_GRAY_LIQUID.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.LIGHT_GRAY_LIQUID_FLOWING.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.LIGHT_BLUE_LIQUID.get(), translucent);
        ItemBlockRenderTypes.setRenderLayer(ModFluids.LIGHT_BLUE_LIQUID_FLOWING.get(), translucent);
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(Meatwo310.loc("entity/grappler_hook"));
        event.register(Meatwo310.loc("entity/enhanced_grappler_hook"));
    }


}
