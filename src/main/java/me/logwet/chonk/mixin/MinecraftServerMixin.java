/*
Code adapted from MinecraftServer_coreMixin.java in fabric-carpet by Gnembon
https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/MinecraftServer_coreMixin.java
*/

package me.logwet.chonk.mixin;

import me.logwet.chonk.config.Config;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    private static final Logger logger = LogManager.getLogger("ChonkMinecraftServerMixin");

    // Inject at RETURN to ensure server.getWorlds() is already populated
    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void onServerLoad(CallbackInfo ci) throws IOException {
        Config.setServer((MinecraftServer) (Object) this);
        Config.setConfig();
        logger.info("Mod initialized");
    }

    // Inject at RETURN to ensure server.getWorlds() is already populated
    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void onServerReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir)
            throws IOException {
        Config.setServer((MinecraftServer) (Object) this);
        Config.setConfig();
        logger.info("Mod reinitialized");
    }
}
