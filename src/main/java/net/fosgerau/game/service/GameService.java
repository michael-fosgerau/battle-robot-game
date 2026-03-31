package net.fosgerau.game.service;

import jakarta.enterprise.context.ApplicationScoped;
import net.fosgerau.game.model.GameState;
import net.fosgerau.game.model.Robot;
import net.fosgerau.game.model.RobotProgram;
import java.util.Random;

@ApplicationScoped
public class GameService {
    private GameState gameState;
    private Random random;

    public GameService() {
        this.gameState = new GameState();
        this.random = new Random();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void updateRobotPosition() {
        Robot playerRobot = gameState.getPlayerRobot();
        Robot aiRobot = gameState.getAiRobot();
        RobotProgram program = gameState.getRobotProgram();
        
        // Update player robot
        String instruction = program.getNextInstruction();
        if (instruction != null) {
            executeInstruction(playerRobot, aiRobot, program, instruction);
        }
        
        // Update AI robot with simple chase/evade logic
        updateAiRobot(playerRobot, aiRobot);
        
        gameState.setLastUpdateTime(System.currentTimeMillis());
    }

    private void executeInstruction(Robot playerRobot, Robot aiRobot, RobotProgram program, String instruction) {
        if (instruction.equals("scan")) {
            // Scan looks ahead to see if next instruction is a variable
            int currentIndex = program.getCurrentInstructionIndex();
            String nextInstruction = program.peekInstruction(currentIndex);
            
            if (nextInstruction != null && nextInstruction.matches("D[1-8]")) {
                // Calculate direction to opponent (0-360 degrees)
                int angle = calculateAngleToTarget(playerRobot, aiRobot);
                program.setDataRegister(nextInstruction, angle);
            }
        } else if (instruction.matches("D[1-8]")) {
            // Variable instruction - this is a no-op when executed
            // It's meant to be paired with scan or other operations
            // Just skip it
        } else {
            // Movement instructions
            switch (instruction) {
                case "moveLeft":
                    playerRobot.moveLeft();
                    break;
                case "moveRight":
                    playerRobot.moveRight();
                    break;
                case "moveUp":
                    playerRobot.moveUp();
                    break;
                case "moveDown":
                    playerRobot.moveDown();
                    break;
            }
        }
    }

    /**
     * Calculate angle from player to AI robot
     * 0° = right, 90° = down, 180° = left, 270° = up
     */
    private int calculateAngleToTarget(Robot player, Robot target) {
        int dx = target.getX() - player.getX();
        int dy = target.getY() - player.getY();
        
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;
        // Convert to 0-360 range
        if (angle < 0) {
            angle += 360;
        }
        return (int) Math.round(angle);
    }

    private void updateAiRobot(Robot playerRobot, Robot aiRobot) {
        // Simple AI logic: chase the player with occasional random moves
        // 70% chance to move towards player, 30% chance random move
        
        if (random.nextDouble() < 0.7) {
            // Chase logic
            int playerX = playerRobot.getX();
            int playerY = playerRobot.getY();
            int aiX = aiRobot.getX();
            int aiY = aiRobot.getY();
            
            // Move closer to player
            if (Math.abs(playerX - aiX) > Math.abs(playerY - aiY)) {
                // X distance is greater, move horizontally
                if (playerX > aiX) {
                    aiRobot.moveRight();
                } else {
                    aiRobot.moveLeft();
                }
            } else {
                // Y distance is greater, move vertically
                if (playerY > aiY) {
                    aiRobot.moveDown();
                } else {
                    aiRobot.moveUp();
                }
            }
        } else {
            // Random move
            int randomMove = random.nextInt(4);
            switch (randomMove) {
                case 0:
                    aiRobot.moveUp();
                    break;
                case 1:
                    aiRobot.moveDown();
                    break;
                case 2:
                    aiRobot.moveLeft();
                    break;
                case 3:
                    aiRobot.moveRight();
                    break;
            }
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
        // Reset both robots
        gameState.getPlayerRobot().setX(10);
        gameState.getPlayerRobot().setY(10);
        gameState.getPlayerRobot().reset();
        
        gameState.getAiRobot().setX(20);
        gameState.getAiRobot().setY(20);
        gameState.getAiRobot().reset();
        
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

