package com.leandrosnazareth.world;

import org.joml.Vector2f;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int BLOCK_SIZE = 32;

    private Block[][] blocks;
    private Vector2f position;

    public Chunk(Vector2f position) {
        this.position = position;
        this.blocks = new Block[CHUNK_SIZE][CHUNK_SIZE];
    }

    public void generate(WorldGenerator generator) {
        generator.generateChunk(this);
    }

    public Block getBlock(int x, int y) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_SIZE) {
            return null;
        }
        return blocks[x][y];
    }

    public void setBlock(int x, int y, Block block) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            blocks[x][y] = block;
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getWorldX() {
        return (int) (position.x * CHUNK_SIZE * BLOCK_SIZE);
    }

    public int getWorldY() {
        return (int) (position.y * CHUNK_SIZE * BLOCK_SIZE);
    }
}