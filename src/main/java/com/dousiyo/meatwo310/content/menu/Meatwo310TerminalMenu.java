package com.dousiyo.meatwo310.content.menu;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.registry.ModBlocks;
import com.dousiyo.meatwo310.registry.ModMenus;
import com.dousiyo.meatwo310.registry.ModRecipeTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Meatwo310TerminalMenu extends AbstractContainerMenu {

    public static final int SLOT_IN_X = 15, SLOT_IN_Y = 36;
    public static final int SLOT_OUT_X = 143, SLOT_OUT_Y = 36;

    public static final int INV_X = 9, INV_Y = 85;
    public static final int HOTBAR_Y = 143;

    private final Level level;
    private final ContainerLevelAccess access;

    private final Container input = new SimpleContainer(1) {
        @Override public void setChanged() {
            super.setChanged();
            Meatwo310TerminalMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer result = new ResultContainer();

    private final Slot inputSlot;
    private final Slot resultSlot;

    private List<? extends Recipe<?>> recipes = new ArrayList<>();
    private final DataSlot selectedIndex = DataSlot.standalone();

    public Meatwo310TerminalMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, ContainerLevelAccess.create(inv.player.level(), buf.readBlockPos()));
    }

    public Meatwo310TerminalMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(ModMenus.MEATWO310_TERMINAL.get(), id);
        this.level = inv.player.level();
        this.access = access;

        this.inputSlot = this.addSlot(new Slot(input, 0, SLOT_IN_X, SLOT_IN_Y));

        this.resultSlot = this.addSlot(new Slot(result, 0, SLOT_OUT_X, SLOT_OUT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                // Grant advancement
                if (player instanceof ServerPlayer serverPlayer) {
                    grantCutterAdvancement(serverPlayer);
                }

                consumeOneInput();

                updateRecipes();
                result.setItem(0, ItemStack.EMPTY);
                broadcastChanges();
            }
        });

        addPlayerInventory(inv);

        addDataSlot(selectedIndex);
        selectedIndex.set(-1);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, INV_X + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.MEATWO310_CUTTER.get());
    }

    @Override
    public void slotsChanged(Container container) {
        updateRecipes();
        selectedIndex.set(-1);
        result.setItem(0, ItemStack.EMPTY);
        broadcastChanges();
    }

    private void updateRecipes() {
        if (input.getItem(0).isEmpty()) {
            recipes = List.of();
            return;
        }
        recipes = level.getRecipeManager().getRecipesFor(
                ModRecipeTypes.MEATWO310_CUTTING.get(), input, level
        );
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < recipes.size()) {
            selectedIndex.set(id);
            craftSelectedRecipe(player);
            return true;
        }

        return false;
    }

    private void craftSelectedRecipe(Player player) {
        int idx = selectedIndex.get();
        if (idx < 0 || idx >= recipes.size() || input.getItem(0).isEmpty()) {
            return;
        }

        Recipe<?> recipe = recipes.get(idx);
        ItemStack out = recipe.getResultItem(level.registryAccess()).copy();
        if (out.isEmpty()) {
            return;
        }

        ItemStack toGive = out.copy();
        if (!player.getInventory().add(toGive)) {
            player.drop(toGive, false);
        }

        // Grant advancement
        if (player instanceof ServerPlayer serverPlayer) {
            grantCutterAdvancement(serverPlayer);
        }

        consumeOneInput();
        updateRecipes();

        if (recipes.isEmpty()) {
            selectedIndex.set(-1);
        } else if (selectedIndex.get() >= recipes.size()) {
            selectedIndex.set(recipes.size() - 1);
        }

        result.setItem(0, ItemStack.EMPTY);
        broadcastChanges();
    }

    private void grantCutterAdvancement(ServerPlayer player) {
        ResourceLocation advId = Meatwo310.loc("use_meatwo310_cutter");
        Advancement advancement = player.server.getAdvancements().getAdvancement(advId);
        if (advancement != null) {
            for (String criterion : advancement.getCriteria().keySet()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }

    public int getSelectedIndex() { return selectedIndex.get(); }
    public List<? extends Recipe<?>> getRecipes() { return recipes; }
    public boolean hasInput() { return !input.getItem(0).isEmpty(); }
    public boolean hasResult() { return !result.getItem(0).isEmpty(); }

    private void consumeOneInput() {
        ItemStack in = input.getItem(0);
        if (!in.isEmpty()) {
            in.shrink(1);
            input.setItem(0, in);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack source = slot.getItem();
        moved = source.copy();

        if (index == 1) {
            if (!this.moveItemStackTo(source, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(source, moved);
        } else if (index == 0) {
            if (!this.moveItemStackTo(source, 2, 38, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(source, 0, 1, false)) {
                if (index < 29) {
                    if (!this.moveItemStackTo(source, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(source, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (source.getCount() == moved.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, source);
        return moved;
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide) {
            ItemStack out = result.getItem(0);
            if (!out.isEmpty()) {
                ItemStack toGive = out.copy();
                result.setItem(0, ItemStack.EMPTY);

                consumeOneInput();

                player.getInventory().add(toGive);
                if (!toGive.isEmpty()) player.drop(toGive, false);
            }
        }

        this.access.execute((lvl, pos) -> this.clearContainer(player, this.input));
        super.removed(player);
    }
}
