package com.leandrosnazareth;


import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import com.leandrosnazareth.entity.Player;
import com.leandrosnazareth.input.KeyboardHandler;
import com.leandrosnazareth.input.MouseHandler;
import com.leandrosnazareth.render.Camera;
import com.leandrosnazareth.render.ShaderProgram;
import com.leandrosnazareth.world.World;

public class Game {
    private World world;
    private Player player;
    private Camera camera;
    private ShaderProgram shader;
    private KeyboardHandler keyboard;
    private MouseHandler mouse;
    
    private boolean isRunning = true;
    
    public void init() {
        // Initialize shader
        shader = new ShaderProgram();
        shader.createVertexShader("resources/shaders/vertex.glsl");
        shader.createFragmentShader("resources/shaders/fragment.glsl");
        shader.link();
        
        // Create projection matrix
        Matrix4f projection = new Matrix4f().perspective(
            (float) Math.toRadians(70.0f), 
            1280.0f / 720.0f, 
            0.1f, 
            1000.0f
        );
        shader.setUniform("projection", projection);
        
        // Initialize camera
        camera = new Camera();
        
        // Initialize world
        world = new World();
        world.generateWorld();
        
        // Initialize player
        player = new Player(world, camera);
        player.setPosition(0, 20, 0);
        
        // Initialize input handlers
        keyboard = new KeyboardHandler();
        mouse = new MouseHandler(camera);
        
        // Set callbacks
        GLFW.glfwSetKeyCallback(Main.getWindow(), keyboard);
        GLFW.glfwSetCursorPosCallback(Main.getWindow(), mouse);
        GLFW.glfwSetMouseButtonCallback(Main.getWindow(), mouse);
        
        // Hide and capture mouse
        GLFW.glfwSetInputMode(Main.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }
    
    public void update() {
        if (!isRunning) return;
        
        // Update player
        player.update(keyboard, mouse);
        
        // Update camera
        camera.update();
        
        // Update world
        world.update(player.getPosition());
    }
    
    public void render() {
        shader.bind();
        
        // Create view matrix from camera
        Matrix4f view = camera.getViewMatrix();
        shader.setUniform("view", view);
        
        // Render world
        world.render(shader);
        
        // Render player
        player.render(shader);
        
        shader.unbind();
    }
    
    public void cleanup() {
        isRunning = false;
        
        if (shader != null) {
            shader.cleanup();
        }
        
        if (world != null) {
            world.cleanup();
        }
    }
    
    public static long getWindow() {
        return Main.getWindow();
    }
}