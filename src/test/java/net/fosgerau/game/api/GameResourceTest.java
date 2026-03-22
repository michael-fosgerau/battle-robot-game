package net.fosgerau.game.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.fosgerau.game.api.dto.RobotProgramDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Game Resource API Tests")
public class GameResourceTest {

    private static final String API_BASE = "/api/game";
    private static final String ROBOT_X = "robot.x";
    private static final String ROBOT_Y = "robot.y";
    private static final String PROGRAM_INSTRUCTIONS = "program.instructions";
    private static final String PROGRAM_INDEX = "program.currentInstructionIndex";

    @BeforeEach
    void setup() {
        // Reset the game state before each test by calling stop
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/stop")
            .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Starting the game makes the game state endpoint available with expected response")
    void testStartGameResponseStructure() {
        // Start the game
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200)
            // Verify response structure
            .body("robot", notNullValue())
            .body("robot.id", equalTo("player"))
            .body("robot.x", equalTo(10))
            .body("robot.y", equalTo(10))
            .body("program", notNullValue())
            .body("program.instructions", notNullValue())
            .body("program.currentInstructionIndex", equalTo(0))
            .body("timestamp", notNullValue());
    }

    @Test
    @DisplayName("Game state endpoint is available after starting game")
    void testGameStateEndpointAfterStart() {
        // Start the game
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        // Verify state endpoint works
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE + "/state")
            .then()
            .statusCode(200)
            .body("robot", notNullValue())
            .body("robot.x", equalTo(10))
            .body("robot.y", equalTo(10));
    }

    @Test
    @DisplayName("Robot starts at position (10, 10) after game start")
    void testRobotInitialPosition() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(10));
    }

    @Test
    @DisplayName("Updating robot instructions can be read in the game state")
    void testSetAndRetrieveProgramInstructions() {
        // Set program with specific instructions
        String[] instructions = {"moveRight", "moveDown", "moveLeft", null, "moveUp"};
        
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200)
            .body("program.instructions", notNullValue())
            .body("program.instructions[0]", equalTo("moveRight"))
            .body("program.instructions[1]", equalTo("moveDown"))
            .body("program.instructions[2]", equalTo("moveLeft"))
            .body("program.instructions[3]", nullValue())
            .body("program.instructions[4]", equalTo("moveUp"));

        // Verify we can retrieve the same instructions via state endpoint
        given()
            .contentType(ContentType.JSON)
            .when()
            .get(API_BASE + "/state")
            .then()
            .statusCode(200)
            .body("program.instructions[0]", equalTo("moveRight"))
            .body("program.instructions[1]", equalTo("moveDown"))
            .body("program.instructions[2]", equalTo("moveLeft"))
            .body("program.instructions[3]", nullValue())
            .body("program.instructions[4]", equalTo("moveUp"));
    }

    @Test
    @DisplayName("Calling update endpoint moves robot according to current instructions")
    void testUpdateMovesRobotByInstruction() {
        // Start game at (10, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(10));

        // Set program with moveRight as first instruction
        String[] instructions = {"moveRight"};
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // Call update - should execute moveRight
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(11))
            .body(ROBOT_Y, equalTo(10));
    }

    @Test
    @DisplayName("Instructions advance one at a time")
    void testInstructionsAdvanceOneAtATime() {
        // Start game at (10, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        // Set program with multiple instructions
        String[] instructions = {"moveRight", "moveDown", "moveLeft", "moveUp"};
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // First update: moveRight (10, 10) -> (11, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(11))
            .body(ROBOT_Y, equalTo(10));

        // Second update: moveDown (11, 10) -> (11, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(11))
            .body(ROBOT_Y, equalTo(11));

        // Third update: moveLeft (11, 11) -> (10, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(11));

        // Fourth update: moveUp (10, 11) -> (10, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(10));
    }

    @Test
    @DisplayName("Null instructions are skipped freely")
    void testNullInstructionsAreSkipped() {
        // Start game
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        // Set program with nulls interspersed
        String[] instructions = {"moveRight", null, null, "moveDown", null, "moveLeft"};
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // First update: skip nulls, execute moveRight (10, 10) -> (11, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(11))
            .body(ROBOT_Y, equalTo(10));

        // Second update: skip nulls, execute moveDown (11, 10) -> (11, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(11))
            .body(ROBOT_Y, equalTo(11));

        // Third update: skip null, execute moveLeft (11, 11) -> (10, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(11));
    }

    @Test
    @DisplayName("Empty program (all nulls) produces no movement")
    void testEmptyProgramNoMovement() {
        // Start game
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        // Set program with all nulls
        String[] instructions = new String[16];
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // Multiple updates should not move the robot
        for (int i = 0; i < 5; i++) {
            given()
                .contentType(ContentType.JSON)
                .when()
                .post(API_BASE + "/update")
                .then()
                .statusCode(200)
                .body(ROBOT_X, equalTo(10))
                .body(ROBOT_Y, equalTo(10));
        }
    }

    @Test
    @DisplayName("Stop game resets robot to initial position")
    void testStopGameResetsPosition() {
        // Start and move robot
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        String[] instructions = {"moveRight", "moveRight", "moveDown"};
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // Execute several updates
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200);

        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .statusCode(200);

        // Stop game - should reset to (10, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/stop")
            .then()
            .statusCode(200)
            .body(ROBOT_X, equalTo(10))
            .body(ROBOT_Y, equalTo(10));
    }

    @Test
    @DisplayName("Program wraps around after last instruction")
    void testProgramWrapsAround() {
        // Start game
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/start")
            .then()
            .statusCode(200);

        // Set small program
        String[] instructions = new String[16];
        instructions[0] = "moveRight";
        instructions[1] = "moveDown";
        
        given()
            .contentType(ContentType.JSON)
            .body(new RobotProgramDto(instructions, 0))
            .when()
            .post(API_BASE + "/program")
            .then()
            .statusCode(200);

        // Execute multiple times - program should wrap
        // First: moveRight (10, 10) -> (11, 10)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .body(ROBOT_X, equalTo(11));

        // Second: moveDown (11, 10) -> (11, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .body(ROBOT_Y, equalTo(11));

        // Third: should wrap and execute moveRight again (11, 11) -> (12, 11)
        given()
            .contentType(ContentType.JSON)
            .when()
            .post(API_BASE + "/update")
            .then()
            .body(ROBOT_X, equalTo(12));
    }
}




