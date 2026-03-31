package net.fosgerau.game.model;

public class Robot {
    private int x;
    private int y;
    private String id;
    private int battery; // 0-100
    private int structuralIntegrity; // 0-100 (health)
    private int ammo; // 0-100

    public Robot(String id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.battery = 100;
        this.structuralIntegrity = 100;
        this.ammo = 100;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = Math.max(0, Math.min(100, battery));
    }

    public int getStructuralIntegrity() {
        return structuralIntegrity;
    }

    public void setStructuralIntegrity(int integrity) {
        this.structuralIntegrity = Math.max(0, Math.min(100, integrity));
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = Math.max(0, Math.min(100, ammo));
    }

    public void moveLeft() {
        this.x -= 1;
        consumeBattery(5);
    }

    public void moveRight() {
        this.x += 1;
        consumeBattery(5);
    }

    public void moveUp() {
        this.y -= 1;
        consumeBattery(5);
    }

    public void moveDown() {
        this.y += 1;
        consumeBattery(5);
    }

    private void consumeBattery(int amount) {
        this.battery = Math.max(0, this.battery - amount);
    }

    public void reset() {
        this.battery = 100;
        this.structuralIntegrity = 100;
        this.ammo = 100;
    }
}

