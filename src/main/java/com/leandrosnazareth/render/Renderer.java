package com.leandrosnazareth.render;

import java.io.IOException;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.leandrosnazareth.util.FileUtils;

public class Renderer {
    private ShaderProgram shader;
    private Matrix4f projectionMatrix;

    public void init() throws IOException {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        shader = new ShaderProgram();
        shader.createVertexShader(FileUtils.loadResource("/shaders/vertex.glsl"));
        shader.createFragmentShader(FileUtils.loadResource("/shaders/fragment.glsl"));
        shader.link();

        projectionMatrix = new Matrix4f().ortho(0.0f, 1280.0f, 720.0f, 0.0f, -1.0f, 1.0f);
        shader.createUniform("projectionMatrix");
        shader.setUniform("projectionMatrix", projectionMatrix);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.53f, 0.81f, 0.92f, 1.0f); // Sky blue
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void cleanup() {
        shader.cleanup();
    }
}