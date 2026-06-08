package com.dousiyo.meatwo310.client;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.client.hud.Valine3gDvdOverlay;
import com.dousiyo.meatwo310.client.magicalgirl.model.MagicRightHandModel;
import com.dousiyo.meatwo310.client.magicalgirl.particle.NatureAttackParticle;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.ColoredLightningRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.ChemicalAreaRenderer;
import com.dousiyo.meatwo310.client.renderer.ConversationNpcRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.DiamondSpinnerRenderer;
import com.dousiyo.meatwo310.client.renderer.EnhancedGrapplerHookRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.FloatingTaCZGunRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.GrandSpellNovaRenderer;
import com.dousiyo.meatwo310.client.renderer.GrapplerHookRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.InjectionNeedleRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicalGirlBossRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicalHazardRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicalPhantomRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicalTwisterRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicSafetyAnchorRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.MagicRightHandRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.NoopEntityRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.RibbonJudgementRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.RuneCageRenderer;
import com.dousiyo.meatwo310.client.magicalgirl.renderer.StellaBurstRenderer;
import com.dousiyo.meatwo310.client.screen.Meatwo310TerminalScreen;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.registry.ModFluids;
import com.dousiyo.meatwo310.registry.ModMenus;
import com.dousiyo.meatwo310.registry.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
        EntityRenderers.register(ModEntities.MAGICAL_GIRL_BOSS.get(), MagicalGirlBossRenderer::new);
        EntityRenderers.register(ModEntities.MAGIC_RIGHT_HAND.get(), MagicRightHandRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_HAZARD.get(), MagicalHazardRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_TWISTER.get(), MagicalTwisterRenderer::new);
        EntityRenderers.register(ModEntities.MAGIC_SAFETY_ANCHOR.get(), MagicSafetyAnchorRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_GROUND_LIGHTNING.get(), NoopEntityRenderer::new);
        EntityRenderers.register(ModEntities.COLORED_LIGHTNING.get(), ColoredLightningRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_PHANTOM.get(), MagicalPhantomRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_GIRL_PHANTOM.get(), MagicalPhantomRenderer::new);
        EntityRenderers.register(ModEntities.STELLA_BURST_PROJECTILE.get(), StellaBurstRenderer::new);
        EntityRenderers.register(ModEntities.DIAMOND_SPINNER.get(), DiamondSpinnerRenderer::new);
        EntityRenderers.register(ModEntities.RIBBON_JUDGEMENT.get(), RibbonJudgementRenderer::new);
        EntityRenderers.register(ModEntities.RUNE_CAGE.get(), RuneCageRenderer::new);
        EntityRenderers.register(ModEntities.GRAND_SPELL_NOVA.get(), GrandSpellNovaRenderer::new);
        EntityRenderers.register(ModEntities.CHEMICAL_AREA.get(), ChemicalAreaRenderer::new);
        EntityRenderers.register(ModEntities.FLOATING_TACZ_GUN.get(), FloatingTaCZGunRenderer::new);
        EntityRenderers.register(ModEntities.INJECTION_NEEDLE.get(), InjectionNeedleRenderer::new);
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

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MagicRightHandModel.LAYER_LOCATION, MagicRightHandModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.NATURE_LEAF.get(), NatureAttackParticle.Provider::new);
        event.registerSpriteSet(ModParticles.NATURE_ROOT.get(), NatureAttackParticle.Provider::new);
        event.registerSpriteSet(ModParticles.NATURE_IVY.get(), NatureAttackParticle.Provider::new);
        event.registerSpriteSet(ModParticles.NATURE_LIGHT.get(), NatureAttackParticle.Provider::new);
    }
}
