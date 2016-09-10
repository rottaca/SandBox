package com.rottaca.sandbox.data;

public class Bullet {
    private float x, y;
    private float damage;
    private float speedX, speedY;

    public Bullet(float y, float x, float damage, float speedX, float speedY) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void update(float accX, float accY) {
        speedX += accX;
        speedY += accY;
        x += speedX;
        y += speedY;
    }

    public float getDamage() {
        return damage;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
