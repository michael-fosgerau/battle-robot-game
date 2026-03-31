package net.fosgerau.game.api.dto;

public class RobotDto {
    public String id;
    public int x;
    public int y;
    public int battery;
    public int structuralIntegrity;
    public int ammo;
    public boolean boundaryHit;

    public RobotDto() {
    }

    public RobotDto(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.battery = 100;
        this.structuralIntegrity = 100;
        this.ammo = 100;
        this.boundaryHit = false;
    }

    public RobotDto(String id, int x, int y, int battery, int structuralIntegrity, int ammo) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.battery = battery;
        this.structuralIntegrity = structuralIntegrity;
        this.ammo = ammo;
        this.boundaryHit = false;
    }

    public RobotDto(String id, int x, int y, int battery, int structuralIntegrity, int ammo, boolean boundaryHit) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.battery = battery;
        this.structuralIntegrity = structuralIntegrity;
        this.ammo = ammo;
        this.boundaryHit = boundaryHit;
    }
}

