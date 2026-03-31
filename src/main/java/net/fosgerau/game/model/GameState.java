package net.fosgerau.game.model;

public class GameState {
    private Robot playerRobot;
    private Robot aiRobot;
    private long lastUpdateTime;
    private int moveCounter;
    private RobotProgram robotProgram;

    public GameState() {
        this.playerRobot = new Robot("player", 10, 10);
        this.aiRobot = new Robot("ai", 20, 20);
        this.lastUpdateTime = System.currentTimeMillis();
        this.moveCounter = 0;
        this.robotProgram = new RobotProgram();
    }

    public Robot getPlayerRobot() {
        return playerRobot;
    }

    public Robot getAiRobot() {
        return aiRobot;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long time) {
        this.lastUpdateTime = time;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int counter) {
        this.moveCounter = counter;
    }

    public void incrementMoveCounter() {
        this.moveCounter++;
    }

    public RobotProgram getRobotProgram() {
        return robotProgram;
    }

    public void setRobotProgram(RobotProgram program) {
        this.robotProgram = program;
    }
}

