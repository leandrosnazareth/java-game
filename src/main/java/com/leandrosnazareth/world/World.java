package com.leandrosnazareth.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.leandrosnazareth.main.InputHandler;
import com.leandrosnazareth.render.PlayerRenderer;
import com.leandrosnazareth.render.Renderer;
import com.leandrosnazareth.render.TileRenderer;

public class World {
    private static final int RENDER_DISTANCE = 2;

    private List<Chunk> chunks;
    private WorldGenerator generator;
    private TileRenderer tileRenderer;
    private PlayerRenderer playerRenderer;

    private Vector2f playerPosition;
    private Vector2f cameraOffset;
    private Vector2f currentChunk;

    public World() {
        chunks = new ArrayList<>();
        generator = new WorldGenerator();
        playerPosition = new Vector2f(640, 360); // Começa no centro da tela
        cameraOffset = new Vector2f(0, 0);
        currentChunk = new Vector2f(0, 0);
    }

    public void init() {
        System.out.println("Initializing renderers...");
        try {
            tileRenderer = new TileRenderer();
            playerRenderer = new PlayerRenderer();
            System.out.println("Renderers initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing renderers: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Loading initial chunks...");
        // Load initial chunks around the player
        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {
                Vector2f chunkPos = new Vector2f(currentChunk.x + x, currentChunk.y + y);
                loadChunk(chunkPos);
            }
        }
        System.out.println("World initialization complete!");
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
            System.out.println("Moving UP: " + playerPosition);
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_S)) {
            playerPosition.y += speed;
            System.out.println("Moving DOWN: " + playerPosition);
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_A)) {
            playerPosition.x -= speed;
            System.out.println("Moving LEFT: " + playerPosition);
        }
        if (input.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_D)) {
            playerPosition.x += speed;
            System.out.println("Moving RIGHT: " + playerPosition);
        }

        // Atualiza camera offset para seguir o player
        cameraOffset.x = playerPosition.x - 640; // Centraliza horizontalmente
        cameraOffset.y = playerPosition.y - 360; // Centraliza verticalmente

        // Update current chunk
        Vector2f newChunk = new Vector2f(
                (int) Math.floor(playerPosition.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE)),
                (int) Math.floor(playerPosition.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE)));

        if (!newChunk.equals(currentChunk)) {
            currentChunk.set(newChunk);
            System.out.println("Player moved to chunk: " + currentChunk);
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
        return new Vector2f(
                screenPos.x + cameraOffset.x,
                screenPos.y + cameraOffset.y);
    }

    private void breakBlock(Vector2f worldPos) {
        System.out.println("Breaking block at world position: " + worldPos);

        int chunkX = (int) Math.floor(worldPos.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));
        int chunkY = (int) Math.floor(worldPos.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));

        for (Chunk chunk : chunks) {
            if (chunk.getPosition().x == chunkX && chunk.getPosition().y == chunkY) {
                int blockX = (int) ((worldPos.x - chunkX * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);
                int blockY = (int) ((worldPos.y - chunkY * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);

                // Verifica se o bloco está dentro dos limites do chunk
                if (blockX >= 0 && blockX < Chunk.CHUNK_SIZE && blockY >= 0 && blockY < Chunk.CHUNK_SIZE) {
                    Block block = chunk.getBlock(blockX, blockY);
                    if (block != null && block.isSolid()) {
                        block.setType(BlockType.AIR);
                        System.out.println("Block broken at chunk coords: " + blockX + "," + blockY);
                    }
                }
                break;
            }
        }
    }

    private void placeBlock(Vector2f worldPos) {
        System.out.println("Placing block at world position: " + worldPos);

        int chunkX = (int) Math.floor(worldPos.x / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));
        int chunkY = (int) Math.floor(worldPos.y / (Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE));

        for (Chunk chunk : chunks) {
            if (chunk.getPosition().x == chunkX && chunk.getPosition().y == chunkY) {
                int blockX = (int) ((worldPos.x - chunkX * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);
                int blockY = (int) ((worldPos.y - chunkY * Chunk.CHUNK_SIZE * Chunk.BLOCK_SIZE) / Chunk.BLOCK_SIZE);

                // Verifica se o bloco está dentro dos limites do chunk
                if (blockX >= 0 && blockX < Chunk.CHUNK_SIZE && blockY >= 0 && blockY < Chunk.CHUNK_SIZE) {
                    Block block = chunk.getBlock(blockX, blockY);
                    if (block != null && !block.isSolid()) {
                        block.setType(BlockType.STONE);
                        System.out.println("Block placed at chunk coords: " + blockX + "," + blockY);
                    }
                }
                break;
            }
        }
    }

    public void render(Renderer renderer) {
        // Renderiza os chunks primeiro (no fundo)
        for (Chunk chunk : chunks) {
            renderChunk(chunk, renderer);
        }

        // Renderiza o player por último (na frente)
        renderPlayer(renderer);
    }

    private void renderPlayer(Renderer renderer) {
        // Posição do player relativa à camera
        Vector2f playerScreenPos = new Vector2f(
                playerPosition.x - cameraOffset.x - 16, // -16 para centralizar (32/2)
                playerPosition.y - cameraOffset.y - 16 // -16 para centralizar (32/2)
        );

        if (playerRenderer != null) {
            playerRenderer.render(playerScreenPos, renderer);
        } else {
            System.err.println("PlayerRenderer is null!");
        }
    }

    private void renderChunk(Chunk chunk, Renderer renderer) {
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                Block block = chunk.getBlock(x, y);
                if (block != null && block.isSolid()) {
                    // Posição do bloco relativa à camera
                    Vector2f blockWorldPos = new Vector2f(
                            chunk.getWorldX() + x * Chunk.BLOCK_SIZE,
                            chunk.getWorldY() + y * Chunk.BLOCK_SIZE);

                    Vector2f blockScreenPos = new Vector2f(
                            blockWorldPos.x - cameraOffset.x,
                            blockWorldPos.y - cameraOffset.y);

                    if (tileRenderer != null) {
                        tileRenderer.render(block.getType(), blockScreenPos, renderer);
                    } else {
                        System.err.println("TileRenderer is null!");
                    }
                }
            }
        }
    }

    // Métodos getter para debug e outras funcionalidades
    public Vector2f getPlayerPosition() {
        return new Vector2f(playerPosition);
    }

    public Vector2f getCameraOffset() {
        return new Vector2f(cameraOffset);
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public void cleanup() {
        System.out.println("Cleaning up World...");
        if (tileRenderer != null) {
            tileRenderer.cleanup();
        }
        if (playerRenderer != null) {
            playerRenderer.cleanup();
        }
        System.out.println("World cleanup complete!");
    }
}