package com.leandrosnazareth.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.leandrosnazareth.render.ShaderProgram;

public class World {
    private static final int CHUNK_SIZE = 16;
    private static final int RENDER_DISTANCE = 8;

    private List<Chunk> chunks;
    private WorldGenerator generator;

    public World() {
        chunks = new ArrayList<>();
        generator = new WorldGenerator();
    }

    public void generateWorld() {
        // Generate initial chunks around the player
        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int z = -RENDER_DISTANCE; z <= RENDER_DISTANCE; z++) {
                Chunk chunk = new Chunk(x, 0, z);
                generator.generateChunk(chunk);
                chunks.add(chunk);
            }
        }
    }

    public void update(Vector3f playerPosition) {
        // TODO: Implement chunk loading/unloading based on player position
    }

    public void render(ShaderProgram shader) {
        for (Chunk chunk : chunks) {
            chunk.render(shader);
        }
    }

    public void cleanup() {
        for (Chunk chunk : chunks) {
            chunk.cleanup();
        }
    }

    public Block getBlock(int x, int y, int z) {
        int chunkX = (int) Math.floor(x / (float) CHUNK_SIZE);
        int chunkZ = (int) Math.floor(z / (float) CHUNK_SIZE);

        for (Chunk chunk : chunks) {
            if (chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                int blockX = x - chunkX * CHUNK_SIZE;
                int blockZ = z - chunkZ * CHUNK_SIZE;

                return chunk.getBlock(blockX, y, blockZ);
            }
        }

        return null;
    }

    public boolean setBlock(int x, int y, int z, Block block) {
        int chunkX = (int) Math.floor(x / (float) CHUNK_SIZE);
        int chunkZ = (int) Math.floor(z / (float) CHUNK_SIZE);

        for (Chunk chunk : chunks) {
            if (chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                int blockX = x - chunkX * CHUNK_SIZE;
                int blockZ = z - chunkZ * CHUNK_SIZE;

                chunk.setBlock(blockX, y, blockZ, block);
                chunk.setModified(true);
                return true;
            }
        }

        return false;
    }
}