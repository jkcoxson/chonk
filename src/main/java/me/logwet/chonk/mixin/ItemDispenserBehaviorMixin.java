package me.logwet.chonk.mixin;

import me.logwet.chonk.Chonk;
import me.logwet.chonk.config.Config;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin {
    private static final Logger logger = LogManager.getLogger("ChonkItemDispenserBehaviorMixin");

    @Inject(method = "dispense", at = @At("HEAD"))
    private void loadChunkOnDispense(BlockPointer block, ItemStack item, CallbackInfoReturnable ci) {
        // Only on the server
        World world = block.getWorld();
        if (world.isClient()) return;

        ServerWorld serverWorld = (ServerWorld) world;
        ChunkPos pos = new ChunkPos(block.getBlockPos());

        // Check if chunk fulfills pot rules
        if (Config.checkChunk(serverWorld, pos)) {
            Chonk.loadEntityTicking(serverWorld, pos);
        }
    }
}
