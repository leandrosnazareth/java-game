package com.leandrosnazareth.world;

import org.joml.Vector2f;

public class WorldGenerator {
    public void generateChunk(Chunk chunk) {
        Vector2f chunkPos = chunk.getPosition();

        // Gera alguns blocos visíveis perto da origem
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                Vector2f blockPos = new Vector2f(
                        chunkPos.x * Chunk.CHUNK_SIZE + x,
                        chunkPos.y * Chunk.CHUNK_SIZE + y);

                BlockType type = BlockType.AIR;

                // Gera um padrão simples para testar
                if (chunkPos.x == 0 && chunkPos.y == 0) {
                    // No chunk central, cria alguns blocos de teste
                    if ((x + y) % 2 == 0 && x < 8 && y < 8) {
                        type = BlockType.STONE;
                    }
                }

                chunk.setBlock(x, y, new Block(type, blockPos));
            }
        }
        
        System.out.println("Generated chunk at: " + chunkPos);
    }
}