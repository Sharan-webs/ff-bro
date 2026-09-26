package com.ff.aimbot.core;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;

public class AimbotEngine {
    private volatile boolean isAimbotEnabled = false;
    private volatile boolean isHeadshotOnly = true;
    private float screenWidth;
    private float screenHeight;
    private float crosshairX;
    private float crosshairY;
    private float detectionRadius = 100f; // 10cm equivalent on screen
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public AimbotEngine(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.crosshairX = width / 2f;
        this.crosshairY = height / 2f;
    }

    public void setAimbotEnabled(boolean enabled) {
        this.isAimbotEnabled = enabled;
    }

    public void setHeadshotOnly(boolean headshot) {
        this.isHeadshotOnly = headshot;
    }

    public boolean isAimbotEnabled() {
        return isAimbotEnabled;
    }

    public boolean isHeadshotOnly() {
        return isHeadshotOnly;
    }

    // Calculate if target is within detection radius
    public boolean isTargetInRange(float targetX, float targetY) {
        if (!isAimbotEnabled) return false;

        float distX = targetX - crosshairX;
        float distY = targetY - crosshairY;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        return distance <= detectionRadius;
    }

    // Calculate aim offset to lock onto target
    public Point calculateAimOffset(float targetX, float targetY) {
        if (!isAimbotEnabled || !isTargetInRange(targetX, targetY)) {
            return new Point(0, 0);
        }

        float offsetX = targetX - crosshairX;
        float offsetY = targetY - crosshairY;

        // Smooth movement for natural feel
        float smoothFactor = 0.85f;
        int finalX = (int) (offsetX * smoothFactor);
        int finalY = (int) (offsetY * smoothFactor);

        return new Point(finalX, finalY);
    }

    // Simulate fire on target lock
    public void triggerAutoFire(CursorController cursorController) {
        if (isAimbotEnabled && cursorController != null) {
            // Simulate fire button press/release
            cursorController.simulateTouch(crosshairX, crosshairY, 50); // 50ms hold
        }
    }

    // Update detection radius based on distance
    public void updateDetectionRadius(float radius) {
        this.detectionRadius = Math.max(50f, Math.min(200f, radius));
    }

    public float getDetectionRadius() {
        return detectionRadius;
    }

    public void setCrosshairPosition(float x, float y) {
        this.crosshairX = x;
        this.crosshairY = y;
    }

    // Get screen center
    public Point getScreenCenter() {
        return new Point((int) (screenWidth / 2), (int) (screenHeight / 2));
    }
}
