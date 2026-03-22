package net.fosgerau.game.service;

import net.fosgerau.game.model.GameState;
import net.fosgerau.game.model.Robot;
import net.fosgerau.game.model.RobotProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game Service Unit Tests")
public class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setup() {
        gameService = new GameService();
    }

    @Test
    @DisplayName("Game state is initialized on service creation")
    void testGameStateInitialization() {
        GameState state = gameService.getGameState();
        
        assertNotNull(state);
        assertNotNull(state.getPlayerRobot());
        assertEquals("player", state.getPlayerRobot().getId());
        assertEquals(10, state.getPlayerRobot().getX());
        assertEquals(10, state.getPlayerRobot().getY());
        assertNotNull(state.getRobotProgram());
    }

    @Test
    @DisplayName("Setting robot program updates game state")
    void testSetRobotProgram() {
        String[] instructions = {"moveRight", "moveDown", null, "moveUp"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        String[] retrievedInstructions = state.getRobotProgram().getInstructions();

        assertEquals("moveRight", retrievedInstructions[0]);
        assertEquals("moveDown", retrievedInstructions[1]);
        assertNull(retrievedInstructions[2]);
        assertEquals("moveUp", retrievedInstructions[3]);
    }

    @Test
    @DisplayName("Update robot position executes current instruction")
    void testUpdateRobotPositionExecutesInstruction() {
        // Set program with moveRight as first instruction
        String[] instructions = {"moveRight"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        int initialX = robot.getX();
        int initialY = robot.getY();

        // Update should execute moveRight
        gameService.updateRobotPosition();

        assertEquals(initialX + 1, robot.getX());
        assertEquals(initialY, robot.getY());
    }

    @Test
    @DisplayName("Instructions execute sequentially")
    void testSequentialInstructionExecution() {
        String[] instructions = {"moveRight", "moveDown", "moveLeft", "moveUp"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // Initial position
        assertEquals(10, robot.getX());
        assertEquals(10, robot.getY());

        // Execute first instruction: moveRight
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(10, robot.getY());

        // Execute second instruction: moveDown
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(11, robot.getY());

        // Execute third instruction: moveLeft
        gameService.updateRobotPosition();
        assertEquals(10, robot.getX());
        assertEquals(11, robot.getY());

        // Execute fourth instruction: moveUp
        gameService.updateRobotPosition();
        assertEquals(10, robot.getX());
        assertEquals(10, robot.getY());
    }

    @Test
    @DisplayName("Null instructions are skipped during execution")
    void testNullInstructionsSkipped() {
        String[] instructions = {"moveRight", null, null, "moveDown"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // First update: skip nulls, execute moveRight
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(10, robot.getY());

        // Second update: skip nulls, execute moveDown
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(11, robot.getY());
    }

    @Test
    @DisplayName("Empty program produces no movement")
    void testEmptyProgramNoMovement() {
        String[] instructions = new String[16]; // All nulls
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        int initialX = robot.getX();
        int initialY = robot.getY();

        // Multiple updates should not change position
        for (int i = 0; i < 5; i++) {
            gameService.updateRobotPosition();
            assertEquals(initialX, robot.getX());
            assertEquals(initialY, robot.getY());
        }
    }

    @Test
    @DisplayName("Stop game resets robot position")
    void testStopGameResetsPosition() {
        // Set program and move robot
        String[] instructions = {"moveRight", "moveRight", "moveDown"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // Execute some movements
        gameService.updateRobotPosition();
        gameService.updateRobotPosition();

        assertEquals(12, robot.getX());
        assertEquals(10, robot.getY());

        // Stop should reset position
        gameService.stopGame();

        assertEquals(10, robot.getX());
        assertEquals(10, robot.getY());
    }

    @Test
    @DisplayName("Stop game resets instruction pointer")
    void testStopGameResetsInstructionPointer() {
        String[] instructions = {"moveRight", "moveDown", "moveLeft", "moveUp"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        RobotProgram program = state.getRobotProgram();

        // Execute some updates to advance pointer
        gameService.updateRobotPosition();
        gameService.updateRobotPosition();

        int indexBeforeStop = program.getCurrentInstructionIndex();
        assertTrue(indexBeforeStop > 0, "Instruction pointer should have advanced");

        // Stop should reset pointer
        gameService.stopGame();

        assertEquals(0, program.getCurrentInstructionIndex());
    }

    @Test
    @DisplayName("Program with all move instructions works correctly")
    void testAllMoveInstructions() {
        String[] instructions = {"moveUp", "moveDown", "moveLeft", "moveRight"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // Initial: (10, 10)
        // After moveUp: (10, 9)
        gameService.updateRobotPosition();
        assertEquals(10, robot.getX());
        assertEquals(9, robot.getY());

        // After moveDown: (10, 10)
        gameService.updateRobotPosition();
        assertEquals(10, robot.getX());
        assertEquals(10, robot.getY());

        // After moveLeft: (9, 10)
        gameService.updateRobotPosition();
        assertEquals(9, robot.getX());
        assertEquals(10, robot.getY());

        // After moveRight: (10, 10)
        gameService.updateRobotPosition();
        assertEquals(10, robot.getX());
        assertEquals(10, robot.getY());
    }

    @Test
    @DisplayName("Update time is recorded on each update")
    void testUpdateTimeRecorded() {
        String[] instructions = {"moveRight"};
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        long initialTime = state.getLastUpdateTime();

        try {
            Thread.sleep(10); // Small delay to ensure time difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        gameService.updateRobotPosition();

        long updatedTime = state.getLastUpdateTime();
        assertTrue(updatedTime >= initialTime, "Update time should be recorded");
    }

    @Test
    @DisplayName("Program can be changed after execution has started")
    void testChangeProgramAfterExecution() {
        String[] instructions1 = {"moveRight"};
        gameService.setRobotProgram(instructions1);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // Execute first program
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());

        // Change program
        String[] instructions2 = {"moveDown"};
        gameService.setRobotProgram(instructions2);

        // Execute should follow new program
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(11, robot.getY());
    }

    @Test
    @DisplayName("Unknown instructions are skipped")
    void testUnknownInstructionsSkipped() {
        String[] instructions = new String[16];
        instructions[0] = "unknownInstruction";
        instructions[1] = "moveRight";
        instructions[2] = "invalidMove";
        instructions[3] = "moveDown";
        // Rest are null
        gameService.setRobotProgram(instructions);

        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();

        // First update: skip unknown instruction at 0, execute moveRight at 1
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(10, robot.getY());

        // Second update: skip invalid instruction at 2, execute moveDown at 3
        gameService.updateRobotPosition();
        assertEquals(11, robot.getX());
        assertEquals(11, robot.getY());
    }
}


