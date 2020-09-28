/*
Code adapted from FlowerPotBlockMixin.java in carpet-extra by Gnembon
https://github.com/gnembon/carpet-extra/blob/master/src/main/java/carpetextra/mixins/FlowerPotBlockMixin.java
*/

package me.logwet.chonk.mixin;

import me.logwet.chonk.config.Config;
import me.logwet.chonk.config.Config.PotLevel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Map;


@Mixin(FlowerPotBlock.class)
public abstract class FlowerPotBlockMixin extends Block {
    private static final Logger logger = LogManager.getLogger("ChonkFlowerPotBlockMixin");
    @Shadow
    @Final
    private static Map<Block, Block> CONTENT_TO_POTTED;

    @Shadow
    @Final
    private Block content;

    public FlowerPotBlockMixin(Settings chonk$settings) {
        super(chonk$settings);
    }

    @Inject(method = "onUse", at = @At("HEAD"))
    private void onActivate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                            BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        // If the pot feature is enabled and the world is a server world
        if (!world.isClient) {
            if (Config.getPotLevel().equals(PotLevel.INCLUDE) || Config.getPotLevel().equals(PotLevel.EXCLUDE)) {
                // Get the item the player is currently holding
                ItemStack stack = player.getStackInHand(hand);
                Item item = stack.getItem();

                // If the item is the correct plant, leave it as is. If not, set the item to air (nothing)
                Block block = item instanceof BlockItem ? CONTENT_TO_POTTED.getOrDefault(((BlockItem) item).getBlock(),
                        Blocks.AIR) : Blocks.AIR;

                boolean blockNotPottable = block.equals(Blocks.AIR);
                boolean potEmpty = this.content.equals(Blocks.AIR);

                // If the player use changed the state of the pot, toggle the state of the chunk
                if (blockNotPottable != potEmpty && (block.equals(Config.getPotType().potPlant) ||
                        this.content.equals(Config.getPotType().plant))) {

                    ChunkPos chunkPos = new ChunkPos(pos);

                    try {
                        Config.setChunk(Config.getWorldNameFromObj((ServerWorld) world), chunkPos, potEmpty);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                    logger.info("Set chunk " + chunkPos.toString() + " to " + potEmpty);
                }
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player) {
        // If the pot feature is enabled, the world is a server world and the pot broken contained the correct plant
        if (!world.isClient) {
            if (Config.getPotLevel().equals(PotLevel.INCLUDE) || Config.getPotLevel().equals(PotLevel.EXCLUDE)
                    && this.content.equals(Config.getPotType().plant)) {

                // Set the state of the chunk to off
                ChunkPos chunkPos = new ChunkPos(blockPos);

                try {
                    Config.setChunk(Config.getWorldNameFromObj((ServerWorld) world), chunkPos, false);
                } catch (IOException e) {
                    logger.error(e);
                }
                logger.info("Removed chunk " + chunkPos.toString());
            }
            // Because this method is overridden and not injected, the parent method must be called.
            super.onBreak(world, blockPos, blockState, player);
        }
    }
}
