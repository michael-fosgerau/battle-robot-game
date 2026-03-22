package net.fosgerau.game.model;

public class RobotProgram {
    private static final int GRID_SIZE = 4;
    private static final int TOTAL_SLOTS = GRID_SIZE * GRID_SIZE;
    
    private String[] instructions;
    private int currentInstructionIndex;

    public RobotProgram() {
        this.instructions = new String[TOTAL_SLOTS];
        this.currentInstructionIndex = 0;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public void setInstruction(int slot, String instruction) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            this.instructions[slot] = instruction;
        }
    }

    public void clearInstruction(int slot) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            this.instructions[slot] = null;
        }
    }

    public String getNextInstruction() {
        // Find the next non-null instruction, skipping any nulls
        int searchIndex = currentInstructionIndex;
        int searchCount = 0;
        
        while (searchCount < TOTAL_SLOTS) {
            if (instructions[searchIndex] != null) {
                // Found a non-null instruction
                String instruction = instructions[searchIndex];
                // Move pointer to next slot for next call
                currentInstructionIndex = (searchIndex + 1) % TOTAL_SLOTS;
                return instruction;
            }
            // This slot is null, move to next and continue searching
            searchIndex = (searchIndex + 1) % TOTAL_SLOTS;
            searchCount++;
        }
        
        // All slots are null, return null (no-op)
        return null;
    }

    public void reset() {
        currentInstructionIndex = 0;
    }

    public int getCurrentInstructionIndex() {
        return currentInstructionIndex;
    }
}

