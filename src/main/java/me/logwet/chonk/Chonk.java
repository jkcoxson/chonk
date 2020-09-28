package me.logwet.chonk;

import me.logwet.chonk.config.Config;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;


public class Chonk {
    private static final Logger logger = LogManager.getLogger("ChonkMain");

    private static int expiryTicks = 8;
    private static ChunkTicketType<ChunkPos> ticket = ChunkTicketType.create("chonk",
            Comparator.comparingLong(ChunkPos::toLong), expiryTicks);

    public static void setExpiryTicks(int expiryTicks) {
        Chonk.expiryTicks = expiryTicks;
        ticket = ChunkTicketType.create("chonk", Comparator.comparingLong(ChunkPos::toLong), expiryTicks);
    }

    public static void loadTicking(ServerWorld world, ChunkPos pos) {
        load(world, pos, 1);
        logger.debug("Loaded chunk " + pos.toString() + " as ticking");
    }

    public static void loadEntityTicking(ServerWorld world, ChunkPos pos) {
        load(world, pos, 2);
        logger.debug("Loaded chunk " + pos.toString() + " as entity ticking");
    }

    private static void load(ServerWorld world, ChunkPos pos, int level) {
        if (!Config.getEnabled()) return;
        ServerChunkManager manager = world.getChunkManager();

        manager.addTicket(ticket, pos, level, pos);

        ChunkHolder holder = manager.getChunkHolder(pos.toLong());
        if (holder.getLevel() > 33 - level) manager.tick();
    }
}

