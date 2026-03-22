package net.fosgerau.game.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.fosgerau.game.api.dto.GameStateDto;
import net.fosgerau.game.api.dto.RobotDto;
import net.fosgerau.game.api.dto.RobotProgramDto;
import net.fosgerau.game.model.GameState;
import net.fosgerau.game.model.Robot;
import net.fosgerau.game.model.RobotProgram;
import net.fosgerau.game.service.GameService;

@Path("/api/game")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    @Inject
    GameService gameService;

    @GET
    @Path("/state")
    public GameStateDto getGameState() {
        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();
        RobotProgram program = state.getRobotProgram();
        
        RobotDto robotDto = new RobotDto(robot.getId(), robot.getX(), robot.getY());
        RobotProgramDto programDto = new RobotProgramDto(program.getInstructions(), program.getCurrentInstructionIndex());
        
        return new GameStateDto(robotDto, state.getLastUpdateTime(), programDto);
    }

    @POST
    @Path("/update")
    public GameStateDto updateGame() {
        gameService.updateRobotPosition();
        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();
        RobotProgram program = state.getRobotProgram();
        
        RobotDto robotDto = new RobotDto(robot.getId(), robot.getX(), robot.getY());
        RobotProgramDto programDto = new RobotProgramDto(program.getInstructions(), program.getCurrentInstructionIndex());
        
        return new GameStateDto(robotDto, state.getLastUpdateTime(), programDto);
    }

    @POST
    @Path("/program")
    public GameStateDto setProgram(RobotProgramDto programDto) {
        gameService.setRobotProgram(programDto.instructions);
        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();
        RobotProgram program = state.getRobotProgram();
        
        RobotDto robotDto = new RobotDto(robot.getId(), robot.getX(), robot.getY());
        RobotProgramDto result = new RobotProgramDto(program.getInstructions(), program.getCurrentInstructionIndex());
        
        return new GameStateDto(robotDto, state.getLastUpdateTime(), result);
    }

    @POST
    @Path("/start")
    public GameStateDto startGame() {
        // Reset robot position and instruction pointer, but keep the existing program
        gameService.stopGame();
        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();
        RobotProgram program = state.getRobotProgram();
        
        RobotDto robotDto = new RobotDto(robot.getId(), robot.getX(), robot.getY());
        RobotProgramDto programDto = new RobotProgramDto(program.getInstructions(), program.getCurrentInstructionIndex());
        
        return new GameStateDto(robotDto, state.getLastUpdateTime(), programDto);
    }

    @POST
    @Path("/stop")
    public GameStateDto stopGame() {
        gameService.stopGame();
        GameState state = gameService.getGameState();
        Robot robot = state.getPlayerRobot();
        RobotProgram program = state.getRobotProgram();
        
        RobotDto robotDto = new RobotDto(robot.getId(), robot.getX(), robot.getY());
        RobotProgramDto programDto = new RobotProgramDto(program.getInstructions(), program.getCurrentInstructionIndex());
        
        return new GameStateDto(robotDto, state.getLastUpdateTime(), programDto);
    }

}
