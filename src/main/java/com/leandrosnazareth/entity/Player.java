package com.leandrosnazareth.entity;


import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.leandrosnazareth.Game;
import com.leandrosnazareth.input.KeyboardHandler;
import com.leandrosnazareth.input.MouseHandler;
import com.leandrosnazareth.render.Camera;
import com.leandrosnazareth.render.ShaderProgram;
import com.leandrosnazareth.world.World;

public class Player {
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;
    private static final float EYE_HEIGHT = 1.6f;
    private static final float WALK_SPEED = 4.3f;
    private static final float RUN_SPEED = 6.0f;
    private static final float JUMP_FORCE = 8.0f;
    private static final float GRAVITY = 20.0f;
    
    private final Vector3f position;
    private final Vector3f velocity;
    private final Camera camera;
    private final World world;
    
    private boolean onGround;
    private boolean isRunning;
    
    public Player(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
        this.position = new Vector3f();
        this.velocity = new Vector3f();
        this.onGround = false;
        this.isRunning = false;
    }
    
    public void update(KeyboardHandler keyboard, MouseHandler mouse) {
        // Handle movement
        handleMovement(keyboard);
        
        // Handle jumping
        if (onGround && keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            velocity.y = JUMP_FORCE;
            onGround = false;
        }
        
        // Apply gravity
        velocity.y -= GRAVITY * Game.getDeltaTime();
        
        // Move player
        move(velocity.x * Game.getDeltaTime(), 
             velocity.y * Game.getDeltaTime(), 
             velocity.z * Game.getDeltaTime());
        
        // Update camera position
        camera.setPosition(position.x, position.y + EYE_HEIGHT, position.z);
        camera.setPitch(mouse.getPitch());
        camera.setYaw(mouse.getYaw());
    }
    
    private void handleMovement(KeyboardHandler keyboard) {
        float speed = isRunning ? RUN_SPEED : WALK_SPEED;
        
        float moveX = 0;
        float moveZ = 0;
        
        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_W)) {
            moveZ -= speed;
        }
        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_S)) {
            moveZ += speed;
        }
        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) {
            moveX -= speed;
        }
        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) {
            moveX += speed;
        }
        
        // Normalize diagonal movement
        if (moveX != 0 && moveZ != 0) {
            float len = (float) Math.sqrt(moveX * moveX + moveZ * moveZ);
            moveX /= len;
            moveZ /= len;
        }
        
        velocity.x = moveX;
        velocity.z = moveZ;
        
        // Check if running
        isRunning = keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT);
    }
    
    private void move(float dx, float dy, float dz) {
        // TODO: Implement collision detection with blocks
        
        // Simple collision for now - just prevent going below y=0
        if (position.y + dy < 0) {
            dy = -position.y;
            velocity.y = 0;
            onGround = true;
        }
        
        position.add(dx, dy, dz);
    }
    
    public void render(ShaderProgram shader) {
        // Player model rendering would go here
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
}