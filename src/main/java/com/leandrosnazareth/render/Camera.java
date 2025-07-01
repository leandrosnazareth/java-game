package com.leandrosnazareth.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f rotation;
    private final Matrix4f viewMatrix;

    private float pitch;
    private float yaw;
    private float roll;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        viewMatrix = new Matrix4f();
        pitch = 0;
        yaw = 0;
        roll = 0;
    }

    public void update() {
        viewMatrix.identity()
                .rotateX((float) Math.toRadians(pitch))
                .rotateY((float) Math.toRadians(yaw))
                .translate(-position.x, -position.y, -position.z);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(yaw)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(yaw)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(yaw - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(yaw - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
}