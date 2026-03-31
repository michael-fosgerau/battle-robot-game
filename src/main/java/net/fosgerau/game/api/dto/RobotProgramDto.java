package net.fosgerau.game.api.dto;

public class RobotProgramDto {
    public String[] instructions;
    public int currentInstructionIndex;
    public boolean scanActive; // True when a scan just occurred

    public RobotProgramDto() {
    }

    public RobotProgramDto(String[] instructions, int currentInstructionIndex) {
        this.instructions = instructions;
        this.currentInstructionIndex = currentInstructionIndex;
        this.scanActive = false;
    }

    public RobotProgramDto(String[] instructions, int currentInstructionIndex, boolean scanActive) {
        this.instructions = instructions;
        this.currentInstructionIndex = currentInstructionIndex;
        this.scanActive = scanActive;
    }
}

