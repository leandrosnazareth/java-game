package com.leandrosnazareth.world;

import org.joml.Vector2f;

public enum BlockType {
    STONE(new Vector2f(0, 0)),
    AIR(null);

    private Vector2f textureCoords;

    BlockType(Vector2f textureCoords) {
        this.textureCoords = textureCoords;
    }

    public Vector2f getTextureCoords() {
        return textureCoords;
    }

    public boolean isSolid() {
        return this != AIR;
    }
}