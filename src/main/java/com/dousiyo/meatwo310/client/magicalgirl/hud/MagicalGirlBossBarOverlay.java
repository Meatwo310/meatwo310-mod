package com.dousiyo.meatwo310.client.magicalgirl.hud;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, value = Dist.CLIENT)
public final class MagicalGirlBossBarOverlay {
    private static final ResourceLocation FRAME = Meatwo310.loc("textures/gui/bossbar/bossbar_frame.png");
    private static final ResourceLocation PHASE1 = Meatwo310.loc("textures/gui/bossbar/bossbar_fill_phase1.png");
    private static final ResourceLocation PHASE2 = Meatwo310.loc("textures/gui/bossbar/bossbar_fill_phase2.png");
    private static final ResourceLocation FINAL = Meatwo310.loc("textures/gui/bossbar/bossbar_fill_final.png");
    private static final int FRAME_WIDTH = 256;
    private static final int FRAME_HEIGHT = 24;
    private static final int FILL_X = 38;
    private static final int FILL_Y = 10;
    private static final int FILL_WIDTH = 180;
    private static final int FILL_HEIGHT = 5;

    private MagicalGirlBossBarOverlay() {
    }

    @SubscribeEvent
    public static void onBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LerpingBossEvent boss = event.getBossEvent();
        if (!isMagicalGirlBossBar(boss)) {
            return;
        }

        event.setCanceled(true);

        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        int x = (minecraft.getWindow().getGuiScaledWidth() - FRAME_WIDTH) / 2;
        int y = event.getY();
        ResourceLocation fill = fillTexture(boss);

        graphics.blit(FRAME, x, y, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
        int drawWidth = Math.max(0, Math.min(FILL_WIDTH, Math.round(FILL_WIDTH * boss.getProgress())));
        if (drawWidth > 0) {
            graphics.blit(fill, x + FILL_X, y + FILL_Y, FILL_X, FILL_Y, drawWidth, FILL_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
        }

        Component title = boss.getName();
        int titleX = x + FRAME_WIDTH / 2 - minecraft.font.width(title) / 2;
        graphics.drawString(minecraft.font, title, titleX, y - 10, 0xFAE6D9, false);
    }

    private static boolean isMagicalGirlBossBar(LerpingBossEvent boss) {
        return "魔法少女".equals(boss.getName().getString());
    }

    private static ResourceLocation fillTexture(LerpingBossEvent boss) {
        BossEvent.BossBarColor color = boss.getColor();
        if (color == BossEvent.BossBarColor.RED) {
            return FINAL;
        }
        if (color == BossEvent.BossBarColor.PURPLE) {
            return PHASE2;
        }
        return PHASE1;
    }
}
