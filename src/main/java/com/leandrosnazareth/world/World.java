package com.leandrosnazareth.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.leandrosnazareth.main.InputHandler;
import com.leandrosnazareth.render.Renderer;
import com.leandrosnazareth.render.TileRenderer;

public class World {
    private static final int RENDER_DISTANCE = 2;

    private List<Chunk> chunks;
    private WorldGenerator generator;
    private TileRenderer tileRenderer;

    private Vector2f playerPosition;
    private Vector2f currentChunk;

    public World() {
        chunks = new ArrayList<>();
        generator = new WorldGenerator();
        playerPosition = new Vector2f(0, 0);
        currentChunk = new Vector2f(0, 0);
    }

    public void init() {
        try {
            tileRenderer = new TileRenderer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load initial chunks around the player
        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {
                Vector2f chunkPos = new Vector2f(currentChunk.x + x, currentChunk.y + y);
                loadChunk(chunkPos);
            }
        }
    }

    private void loadChunk(Vector2f chunkPos) {
        Chunk chunk = new Chunk(chunkPos);
        chunk.generate(generator);
        chunks.add(chunk);
    }

    public void update(float delta, InputHandler input) {
        // Player movement
        float speed = 200.0f * delta;
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_W)) {
            playerPosition.y -= speed;
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_S)) {
            playerPosition.y += speed;
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_A)) {
            playerPosition.x -= speed;
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_D)) {
            playerPosition.x += speed;
        }

        // Update current chunk
        Vector2f newChunk = new Vector2f(
                (int) Math.floor(playerPosition.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE)),
                (int) Math.floor(playerPosition.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE)));

        if (!newChunk.equals(currentChunk)) {
            currentChunk.set(newChunk);
            // TODO: Load/unload chunks based on new position
        }

        // Block breaking/placing
        if (input.isLeftMousePressed()) {
            Vector2f mousePos = input.getMousePos();
            Vector2f worldPos = screenToWorld(mousePos);
            breakBlock(worldPos);
        }

        if (input.isRightMousePressed()) {
            Vector2f mousePos = input.getMousePos();
            Vector2f worldPos = screenToWorld(mousePos);
            placeBlock(worldPos);
        }
    }

    private Vector2f screenToWorld(Vector2f screenPos) {
        // Simple conversion - in a real game you'd need camera transforms
        return new Vector2f(
                screenPos.x + playerPosition.x,
                screenPos.y + playerPosition.y);
    }

    private void breakBlock(Vector2f worldPos) {
        int chunkX = (int) Math.floor(worldPos.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));
        int chunkY = (int) Math.floor(worldPos.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));

        for (Chunk chunk : chunks) {
            if (chunk.getPosition().x == chunkX && chunk.getPosition().y == chunkY) {
                int blockX = (int) ((worldPos.x - chunkX * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);
                int blockY = (int) ((worldPos.y - chunkY * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);

                Block block = chunk.getBlock(blockX, blockY);
                if (block != null && block.isSolid()) {
                    block.setType(BlockType.AIR);
                }
                break;
            }
        }
    }

    private void placeBlock(Vector2f worldPos) {
        int chunkX = (int) Math.floor(worldPos.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));
        int chunkY = (int) Math.floor(worldPos.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));

        for (Chunk chunk : chunks) {
            if (chunk.getPosition().x == chunkX && chunk.getPosition().y == chunkY) {
                int blockX = (int) ((worldPos.x - chunkX * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);
                int blockY = (int) ((worldPos.y - chunkY * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);

                Block block = chunk.getBlock(blockX, blockY);
                if (block != null && !block.isSolid()) {
                    block.setType(BlockType.STONE);
                }
                break;
            }
        }
    }

    public void render(Renderer renderer) {
        for (Chunk chunk : chunks) {
            renderChunk(chunk, renderer);
        }
    }

    private void renderChunk(Chunk chunk, Renderer renderer) {
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                Block block = chunk.getBlock(x, y);
                if (block != null && block.isSolid()) {
                    Vector2f blockPos = new Vector2f(
                            chunk.getWorldX() + x * Chunk.BLOCK_SIZE - playerPosition.x,
                            chunk.getWorldY() + y * Chunk.BLOCK_SIZE - playerPosition.y);
                    tileRenderer.render(block.getType(), blockPos, renderer);
                }
            }
        }
    }

    public void cleanup() {
        tileRenderer.cleanup();
    }
}