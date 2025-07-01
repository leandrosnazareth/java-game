package com.leandrosnazareth.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class Model {
    private int vaoId;
    private int vboId;
    private int texVboId;
    private int eboId;
    private int vertexCount;

    public Model(float[] vertices, float[] texCoords, int[] indices) {
        vertexCount = indices.length;

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Vertex VBO
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, verticesBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(0);

        // Texture coordinates VBO
        FloatBuffer texCoordsBuffer = BufferUtils.createFloatBuffer(texCoords.length);
        texCoordsBuffer.put(texCoords).flip();

        texVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, texVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, texCoordsBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(1);

        // Index EBO
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        eboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void render(ShaderProgram shader) {
        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL30.glDisableVertexAttribArray(0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(vboId);
        GL30.glDeleteBuffers(texVboId);
        GL30.glDeleteBuffers(eboId);

        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }
}