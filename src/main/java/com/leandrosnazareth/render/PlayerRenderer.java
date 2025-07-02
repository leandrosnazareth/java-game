package com.leandrosnazareth.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PlayerRenderer {
    private int vaoId;
    private int vboId;
    private int iboId;
    private int vertexCount;
    private Texture texture;

    public PlayerRenderer() throws Exception {
        System.out.println("Creating PlayerRenderer...");
        
        // Cria uma textura simples verde para o player
        ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 32 * 4);
        for (int i = 0; i < 32 * 32; i++) {
            buffer.put((byte) 0);   // R - vermelho 0
            buffer.put((byte) 255); // G - verde máximo
            buffer.put((byte) 0);   // B - azul 0
            buffer.put((byte) 255); // A - alpha máximo
        }
        buffer.flip();
        texture = new Texture(buffer, 32, 32);

        // Vertices para um quadrado 32x32
        float[] vertices = {
                // Position (x, y, z) Texture Coords (x, y)
                32.0f, 32.0f, 0.0f,   1.0f, 0.0f, // Top right
                32.0f, 0.0f, 0.0f,    1.0f, 1.0f, // Bottom right  
                0.0f, 0.0f, 0.0f,     0.0f, 1.0f, // Bottom left
                0.0f, 32.0f, 0.0f,    0.0f, 0.0f  // Top left
        };

        int[] indices = {
                0, 1, 3, // First triangle
                1, 2, 3  // Second triangle
        };

        vertexCount = indices.length;

        // Cria VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Cria VBO para vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture coordinate attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Cria IBO
        iboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Unbind
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        System.out.println("PlayerRenderer created successfully!");
    }

    public void render(Vector2f position, Renderer renderer) {
        Matrix4f modelMatrix = new Matrix4f().translate(new Vector3f(position, 0.0f));
        try {
            renderer.getShader().setUniform("modelMatrix", modelMatrix);
        } catch (Exception e) {
            System.err.println("Error setting model matrix for player: " + e.getMessage());
            return;
        }

        texture.bind();
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        texture.unbind();
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(iboId);
        if (texture != null) {
            texture.cleanup();
        }
    }
}