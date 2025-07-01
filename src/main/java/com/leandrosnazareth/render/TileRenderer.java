package com.leandrosnazareth.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.leandrosnazareth.util.FileUtils;
import com.leandrosnazareth.world.BlockType;

public class TileRenderer {
    private int vaoId;
    private int vboId;
    private int iboId;
    private int vertexCount;
    private Texture texture;

    public TileRenderer() throws Exception {
        texture = new Texture(FileUtils.loadResource("/textures/tiles.png"));

        float[] vertices = {
                // Position (x, y, z) Texture Coords (x, y)
                0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // Top right
                0.5f, -0.5f, 0.0f, 1.0f, 1.0f, // Bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // Bottom left
                -0.5f, 0.5f, 0.0f, 0.0f, 0.0f // Top left
        };

        int[] indices = {
                0, 1, 3, // First triangle
                1, 2, 3 // Second triangle
        };

        vertexCount = indices.length;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();

            IntBuffer indicesBuffer = stack.mallocInt(indices.length);
            indicesBuffer.put(indices).flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            iboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // Position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // Texture coordinate attribute
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void render(BlockType blockType, Vector2f position, Renderer renderer) {
        Matrix4f modelMatrix = new Matrix4f().translate(new Vector3f(position, 0.0f));
        renderer.getShader().setUniform("modelMatrix", modelMatrix);

        texture.bind();
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        texture.unbind();
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        glDeleteBuffers(iboId);

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        texture.cleanup();
    }
}