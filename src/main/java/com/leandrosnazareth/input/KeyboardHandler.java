package com.leandrosnazareth.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyboardHandler extends GLFWKeyCallback {
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();
    private static final Map<Integer, Boolean> prevKeyStates = new HashMap<>();

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            keyStates.put(key, true);
        } else if (action == GLFW.GLFW_RELEASE) {
            keyStates.put(key, false);
        }
    }

    public static void update() {
        prevKeyStates.clear();
        prevKeyStates.putAll(keyStates);
    }

    public static boolean isKeyDown(int keyCode) {
        return keyStates.getOrDefault(keyCode, false);
    }

    public static boolean isKeyPressed(int keyCode) {
        return isKeyDown(keyCode) && !prevKeyStates.getOrDefault(keyCode, false);
    }

    public static boolean isKeyReleased(int keyCode) {
        return !isKeyDown(keyCode) && prevKeyStates.getOrDefault(keyCode, false);
    }
}