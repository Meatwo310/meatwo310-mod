package com.dousiyo.meatwo310.client.hud;

import com.dousiyo.meatwo310.item.food.Valine3gItem;
import com.dousiyo.meatwo310.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public final class Valine3gDvdOverlay {
    private static final int ICON_SIZE = 16;
    private static final float SCALE = 2.0F;
    private static final float SPEED_X = 1.37F;
    private static final float SPEED_Y = 0.91F;

    private Valine3gDvdOverlay() {
    }

    public static void render(GuiGraphics graphics, int screenWidth, int screenHeight, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !Valine3gItem.hasScreenEffect(mc.player)) {
            return;
        }

        int drawnSize = Math.round(ICON_SIZE * SCALE);
        int maxX = Math.max(0, screenWidth - drawnSize);
        int maxY = Math.max(0, screenHeight - drawnSize);
        float age = mc.player.tickCount + partialTick;
        int x = Math.round(pingPong(age * SPEED_X, maxX));
        int y = Math.round(pingPong(age * SPEED_Y, maxY));

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(SCALE, SCALE, 1.0F);
        graphics.renderItem(new ItemStack(ModItems.VALINE3G.get()), 0, 0);
        graphics.pose().popPose();
    }

    private static float pingPong(float value, int max) {
        if (max <= 0) {
            return 0.0F;
        }

        float period = max * 2.0F;
        float wrapped = value % period;
        if (wrapped < 0.0F) {
            wrapped += period;
        }
        return wrapped <= max ? wrapped : period - wrapped;
    }
}
