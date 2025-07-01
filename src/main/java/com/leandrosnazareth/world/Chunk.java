package com.leandrosnazareth.world;

import java.util.ArrayList;
import java.util.List;

import com.leandrosnazareth.render.Model;
import com.leandrosnazareth.render.ShaderProgram;

public class Chunk {
    public static final int SIZE = 16;
    public static final int HEIGHT = 256;

    private int x, y, z;
    private Block[][][] blocks;
    private Model model;
    private boolean modified;

    public Chunk(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocks = new Block[SIZE][HEIGHT][SIZE];
        this.modified = true;

        // Initialize with air blocks
        for (int bx = 0; bx < SIZE; bx++) {
            for (int by = 0; by < HEIGHT; by++) {
                for (int bz = 0; bz < SIZE; bz++) {
                    blocks[bx][by][bz] = new Block(BlockType.AIR);
                }
            }
        }
    }

    public void render(ShaderProgram shader) {
        if (modified) {
            rebuildMesh();
            modified = false;
        }

        if (model != null) {
            model.render(shader);
        }
    }

    private void rebuildMesh() {
        // TODO: Implement greedy meshing or similar optimization
        // For now we'll just create a simple mesh

        List<Float> vertices = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int index = 0;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < SIZE; z++) {
                    Block block = blocks[x][y][z];
                    if (block.getType() == BlockType.AIR)
                        continue;

                    // World position of the block
                    float wx = this.x * SIZE + x;
                    float wy = y;
                    float wz = this.z * SIZE + z;

                    // Check adjacent blocks to determine which faces to render
                    // Front face
                    if (z == SIZE - 1 || blocks[x][y][z + 1].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 0, block.getType());
                        index += 4;
                    }

                    // Back face
                    if (z == 0 || blocks[x][y][z - 1].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 1, block.getType());
                        index += 4;
                    }

                    // Right face
                    if (x == SIZE - 1 || blocks[x + 1][y][z].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 2, block.getType());
                        index += 4;
                    }

                    // Left face
                    if (x == 0 || blocks[x - 1][y][z].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 3, block.getType());
                        index += 4;
                    }

                    // Top face
                    if (y == HEIGHT - 1 || blocks[x][y + 1][z].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 4, block.getType());
                        index += 4;
                    }

                    // Bottom face
                    if (y == 0 || blocks[x][y - 1][z].getType() == BlockType.AIR) {
                        addFace(vertices, texCoords, indices, index, wx, wy, wz, 5, block.getType());
                        index += 4;
                    }
                }
            }
        }

        if (model != null) {
            model.cleanup();
        }

        if (!vertices.isEmpty()) {
            float[] verticesArray = new float[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                verticesArray[i] = vertices.get(i);
            }

            float[] texCoordsArray = new float[texCoords.size()];
            for (int i = 0; i < texCoords.size(); i++) {
                texCoordsArray[i] = texCoords.get(i);
            }

            int[] indicesArray = new int[indices.size()];
            for (int i = 0; i < indices.size(); i++) {
                indicesArray[i] = indices.get(i);
            }

            model = new Model(verticesArray, texCoordsArray, indicesArray);
        }
    }

    private void addFace(List<Float> vertices, List<Float> texCoords, List<Integer> indices,
            int index, float x, float y, float z, int face, BlockType type) {
        // Add vertices for the face
        switch (face) {
            case 0: // Front
                vertices.add(x);
                vertices.add(y);
                vertices.add(z + 1);
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z + 1);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z + 1);
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z + 1);
                break;
            case 1: // Back
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z);
                break;
            case 2: // Right
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z + 1);
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z + 1);
                break;
            case 3: // Left
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z + 1);
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z + 1);
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z);
                break;
            case 4: // Top
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z + 1);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z + 1);
                vertices.add(x + 1);
                vertices.add(y + 1);
                vertices.add(z);
                vertices.add(x);
                vertices.add(y + 1);
                vertices.add(z);
                break;
            case 5: // Bottom
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z);
                vertices.add(x + 1);
                vertices.add(y);
                vertices.add(z + 1);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z + 1);
                break;
        }

        // Add texture coordinates (simplified, would need atlas coordinates in real
        // implementation)
        float texX = type.getTextureX(face) / 16.0f;
        float texY = type.getTextureY(face) / 16.0f;
        float texSize = 1.0f / 16.0f;

        texCoords.add(texX);
        texCoords.add(texY);
        texCoords.add(texX + texSize);
        texCoords.add(texY);
        texCoords.add(texX + texSize);
        texCoords.add(texY + texSize);
        texCoords.add(texX);
        texCoords.add(texY + texSize);

        // Add indices
        indices.add(index);
        indices.add(index + 1);
        indices.add(index + 2);
        indices.add(index + 2);
        indices.add(index + 3);
        indices.add(index);
    }

    public void cleanup() {
        if (model != null) {
            model.cleanup();
        }
    }

    // Getters and setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Block getBlock(int x, int y, int z) {
        if (x < 0 || x >= SIZE || y < 0 || y >= HEIGHT || z < 0 || z >= SIZE) {
            return new Block(BlockType.AIR);
        }
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        if (x >= 0 && x < SIZE && y >= 0 && y < HEIGHT && z >= 0 && z < SIZE) {
            blocks[x][y][z] = block;
            modified = true;
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}