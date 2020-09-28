package me.logwet.chonk.config.object;

public class Chunk {
    private int x;
    private int z;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Chunk() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
