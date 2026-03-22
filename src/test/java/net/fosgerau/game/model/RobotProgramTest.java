package net.fosgerau.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Robot Program Unit Tests")
public class RobotProgramTest {

    private RobotProgram program;

    @BeforeEach
    void setup() {
        program = new RobotProgram();
    }

    @Test
    @DisplayName("Program initializes with 16 slots (4x4 grid)")
    void testProgramInitialization() {
        String[] instructions = program.getInstructions();
        
        assertNotNull(instructions);
        assertEquals(16, instructions.length);
        
        // All slots should be null initially
        for (String instruction : instructions) {
            assertNull(instruction);
        }
    }

    @Test
    @DisplayName("Instruction can be set at specific slot")
    void testSetInstruction() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(5, "moveDown");
        program.setInstruction(15, "moveLeft");

        String[] instructions = program.getInstructions();
        
        assertEquals("moveRight", instructions[0]);
        assertEquals("moveDown", instructions[5]);
        assertEquals("moveLeft", instructions[15]);
    }

    @Test
    @DisplayName("Setting instruction at invalid slot does nothing")
    void testSetInstructionInvalidSlot() {
        program.setInstruction(-1, "moveRight");
        program.setInstruction(16, "moveDown");
        program.setInstruction(100, "moveLeft");

        // All instructions should remain null
        for (String instruction : program.getInstructions()) {
            assertNull(instruction);
        }
    }

    @Test
    @DisplayName("Instruction can be cleared from slot")
    void testClearInstruction() {
        program.setInstruction(5, "moveRight");
        assertEquals("moveRight", program.getInstructions()[5]);

        program.clearInstruction(5);
        assertNull(program.getInstructions()[5]);
    }

    @Test
    @DisplayName("getNextInstruction returns first non-null instruction")
    void testGetNextInstructionReturnsFirstNonNull() {
        program.setInstruction(0, "moveRight");
        
        String instruction = program.getNextInstruction();
        
        assertEquals("moveRight", instruction);
    }

    @Test
    @DisplayName("getNextInstruction skips null instructions")
    void testGetNextInstructionSkipsNulls() {
        program.setInstruction(2, "moveDown");
        program.setInstruction(5, "moveLeft");
        
        // First call should skip slots 0, 1 and return slot 2
        String instruction = program.getNextInstruction();
        assertEquals("moveDown", instruction);
        
        // Second call should skip slots 3, 4 and return slot 5
        instruction = program.getNextInstruction();
        assertEquals("moveLeft", instruction);
    }

    @Test
    @DisplayName("getNextInstruction returns null when all slots are null")
    void testGetNextInstructionAllNull() {
        String instruction = program.getNextInstruction();
        assertNull(instruction);
        
        // Multiple calls should continue returning null
        instruction = program.getNextInstruction();
        assertNull(instruction);
        
        instruction = program.getNextInstruction();
        assertNull(instruction);
    }

    @Test
    @DisplayName("Instructions advance sequentially")
    void testInstructionsAdvanceSequentially() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(1, "moveDown");
        program.setInstruction(2, "moveLeft");
        program.setInstruction(3, "moveUp");
        
        assertEquals("moveRight", program.getNextInstruction());
        assertEquals("moveDown", program.getNextInstruction());
        assertEquals("moveLeft", program.getNextInstruction());
        assertEquals("moveUp", program.getNextInstruction());
    }

    @Test
    @DisplayName("Program pointer wraps around to beginning")
    void testProgramPointerWrapsAround() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(1, "moveDown");
        
        // Execute the 2 instructions
        assertEquals("moveRight", program.getNextInstruction());
        assertEquals("moveDown", program.getNextInstruction());
        
        // Next call should wrap around and find moveRight again
        assertEquals("moveRight", program.getNextInstruction());
    }

    @Test
    @DisplayName("Program pointer advances past nulls to next valid instruction")
    void testPointerAdvancesPastNulls() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(3, "moveDown");
        program.setInstruction(4, "moveLeft");
        
        // First call: get moveRight (slot 0), pointer moves to 1
        assertEquals("moveRight", program.getNextInstruction());
        
        // Second call: skip slots 1, 2, get moveDown (slot 3), pointer moves to 4
        assertEquals("moveDown", program.getNextInstruction());
        
        // Third call: get moveLeft (slot 4), pointer moves to 5
        assertEquals("moveLeft", program.getNextInstruction());
    }

    @Test
    @DisplayName("Reset resets instruction pointer to 0")
    void testResetInstructionPointer() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(1, "moveDown");
        
        // Advance pointer
        program.getNextInstruction();
        program.getNextInstruction();
        
        int indexBefore = program.getCurrentInstructionIndex();
        assertTrue(indexBefore > 0, "Pointer should have advanced");
        
        // Reset
        program.reset();
        
        assertEquals(0, program.getCurrentInstructionIndex());
    }

    @Test
    @DisplayName("getCurrentInstructionIndex returns correct pointer position")
    void testGetCurrentInstructionIndex() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(1, "moveDown");
        
        assertEquals(0, program.getCurrentInstructionIndex());
        
        program.getNextInstruction();
        // Pointer should now be at 1
        assertEquals(1, program.getCurrentInstructionIndex());
        
        program.getNextInstruction();
        // Pointer should now be at 2
        assertEquals(2, program.getCurrentInstructionIndex());
    }

    @Test
    @DisplayName("Program with single instruction repeats correctly")
    void testSingleInstructionRepeat() {
        program.setInstruction(0, "moveRight");
        
        for (int i = 0; i < 5; i++) {
            String instruction = program.getNextInstruction();
            assertEquals("moveRight", instruction);
        }
    }

    @Test
    @DisplayName("Program with sparse instructions handles wrapping")
    void testSparseInstructionWrapping() {
        // Only fill slots 0, 5, 10, 15
        program.setInstruction(0, "moveRight");
        program.setInstruction(5, "moveDown");
        program.setInstruction(10, "moveLeft");
        program.setInstruction(15, "moveUp");
        
        String[] expected = {"moveRight", "moveDown", "moveLeft", "moveUp"};
        
        // Should cycle through these 4 instructions repeatedly
        for (int cycle = 0; cycle < 3; cycle++) {
            for (String exp : expected) {
                assertEquals(exp, program.getNextInstruction());
            }
        }
    }

    @Test
    @DisplayName("All 16 slots can be filled with instructions")
    void testFillAllSlots() {
        String[] instructions = {
            "moveRight", "moveDown", "moveLeft", "moveUp",
            "moveRight", "moveDown", "moveLeft", "moveUp",
            "moveRight", "moveDown", "moveLeft", "moveUp",
            "moveRight", "moveDown", "moveLeft", "moveUp"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            program.setInstruction(i, instructions[i]);
        }
        
        String[] retrieved = program.getInstructions();
        for (int i = 0; i < instructions.length; i++) {
            assertEquals(instructions[i], retrieved[i]);
        }
    }

    @Test
    @DisplayName("Program pointer behavior with mixed nulls and instructions")
    void testMixedNullsAndInstructions() {
        program.setInstruction(0, "moveRight");
        program.setInstruction(2, "moveDown");
        program.setInstruction(4, "moveLeft");
        program.setInstruction(6, "moveUp");
        // All other slots are null
        
        // Should get instructions in order, skipping nulls
        assertEquals("moveRight", program.getNextInstruction());
        assertEquals("moveDown", program.getNextInstruction());
        assertEquals("moveLeft", program.getNextInstruction());
        assertEquals("moveUp", program.getNextInstruction());
        
        // Should wrap and start from beginning
        assertEquals("moveRight", program.getNextInstruction());
    }

    @Test
    @DisplayName("Overwriting an instruction updates the slot")
    void testOverwriteInstruction() {
        program.setInstruction(0, "moveRight");
        assertEquals("moveRight", program.getInstructions()[0]);
        
        program.setInstruction(0, "moveDown");
        assertEquals("moveDown", program.getInstructions()[0]);
    }
}

