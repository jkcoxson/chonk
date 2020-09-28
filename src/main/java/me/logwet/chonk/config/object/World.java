package me.logwet.chonk.config.object;

import java.util.List;

public class World {
    private String name;
    private List<Chunk> chunks;

    public World(String name, List<Chunk> chunks) {
        this.name = name;
        this.chunks = chunks;
    }

    public World() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<Chunk> chunks) {
        this.chunks = chunks;
    }
}
