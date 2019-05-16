package io.github.kvverti.bluelightspecial.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

/**
 * Allows us to pass in our own world.
 */
class ForwardingItemUsageContext extends ItemUsageContext {

    ForwardingItemUsageContext(World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        super(world, player, hand, player.getStackInHand(hand), hitResult);
    }
}
