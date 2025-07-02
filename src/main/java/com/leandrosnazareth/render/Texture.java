package com.leandrosnazareth.render;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

public class Texture {
    private final int id;
    private final int width;
    private final int height;

    public Texture(String fileName) throws Exception {
        ByteBuffer buf;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(fileName, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + fileName + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
        }

        this.id = createTexture(buf);

        stbi_image_free(buf);
    }

    public Texture(ByteBuffer buffer, int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        this.id = createTexture(buffer);
    }

    // Construtor para carregar recurso do classpath
    public static Texture loadFromResource(String resourcePath) throws Exception {
        java.net.URL resourceUrl = Texture.class.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }

        // Converte URL para um caminho que STB pode entender
        java.nio.file.Path path;
        try {
            path = java.nio.file.Paths.get(resourceUrl.toURI());
            return new Texture(path.toString());
        } catch (java.net.URISyntaxException e) {
            // Se estiver dentro de um JAR, extraia o recurso para um arquivo temporário
            try (java.io.InputStream is = Texture.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new Exception("Resource not found: " + resourcePath);
                }

                java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("texture", ".png");
                java.nio.file.Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                tempFile.toFile().deleteOnExit();
                return new Texture(tempFile.toString());
            }
        }
    }

    private int createTexture(ByteBuffer buf) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}