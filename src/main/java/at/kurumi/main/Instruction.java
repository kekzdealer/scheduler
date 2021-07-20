package at.kurumi.main;

import java.util.HashSet;
import java.util.Set;

public class Instruction {

    private final String instruction;
    private final long duration;
    private final Set<Instruction> next = new HashSet<>();
    private final Set<Instruction> previous = new HashSet<>();

    public Instruction(String instruction, long duration) {
        this.instruction = instruction;
        this.duration = duration;
    }

    public String getInstruction() {
        return instruction;
    }

    public long getDuration() {
        return duration;
    }

    public Set<Instruction> getNext() {
        return next;
    }

    public Set<Instruction> getPrevious() {
        return previous;
    }
}
