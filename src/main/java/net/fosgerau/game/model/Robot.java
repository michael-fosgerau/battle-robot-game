package net.fosgerau.game.model;

public class Robot {
    private int x;
    private int y;
    private String id;

    public Robot(String id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
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

    public void moveLeft() {
        this.x -= 1;
    }

    public void moveRight() {
        this.x += 1;
    }

    public void moveUp() {
        this.y -= 1;
    }

    public void moveDown() {
        this.y += 1;
    }
}

