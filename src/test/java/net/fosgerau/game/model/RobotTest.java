package net.fosgerau.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Robot Model Unit Tests")
public class RobotTest {

    private Robot robot;

    @BeforeEach
    void setup() {
        robot = new Robot("testRobot", 10, 15);
    }

    @Test
    @DisplayName("Robot initializes with correct id and position")
    void testRobotInitialization() {
        assertEquals("testRobot", robot.getId());
        assertEquals(10, robot.getX());
        assertEquals(15, robot.getY());
    }

    @Test
    @DisplayName("Robot can be created with different starting positions")
    void testRobotWithDifferentPositions() {
        Robot robot1 = new Robot("player", 5, 5);
        Robot robot2 = new Robot("enemy", 20, 25);
        
        assertEquals(5, robot1.getX());
        assertEquals(5, robot1.getY());
        assertEquals(20, robot2.getX());
        assertEquals(25, robot2.getY());
    }

    @Test
    @DisplayName("moveLeft decrements x coordinate")
    void testMoveLeft() {
        int initialX = robot.getX();
        robot.moveLeft();
        
        assertEquals(initialX - 1, robot.getX());
        assertEquals(15, robot.getY()); // Y should not change
    }

    @Test
    @DisplayName("moveRight increments x coordinate")
    void testMoveRight() {
        int initialX = robot.getX();
        robot.moveRight();
        
        assertEquals(initialX + 1, robot.getX());
        assertEquals(15, robot.getY()); // Y should not change
    }

    @Test
    @DisplayName("moveUp decrements y coordinate")
    void testMoveUp() {
        int initialY = robot.getY();
        robot.moveUp();
        
        assertEquals(10, robot.getX()); // X should not change
        assertEquals(initialY - 1, robot.getY());
    }

    @Test
    @DisplayName("moveDown increments y coordinate")
    void testMoveDown() {
        int initialY = robot.getY();
        robot.moveDown();
        
        assertEquals(10, robot.getX()); // X should not change
        assertEquals(initialY + 1, robot.getY());
    }

    @Test
    @DisplayName("Multiple movements accumulate correctly")
    void testMultipleMovements() {
        robot.moveRight();
        robot.moveRight();
        robot.moveDown();
        
        assertEquals(12, robot.getX());
        assertEquals(16, robot.getY());
    }

    @Test
    @DisplayName("Movements can result in negative coordinates")
    void testNegativeCoordinates() {
        robot.moveLeft();
        robot.moveLeft();
        robot.moveLeft();
        robot.moveUp();
        
        assertEquals(7, robot.getX());
        assertEquals(14, robot.getY());
    }

    @Test
    @DisplayName("setX updates x coordinate")
    void testSetX() {
        robot.setX(25);
        assertEquals(25, robot.getX());
        assertEquals(15, robot.getY()); // Y should not change
    }

    @Test
    @DisplayName("setY updates y coordinate")
    void testSetY() {
        robot.setY(30);
        assertEquals(10, robot.getX()); // X should not change
        assertEquals(30, robot.getY());
    }

    @Test
    @DisplayName("getId returns correct robot identifier")
    void testGetId() {
        Robot player = new Robot("player", 0, 0);
        Robot enemy = new Robot("enemy", 0, 0);
        
        assertEquals("player", player.getId());
        assertEquals("enemy", enemy.getId());
    }

    @Test
    @DisplayName("Robot can return to starting position")
    void testReturnToStartingPosition() {
        int startX = robot.getX();
        int startY = robot.getY();
        
        robot.moveRight();
        robot.moveDown();
        robot.moveLeft();
        robot.moveUp();
        
        assertEquals(startX, robot.getX());
        assertEquals(startY, robot.getY());
    }

    @Test
    @DisplayName("Robot position can be reset using setters")
    void testResetPositionWithSetters() {
        robot.moveRight();
        robot.moveDown();
        
        robot.setX(10);
        robot.setY(15);
        
        assertEquals(10, robot.getX());
        assertEquals(15, robot.getY());
    }

    @Test
    @DisplayName("Large movement values work correctly")
    void testLargeMovements() {
        for (int i = 0; i < 100; i++) {
            robot.moveRight();
        }
        assertEquals(110, robot.getX());
        
        for (int i = 0; i < 50; i++) {
            robot.moveDown();
        }
        assertEquals(65, robot.getY());
    }

    @Test
    @DisplayName("Robot can reach zero coordinates")
    void testReachZeroCoordinates() {
        robot.setX(0);
        robot.setY(0);
        
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    @DisplayName("Movement combinations work as expected")
    void testMovementCombinations() {
        // Move in a pattern: right, right, down, left, left, up
        robot.moveRight();
        robot.moveRight();
        robot.moveDown();
        robot.moveLeft();
        robot.moveLeft();
        robot.moveUp();
        
        assertEquals(10, robot.getX()); // Back to original X
        assertEquals(15, robot.getY()); // Back to original Y
    }
}

