package com.leandrosnazareth.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import com.leandrosnazareth.render.Camera;

public class MouseHandler {
    private static double lastX = 0;
    private static double lastY = 0;
    private static double deltaX = 0;
    private static double deltaY = 0;

    private static boolean[] buttonStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
    private static boolean[] prevButtonStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

    private final Camera camera;
    private float pitch;
    private float yaw;

    public MouseHandler(Camera camera) {
        this.camera = camera;
        this.pitch = 0;
        this.yaw = 0;
    }

    public GLFWCursorPosCallback getCursorPosCallback() {
        return new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                deltaX = xpos - lastX;
                deltaY = lastY - ypos; // Reversed since y-coordinates range from bottom to top
                lastX = xpos;
                lastY = ypos;

                // Update camera rotation
                float sensitivity = 0.1f;
                yaw += (float) deltaX * sensitivity;
                pitch += (float) deltaY * sensitivity;

                // Constrain pitch
                if (pitch > 89.0f)
                    pitch = 89.0f;
                if (pitch < -89.0f)
                    pitch = -89.0f;

                camera.setYaw(yaw);
                camera.setPitch(pitch);
            }
        };
    }

    public GLFWMouseButtonCallback getMouseButtonCallback() {
        return new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button >= 0 && button < buttonStates.length) {
                    buttonStates[button] = action != GLFW.GLFW_RELEASE;
                }
            }
        };
    }

    public static void update() {
        System.arraycopy(buttonStates, 0, prevButtonStates, 0, buttonStates.length);
        deltaX = 0;
        deltaY = 0;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public static boolean isButtonDown(int button) {
        if (button >= 0 && button < buttonStates.length) {
            return buttonStates[button];
        }
        return false;
    }

    public static boolean isButtonPressed(int button) {
        if (button >= 0 && button < buttonStates.length) {
            return buttonStates[button] && !prevButtonStates[button];
        }
        return false;
    }

    public static boolean isButtonReleased(int button) {
        if (button >= 0 && button < buttonStates.length) {
            return !buttonStates[button] && prevButtonStates[button];
        }
        return false;
    }
}