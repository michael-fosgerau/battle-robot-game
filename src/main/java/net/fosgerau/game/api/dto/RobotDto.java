package net.fosgerau.game.api.dto;

public class RobotDto {
    public String id;
    public int x;
    public int y;

    public RobotDto() {
    }

    public RobotDto(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

