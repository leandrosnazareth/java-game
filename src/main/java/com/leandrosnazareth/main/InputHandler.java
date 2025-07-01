package com.leandrosnazareth.main;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class InputHandler {
    private long windowHandle;
    private boolean[] keys;
    private Vector2f mousePos;
    private boolean leftMousePressed;
    private boolean rightMousePressed;

    public InputHandler(GameWindow window) {
        if (window == null || window.getWindowHandle() == 0) {
            throw new IllegalStateException("Window not initialized");
        }

        this.windowHandle = window.getWindowHandle();
        this.keys = new boolean[GLFW.GLFW_KEY_LAST];
        this.mousePos = new Vector2f();

        // Altere o nome do parâmetro 'window' para 'windowHandle' nos callbacks
        GLFW.glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW.GLFW_RELEASE;
            }
        });

        GLFW.glfwSetCursorPosCallback(windowHandle, (win, xpos, ypos) -> {
            mousePos.set((float) xpos, (float) ypos);
        });

        GLFW.glfwSetMouseButtonCallback(windowHandle, (win, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                leftMousePressed = action != GLFW.GLFW_RELEASE;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                rightMousePressed = action != GLFW.GLFW_RELEASE;
            }
        });
    }

    public boolean isKeyPressed(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
    }

    public Vector2f getMousePos() {
        return mousePos;
    }

    public boolean isLeftMousePressed() {
        return leftMousePressed;
    }

    public boolean isRightMousePressed() {
        return rightMousePressed;
    }
}