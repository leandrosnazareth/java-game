package com.leandrosnazareth.render;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram() {
        programId = GL20.glCreateProgram();
    }

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);

        try (BufferedReader reader = new BufferedReader(new FileReader(shaderCode))) {
            StringBuilder shaderSource = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            GL20.glShaderSource(shaderId, shaderSource.toString());
        } catch (IOException e) {
            System.err.println("Could not read shader file: " + shaderCode);
            e.printStackTrace();
            return -1;
        }

        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
            return -1;
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }

    public void setUniform(String name, Matrix4f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            value.get(matrixBuffer);
            GL20.glUniformMatrix4fv(location, false, matrixBuffer);
        }
    }

    public void setUniform(String name, int value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform1i(location, value);
        }
    }
}