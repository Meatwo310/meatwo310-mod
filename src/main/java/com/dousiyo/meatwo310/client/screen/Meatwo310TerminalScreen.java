package com.dousiyo.meatwo310.client.screen;

import com.dousiyo.meatwo310.content.menu.Meatwo310TerminalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Meatwo310TerminalScreen extends AbstractContainerScreen<Meatwo310TerminalMenu> {
    private static final class TransparentButton extends Button {
        private TransparentButton(int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        }
    }

    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("meatwo310", "textures/gui/container/meatwo310_terminal_gui.png");

    private static final int CAND_X = 44;
    private static final int CAND_Y = 20;
    private static final int CAND_COLS = 5;
    private static final int CAND_SIZE = 18;
    private final List<Button> candidateButtons = new ArrayList<>(10);

    public Meatwo310TerminalScreen(Meatwo310TerminalMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        int selected = this.menu.getSelectedIndex();
        var recipes = this.menu.getRecipes();

        if (selected >= 0 && selected < recipes.size()) {
            var out = recipes.get(selected).getResultItem(this.minecraft.level.registryAccess());
            String text = out.getHoverName().getString() + " x" + out.getCount();
            g.drawString(this.font, text, 7, 8, 0xFFEECF6A, false);
        }
    }

    private int gx(int x) {
        return this.leftPos + x;
    }

    private int gy(int y) {
        return this.topPos + y;
    }

    @Override
    protected void init() {
        super.init();

        candidateButtons.clear();
        for (int i = 0; i < 10; i++) {
            int col = i % CAND_COLS;
            int row = i / CAND_COLS;

            int bx = gx(CAND_X + col * CAND_SIZE);
            int by = gy(CAND_Y + row * CAND_SIZE);
            final int idx = i;

            Button btn = new TransparentButton(bx, by, 18, 18, b ->
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, idx));

            candidateButtons.add(btn);
            this.addRenderableWidget(btn);
        }

        syncButtons();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        syncButtons();
    }

    private void syncButtons() {
        boolean canProcess = this.menu.hasInput() && !this.menu.getRecipes().isEmpty();

        int count = this.menu.getRecipes().size();
        for (int i = 0; i < candidateButtons.size(); i++) {
            boolean show = canProcess && i < count;
            candidateButtons.get(i).visible = show;
            candidateButtons.get(i).active = show;
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        var recipes = this.menu.getRecipes();
        int max = Math.min(10, recipes.size());
        hideUnusedCandidateSlots(g, max);

        for (int i = 0; i < max; i++) {
            int col = i % CAND_COLS;
            int row = i / CAND_COLS;

            int x = gx(CAND_X + col * CAND_SIZE + 1);
            int y = gy(CAND_Y + row * CAND_SIZE + 1);
            int slotLeft = gx(CAND_X + col * CAND_SIZE);
            int slotTop = gy(CAND_Y + row * CAND_SIZE);
            int slotRight = slotLeft + 18;
            int slotBottom = slotTop + 18;

            var out = recipes.get(i).getResultItem(this.minecraft.level.registryAccess());
            g.renderItem(out, x, y);
            g.renderItemDecorations(this.font, out, x, y);

            boolean hovered = mouseX >= slotLeft && mouseX < slotRight
                    && mouseY >= slotTop && mouseY < slotBottom;
            if (hovered) {
                g.fill(slotLeft, slotTop, slotRight, slotBottom, 0x30FFFFFF);
            }

            if (i == this.menu.getSelectedIndex()) {
                g.fill(slotLeft, slotTop, slotRight, slotBottom, 0x22000000);
                g.fill(slotLeft, slotTop, slotRight, slotTop + 1, 0xFFFFD86A);
                g.fill(slotLeft, slotBottom - 1, slotRight, slotBottom, 0xFFFFD86A);
                g.fill(slotLeft, slotTop, slotLeft + 1, slotBottom, 0xFFFFD86A);
                g.fill(slotRight - 1, slotTop, slotRight, slotBottom, 0xFFFFD86A);
            }
        }
    }

    private void hideUnusedCandidateSlots(GuiGraphics g, int usedCount) {
        for (int i = usedCount; i < 10; i++) {
            int col = i % CAND_COLS;
            int row = i / CAND_COLS;
            int left = gx(CAND_X + col * CAND_SIZE);
            int top = gy(CAND_Y + row * CAND_SIZE);
            paintUnusedCandidateSlot(g, left, top);
        }
    }

    private void paintUnusedCandidateSlot(GuiGraphics g, int left, int top) {
        int base = 0xFF2A2A2D;
        int inner = 0xFF232326;
        int edge = 0xFF313136;

        g.fill(left + 1, top + 1, left + 17, top + 17, base);
        g.fill(left + 2, top + 2, left + 16, top + 16, inner);
        g.fill(left + 1, top + 1, left + 17, top + 2, edge);
        g.fill(left + 1, top + 1, left + 2, top + 17, edge);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderCandidateTooltip(g, mouseX, mouseY);
        this.renderTooltip(g, mouseX, mouseY);
    }

    private void renderCandidateTooltip(GuiGraphics g, int mouseX, int mouseY) {
        var recipes = this.menu.getRecipes();
        int max = Math.min(10, recipes.size());
        for (int i = 0; i < max; i++) {
            int col = i % CAND_COLS;
            int row = i / CAND_COLS;
            int left = gx(CAND_X + col * CAND_SIZE);
            int top = gy(CAND_Y + row * CAND_SIZE);
            int right = left + 18;
            int bottom = top + 18;

            if (mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom) {
                ItemStack out = recipes.get(i).getResultItem(this.minecraft.level.registryAccess());
                g.renderTooltip(this.font, out, mouseX, mouseY);
                return;
            }
        }
    }
}
