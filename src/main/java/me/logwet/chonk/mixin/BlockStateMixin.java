package me.logwet.chonk.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.logwet.chonk.Chonk;

@Mixin(AbstractBlockState.class)
public abstract class BlockStateMixin {
	@Inject(method = "neighborUpdate", at = @At("HEAD"))
	private void loadChunkOnUpdate(World world, BlockPos pos, Block block, BlockPos from, boolean idkWhatThisIs, CallbackInfo ci) {
		// Only on the server
		if (world.isClient()) return;

		// Only across chunk borders
		ChunkPos src = new ChunkPos(from);
		ChunkPos dest = new ChunkPos(pos);
		if (src.equals(dest)) return;

		Chonk.loadTicking((ServerWorld)world, dest);
	}
}
