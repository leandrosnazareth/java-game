package com.leandrosnazareth.world;

import org.joml.Vector2f;

public class Block {
    private BlockType type;
    private Vector2f position;

    public Block(BlockType type, Vector2f position) {
        this.type = type;
        this.position = position;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public Vector2f getPosition() {
        return position;
    }

    public boolean isSolid() {
        return type.isSolid();
    }
}