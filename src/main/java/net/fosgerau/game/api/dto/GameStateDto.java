package net.fosgerau.game.api.dto;

public class GameStateDto {
    public RobotDto robot;
    public RobotDto opponentRobot;
    public long timestamp;
    public RobotProgramDto program;

    public GameStateDto() {
    }

    public GameStateDto(RobotDto robot, long timestamp, RobotProgramDto program) {
        this.robot = robot;
        this.timestamp = timestamp;
        this.program = program;
    }

    public GameStateDto(RobotDto robot, RobotDto opponentRobot, long timestamp, RobotProgramDto program) {
        this.robot = robot;
        this.opponentRobot = opponentRobot;
        this.timestamp = timestamp;
        this.program = program;
    }
}

