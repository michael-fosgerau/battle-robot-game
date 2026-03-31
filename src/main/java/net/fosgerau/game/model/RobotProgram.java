package net.fosgerau.game.model;

import java.util.HashMap;
import java.util.Map;

public class RobotProgram {
    private static final int GRID_SIZE = 4;
    private static final int TOTAL_SLOTS = GRID_SIZE * GRID_SIZE;
    
    private String[] instructions;
    private int currentInstructionIndex;
    private Map<String, Integer> dataRegisters; // D1, D2, D3, etc. store angle values (0-360)

    public RobotProgram() {
        this.instructions = new String[TOTAL_SLOTS];
        this.currentInstructionIndex = 0;
        this.dataRegisters = new HashMap<>();
        // Initialize 8 data registers
        for (int i = 1; i <= 8; i++) {
            this.dataRegisters.put("D" + i, 0);
        }
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
        // Find the next valid instruction, skipping any nulls and invalid instructions
        int searchIndex = currentInstructionIndex;
        int searchCount = 0;
        
        while (searchCount < TOTAL_SLOTS) {
            if (instructions[searchIndex] != null && isValidInstruction(instructions[searchIndex])) {
                // Found a valid instruction
                String instruction = instructions[searchIndex];
                // Move pointer to next slot for next call
                currentInstructionIndex = (searchIndex + 1) % TOTAL_SLOTS;
                return instruction;
            }
            // This slot is invalid/null, move to next and continue searching
            searchIndex = (searchIndex + 1) % TOTAL_SLOTS;
            searchCount++;
        }
        
        // All slots are invalid, return null (no-op)
        return null;
    }

    private boolean isValidInstruction(String instruction) {
        if (instruction == null) {
            return false;
        }
        // Check for movement instructions
        if (instruction.equals("moveLeft") || 
            instruction.equals("moveRight") || 
            instruction.equals("moveUp") || 
            instruction.equals("moveDown")) {
            return true;
        }
        // Check for scan instruction
        if (instruction.equals("scan")) {
            return true;
        }
        // Check for variable instruction (D1, D2, etc.)
        if (instruction.matches("D[1-8]")) {
            return true;
        }
        return false;
    }

    public void reset() {
        currentInstructionIndex = 0;
        // Reset all data registers to 0
        for (int i = 1; i <= 8; i++) {
            this.dataRegisters.put("D" + i, 0);
        }
    }

    public int getCurrentInstructionIndex() {
        return currentInstructionIndex;
    }

    public Map<String, Integer> getDataRegisters() {
        return dataRegisters;
    }

    public void setDataRegister(String register, int value) {
        if (dataRegisters.containsKey(register)) {
            // Clamp value to 0-360 for angle
            this.dataRegisters.put(register, Math.max(0, Math.min(360, value)));
        }
    }

    public int getDataRegister(String register) {
        return dataRegisters.getOrDefault(register, 0);
    }

    /**
     * Get the next instruction at the given slot index, without advancing the instruction pointer
     */
    public String peekInstruction(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < TOTAL_SLOTS) {
            return instructions[slotIndex];
        }
        return null;
    }
}

