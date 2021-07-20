package at.kurumi.json;


import java.util.List;

/**
 * Covers a list of instructions as described in the exercises.
 */
public class InstructionsJSON {

    private final List<InstructionJSON> instructions;

    public InstructionsJSON(List<InstructionJSON> instructions) {
        this.instructions = instructions;
    }

    public List<InstructionJSON> getInstructions() {
        return instructions;
    }
}
