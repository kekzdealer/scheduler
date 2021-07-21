package at.kurumi.main;

import java.util.HashSet;
import java.util.Set;

public class Instruction {

    private final String instruction;
    private final int duration;

    private final Set<Instruction> next = new HashSet<>();
    private final Set<Instruction> previous = new HashSet<>();
    private int inclusivePathLength = 0;
    private int earliestStartTime = -1;

    public Instruction(String instruction, int duration) {
        this.instruction = instruction;
        this.duration = duration;
    }

    public String getInstruction() {
        return instruction;
    }

    public int getDuration() {
        return duration;
    }

    public Set<Instruction> getNext() {
        return next;
    }

    public Set<Instruction> getPrevious() {
        return previous;
    }

    /**
     * Called by Instruction::addPrevious() to establish double link.
     *
     * @param next subsequent instruction
     */
    private void addNext(Instruction next) {
        this.next.add(next);
    }

    /**
     * Double link this instruction with a dependency.
     *
     * @param previous prerequisite instruction
     */
    public void linkWithDependency(Instruction previous) {
        this.previous.add(previous);
        previous.addNext(this);
    }

    private void calculateInclusivePathLength() {
        earliestStartTime = pathLengthRecursive(0, this);
        inclusivePathLength = duration + earliestStartTime;
    }

    private int pathLengthRecursive(final int pathLength, Instruction current) {
        if(current.previous.isEmpty()) {
            return pathLength + duration;
        }
        return current.previous.stream()
                .mapToInt(instr -> pathLengthRecursive(pathLength + duration, instr))
                .max()
                .getAsInt();
    }

    public int getEarliestStartTime() {
        if(earliestStartTime == -1) {
            calculateInclusivePathLength();
        }
        return earliestStartTime;
    }
}
