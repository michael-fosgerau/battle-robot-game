package net.fosgerau.game.service;

import jakarta.enterprise.context.ApplicationScoped;
import net.fosgerau.game.model.GameState;
import net.fosgerau.game.model.Robot;
import net.fosgerau.game.model.RobotProgram;

@ApplicationScoped
public class GameService {
    private GameState gameState;

    public GameService() {
        this.gameState = new GameState();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void updateRobotPosition() {
        Robot robot = gameState.getPlayerRobot();
        RobotProgram program = gameState.getRobotProgram();
        
        String instruction = program.getNextInstruction();
        
        if (instruction != null) {
            executeInstruction(robot, instruction);
        }
        
        gameState.setLastUpdateTime(System.currentTimeMillis());
    }

    private void executeInstruction(Robot robot, String instruction) {
        switch (instruction) {
            case "moveLeft":
                robot.moveLeft();
                break;
            case "moveRight":
                robot.moveRight();
                break;
            case "moveUp":
                robot.moveUp();
                break;
            case "moveDown":
                robot.moveDown();
                break;
            default:
                // Unknown instruction, skip
                break;
        }
    }

    public void setRobotProgram(String[] instructions) {
        RobotProgram program = new RobotProgram();
        for (int i = 0; i < instructions.length && i < 16; i++) {
            program.setInstruction(i, instructions[i]);
        }
        gameState.setRobotProgram(program);
        gameState.getRobotProgram().reset();
    }

    public void stopGame() {
        // Reset robot position and instruction pointer, but keep the program
        gameState.getPlayerRobot().setX(10);
        gameState.getPlayerRobot().setY(10);
        gameState.getRobotProgram().reset();
        gameState.setLastUpdateTime(System.currentTimeMillis());
    }

    public void reset() {
        this.gameState = new GameState();
    }

    public void ensureProgramInitialized() {
        if (gameState.getRobotProgram() == null) {
            gameState.setRobotProgram(new RobotProgram());
        }
    }
}

