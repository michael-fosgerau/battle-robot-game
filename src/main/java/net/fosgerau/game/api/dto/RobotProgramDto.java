package net.fosgerau.game.api.dto;

public class RobotProgramDto {
    public String[] instructions;
    public int currentInstructionIndex;

    public RobotProgramDto() {
    }

    public RobotProgramDto(String[] instructions, int currentInstructionIndex) {
        this.instructions = instructions;
        this.currentInstructionIndex = currentInstructionIndex;
    }
}

