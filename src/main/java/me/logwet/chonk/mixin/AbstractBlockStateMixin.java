package me.logwet.chonk.mixin;

import me.logwet.chonk.Chonk;
import me.logwet.chonk.config.Config;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    private static final Logger logger = LogManager.getLogger("ChonkAbstractBlockStateMixin");

    @Inject(method = "neighborUpdate", at = @At("HEAD"))
    private void loadChunkOnUpdate(World world, BlockPos pos, Block block, BlockPos from, boolean notify, CallbackInfo ci) {
        // Only on the server
        if (world.isClient()) return;

        // Only across chunk borders
        ChunkPos src = new ChunkPos(from);
        ChunkPos dest = new ChunkPos(pos);
        if (src.equals(dest)) return;

        ServerWorld serverWorld = (ServerWorld) world;

        if (Config.acceptedBlocks.contains(block.getDefaultState().getBlock())) {
            // Check if chunk fulfills pot rules
            if (Config.checkChunk(serverWorld, dest)) {
                Chonk.loadTicking(serverWorld, dest);
            }
        }
    }
}
