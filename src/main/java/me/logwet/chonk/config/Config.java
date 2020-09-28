package me.logwet.chonk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.logwet.chonk.Chonk;
import me.logwet.chonk.config.object.Chunk;
import me.logwet.chonk.config.object.Settings;
import me.logwet.chonk.config.object.World;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public class Config {

    private static final Logger logger = LogManager.getLogger("ChonkConfig");
    private static final String identifier = "chonk";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));

    private static final List<String> loadedWorlds = new ArrayList<>();
    private static final Map<String, Map<List<Integer>, Boolean>> worldMap = new HashMap<>();
    private static PotLevel potLevel;
    private static PotType potType;
    private static MinecraftServer server;
    private static Settings settings;

    public static List<Block> acceptedBlocks = new ArrayList<>();

    public static Boolean getEnabled() {
        return settings.getEnabled();
    }

    public static PotLevel getPotLevel() {
        return potLevel;
    }

    public static PotType getPotType() {
        return potType;
    }

    public static void setServer(MinecraftServer server) {
        Config.server = server;
    }

    private static File getConfigFile() {
        return server.getSavePath(WorldSavePath.ROOT).resolve(identifier + ".yml").toFile();
    }

    private static void readConfig() throws IOException {
        File configFile = getConfigFile();

        if (!(configFile.exists() && configFile.isFile() && configFile.canRead())) {
            writeDefaultConfigToFile(configFile);
            logger.warn("Chonk was unable to load the config file. Writing default config to disk");
        }

        try {
            readConfigFromFile(configFile);
        } catch (IOException e) {
            writeDefaultConfigToFile(configFile);
            logger.warn("Chonk was unable to parse the config file. Writing and using default config");
            readConfigFromFile(configFile);
        }
    }

    private static void readConfigFromFile(File file) throws IOException {
        // Convert the nested objects parsed from yaml by Jackson into nested Hashmaps for efficiency
        settings = objectMapper.readValue(file, Settings.class);
        for (World w : settings.getWorlds()) {
            Map<List<Integer>, Boolean> chunkMap = new HashMap<>();
            for (Chunk c : w.getChunks()) {
                chunkMap.put(Arrays.asList(c.getX(), c.getZ()), true);
            }
            worldMap.put(w.getName(), chunkMap);
        }
    }

    public static void saveConfig() throws IOException {
        // Convert the in memory nested Hashmaps into nested objects for Jackson to parse into yaml
        // This is called in MinecraftServerMixin on every world save
        settings.setWorlds(new ArrayList<>());
        for (Map.Entry<String, Map<List<Integer>, Boolean>> world : worldMap.entrySet()) {
            String worldName = world.getKey();
            World worldToAdd = new World(worldName, new ArrayList<>());
            Map<List<Integer>, Boolean> chunkList = world.getValue();
            for (Map.Entry<List<Integer>, Boolean> chunk : chunkList.entrySet()) {
                List<Integer> pos = chunk.getKey();
                worldToAdd.getChunks().add(new Chunk(pos.get(0), pos.get(1)));
            }
            settings.getWorlds().add(worldToAdd);
        }
        // TODO: Add the comments that are present in the default config file but are lost when written by Jackson
        objectMapper.writeValue(getConfigFile(), settings);
        logger.debug("Saved config to disk");
    }

    public static boolean copy(InputStream source , String destination) {
        boolean success = true;

        logger.info("Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.error(ex);
            success = false;
        }

        return success;

    }

    private static void writeDefaultConfigToFile(File newFile) throws NullPointerException, IOException {
        InputStream source = (Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(
                "assets/chonk/" + identifier + ".yml")));

        // Copy the file and overwrite if it already exists
        System.out.println(source.toString());
        copy(source, newFile.getCanonicalPath());
    }

    public static String getWorldNameFromObj(ServerWorld world) {
        return world.getRegistryKey().getValue().toString();
    }

    public static void setConfig() throws IOException, IllegalArgumentException {
        // Get a list of worlds loaded by the server
        Iterable<ServerWorld> worldObjects = server.getWorlds();
        for (ServerWorld world : worldObjects) {
            String worldName = getWorldNameFromObj(world);
            loadedWorlds.add(worldName);
        }
        logger.info("Loaded worlds: " + loadedWorlds.toString());

        readConfig();

        Chonk.setExpiryTicks(settings.getLife());
        potLevel = PotLevel.valueOf(settings.getPot().toUpperCase(Locale.ENGLISH));
        potType = PotType.valueOf(settings.getPotType().toUpperCase(Locale.ENGLISH));

        acceptedBlocks.add(Blocks.REDSTONE_WIRE);
        acceptedBlocks.add(Blocks.REPEATER);
        acceptedBlocks.add(Blocks.COMPARATOR);
        acceptedBlocks.add(Blocks.POWERED_RAIL);
        acceptedBlocks.add(Blocks.ACTIVATOR_RAIL);
        acceptedBlocks.add(Blocks.OBSERVER);
        acceptedBlocks.add(Blocks.PISTON);
        acceptedBlocks.add(Blocks.PISTON_HEAD);
        acceptedBlocks.add(Blocks.MOVING_PISTON);
        acceptedBlocks.add(Blocks.STICKY_PISTON);
        acceptedBlocks.add(Blocks.NOTE_BLOCK);
        acceptedBlocks.add(Blocks.SLIME_BLOCK);
        acceptedBlocks.add(Blocks.HONEY_BLOCK);
        acceptedBlocks.add(Blocks.REDSTONE_BLOCK);
        acceptedBlocks.add(Blocks.REDSTONE_LAMP);
        acceptedBlocks.add(Blocks.DROPPER);
        acceptedBlocks.add(Blocks.DISPENSER);

        logger.info("Finished initializing config");
    }

    public static void setChunk(String worldName, ChunkPos pos, Boolean state) throws IOException {
        // (De)Register a chunk in the system
        List<Integer> posL = Arrays.asList(pos.x, pos.z);
        if (loadedWorlds.contains(worldName)) {
            Map<List<Integer>, Boolean> chunkList = worldMap.computeIfAbsent(worldName, v -> new HashMap<>());
            if (state) {
                chunkList.computeIfAbsent(posL, v -> true);
            } else {
                chunkList.remove(posL);
            }
            saveConfig();
        }
    }

    public static Boolean checkChunkRegistered(String worldName, ChunkPos pos) {
        // See if a chunk is registered in the system
        List<Integer> posL = Arrays.asList(pos.x, pos.z);
        if (loadedWorlds.contains(worldName)) {
            if (worldMap.containsKey(worldName)) {
                Map<List<Integer>, Boolean> chunkList = worldMap.get(worldName);
                return chunkList.containsKey(posL);
            }
        }
        return false;
    }

    public static Boolean checkChunk(ServerWorld world, ChunkPos pos) throws NullPointerException {
        switch (Config.getPotLevel()) {
            case DISABLE:
                return true;
            case INCLUDE:
                if (Config.checkChunkRegistered(Config.getWorldNameFromObj(world), pos)) return true;
                break;
            case EXCLUDE:
                if (!Config.checkChunkRegistered(Config.getWorldNameFromObj(world), pos)) return true;
                break;
        }
        return false;
    }

    public enum PotLevel {
        INCLUDE,
        EXCLUDE,
        DISABLE
    }

    public enum PotType {
        OAK_SAPLING(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING),
        SPRUCE_SAPLING(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING),
        BIRCH_SAPLING(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING),
        JUNGLE_SAPLING(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING),
        ACACIA_SAPLING(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING),
        DARK_OAK_SAPLING(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING),
        FERN(Blocks.FERN, Blocks.POTTED_FERN),
        DANDELION(Blocks.DANDELION, Blocks.POTTED_DANDELION),
        POPPY(Blocks.POPPY, Blocks.POTTED_POPPY),
        BLUE_ORCHID(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID),
        ALLIUM(Blocks.ALLIUM, Blocks.POTTED_ALLIUM),
        AZURE_BLUET(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET),
        RED_TULIP(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP),
        ORANGE_TULIP(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP),
        WHITE_TULIP(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP),
        PINK_TULIP(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP),
        OXEYE_DAISY(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY),
        CORNFLOWER(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER),
        LILY_OF_THE_VALLEY(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY),
        WITHER_ROSE(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE),
        RED_MUSHROOM(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM),
        BROWN_MUSHROOM(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM),
        DEAD_BUSH(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH),
        CACTUS(Blocks.CACTUS, Blocks.POTTED_CACTUS);

        public final Block plant;
        public final Block potPlant;

        private PotType(Block plant, Block potPlant) {
            this.plant = plant;
            this.potPlant = potPlant;
        }
    }

}
