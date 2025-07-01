package com.leandrosnazareth.world;

public class Block {
    private BlockType type;

    public Block(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public boolean isSolid() {
        return type != BlockType.AIR;
    }
}

public enum BlockType {
    AIR(0, 0, 0, 0, 0, 0),
    GRASS(0, 0, 2, 2, 2, 3),
    DIRT(2, 0, 2, 2, 2, 2),
    STONE(1, 0, 1, 1, 1, 1),
    SAND(2, 1, 2, 2, 2, 2),
    WOOD(4, 0, 4, 4, 5, 5),
    LEAVES(3, 0, 3, 3, 3, 3);

    private final int[] textureFaces;

    BlockType(int top, int bottom, int front, int back, int left, int right) {
        textureFaces = new int[] { top, bottom, front, back, left, right };
    }

    public int getTextureX(int face) {
        return textureFaces[face] % 16;
    }

    public int getTextureY(int face) {
        return textureFaces[face] / 16;
    }
}