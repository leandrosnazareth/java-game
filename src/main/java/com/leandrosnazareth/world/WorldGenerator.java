package com.leandrosnazareth.world;

import org.joml.Vector2f;

public class WorldGenerator {
    public void generateChunk(Chunk chunk) {
        Vector2f chunkPos = chunk.getPosition();

        // Simple flat world generation
        int groundLevel = 10;

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                Vector2f blockPos = new Vector2f(
                        chunkPos.x * Chunk.CHUNK_SIZE + x,
                        chunkPos.y * Chunk.CHUNK_SIZE + y);

                BlockType type = BlockType.AIR;

                // Generate ground
                if (blockPos.y >= groundLevel) {
                    type = BlockType.STONE;
                }

                chunk.setBlock(x, y, new Block(type, blockPos));
            }
        }
    }
}