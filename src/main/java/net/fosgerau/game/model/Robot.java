package net.fosgerau.game.model;

public class Robot {
    private static final int ARENA_SIZE = 30; // 30x30 grid (0-29)
    private static final int MIN_COORD = 0;
    private static final int MAX_COORD = ARENA_SIZE - 1;
    
    private int x;
    private int y;
    private String id;
    private int battery; // 0-100
    private int structuralIntegrity; // 0-100 (health)
    private int ammo; // 0-100
    private boolean boundaryHit; // Flag set when robot hits boundary

    public Robot(String id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.battery = 100;
        this.structuralIntegrity = 100;
        this.ammo = 100;
        this.boundaryHit = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = Math.max(MIN_COORD, Math.min(MAX_COORD, x));
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = Math.max(MIN_COORD, Math.min(MAX_COORD, y));
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

    public boolean isBoundaryHit() {
        return boundaryHit;
    }

    public void setBoundaryHit(boolean hit) {
        this.boundaryHit = hit;
    }

    public void moveLeft() {
        int newX = this.x - 1;
        if (newX < MIN_COORD) {
            this.boundaryHit = true;
        } else {
            this.x = newX;
            this.boundaryHit = false;
            consumeBattery(5);
        }
    }

    public void moveRight() {
        int newX = this.x + 1;
        if (newX > MAX_COORD) {
            this.boundaryHit = true;
        } else {
            this.x = newX;
            this.boundaryHit = false;
            consumeBattery(5);
        }
    }

    public void moveUp() {
        int newY = this.y - 1;
        if (newY < MIN_COORD) {
            this.boundaryHit = true;
        } else {
            this.y = newY;
            this.boundaryHit = false;
            consumeBattery(5);
        }
    }

    public void moveDown() {
        int newY = this.y + 1;
        if (newY > MAX_COORD) {
            this.boundaryHit = true;
        } else {
            this.y = newY;
            this.boundaryHit = false;
            consumeBattery(5);
        }
    }

    private void consumeBattery(int amount) {
        this.battery = Math.max(0, this.battery - amount);
    }

    public void reset() {
        this.battery = 100;
        this.structuralIntegrity = 100;
        this.ammo = 100;
        this.boundaryHit = false;
    }
}

