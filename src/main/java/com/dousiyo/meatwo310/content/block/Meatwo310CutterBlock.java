package com.dousiyo.meatwo310.content.block;

import com.dousiyo.meatwo310.content.menu.Meatwo310TerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class Meatwo310CutterBlock extends Block {
    private static final Component TITLE = Component.literal("Meatwo310 Terminal");

    public Meatwo310CutterBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MenuConstructor factory = (windowId, inv, p) ->
                    new Meatwo310TerminalMenu(windowId, inv, ContainerLevelAccess.create(level, pos));
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(factory, TITLE), pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
