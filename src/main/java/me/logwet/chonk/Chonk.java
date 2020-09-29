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
    private static ChunkTicketType<ChunkPos> tickingTicket;
    private static ChunkTicketType<ChunkPos> entityTicket;

    public static void setExpiryTicks(int expiryTicks) {
        Chonk.expiryTicks = expiryTicks;
        tickingTicket = ChunkTicketType.create("chonkTicking",
                Comparator.comparingLong(ChunkPos::toLong), expiryTicks);
        entityTicket = ChunkTicketType.create("chonkEntity",
                Comparator.comparingLong(ChunkPos::toLong), expiryTicks);
    }

    public static void loadTicking(ServerWorld world, ChunkPos pos) {
        load(world, pos, false);
        logger.debug("Loaded chunk " + pos.toString() + " as ticking");
    }

    public static void loadEntityTicking(ServerWorld world, ChunkPos pos) {
        load(world, pos, true);
        logger.debug("Loaded chunk " + pos.toString() + " as entity ticking");
    }

    private static void load(ServerWorld world, ChunkPos pos, Boolean entity) {
        if (!Config.getEnabled()) return;

        ServerChunkManager manager = world.getChunkManager();

        int level;
        if (entity) {
            level = 2;
            manager.addTicket(entityTicket, pos, level, pos);
        } else {
            level = 1;
            manager.addTicket(tickingTicket, pos, level, pos);
        }

        ChunkHolder holder = manager.getChunkHolder(pos.toLong());
        if (holder.getLevel() > 33 - level) manager.tick();
    }
}

