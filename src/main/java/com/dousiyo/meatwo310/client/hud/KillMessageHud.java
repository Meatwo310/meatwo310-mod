package com.dousiyo.meatwo310.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;

import java.util.UUID;

public final class KillMessageHud {
    private KillMessageHud() {}

    private static long expiresAtGameTime = -1L;
    private static String victimName = "";
    private static UUID victimUUID = null;

    private static final int DURATION_TICKS = 60;

    public static void show(String victim, UUID uuid) {
        Minecraft mc = Minecraft.getInstance();
        victimName = victim;
        victimUUID = uuid;
        if (mc.level == null) {
            expiresAtGameTime = -1L;
            return;
        }
        expiresAtGameTime = mc.level.getGameTime() + DURATION_TICKS;
    }

    public static void render(GuiGraphics g, int screenW, int screenH) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        long ticksLeft = expiresAtGameTime - mc.level.getGameTime();
        if (ticksLeft <= 0) return;
        if (mc.font == null) return;

        int alpha = 255;
        if (ticksLeft < 20) {
            alpha = (int)(255f * (ticksLeft / 20f));
        }
        int color = (alpha << 24) | 0xFFFFFF;

        Component defeatText = Component.literal("撃破");
        int defeatWidth = mc.font.width(defeatText);
        int defeatX = (screenW - defeatWidth) / 2;
        int defeatY = screenH - 110;

        g.drawString(mc.font, defeatText, defeatX, defeatY, color, true);

        int playerColor = color;
        if (victimUUID != null) {
            Player victim = (Player) mc.level.getPlayerByUUID(victimUUID);
            if (victim != null && victim.getTeam() instanceof PlayerTeam team) {
                int teamColor = team.getColor().getColor();
                if (teamColor != 0) {
                    playerColor = (alpha << 24) | (teamColor & 0xFFFFFF);
                }
            }
        }

        Component playerText = Component.literal(victimName);
        float playerScale = 1.25F;
        int playerWidth = mc.font.width(playerText);
        int playerX = (int)((screenW - playerWidth * playerScale) / 2);
        int playerY = defeatY + mc.font.lineHeight + 2;

        g.pose().pushPose();
        g.pose().translate(playerX, playerY, 0);
        g.pose().scale(playerScale, playerScale, 1.0F);

        int bgAlpha = (int)(alpha * 0.6f);
        int backgroundColor = (bgAlpha << 24) | 0x000000;
        int padding = 2;
        g.fill(-padding, -padding,
               playerWidth + padding,
               (int)(mc.font.lineHeight + padding),
               backgroundColor);

        g.drawString(mc.font, playerText, 0, 0, playerColor, true);
        g.pose().popPose();
    }
}
