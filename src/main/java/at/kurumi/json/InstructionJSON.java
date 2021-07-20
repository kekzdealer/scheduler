package at.kurumi.json;

import java.util.Set;

/**
 * Covers a single instruction with a duration and prerequisite instruction ids.
 */
public class InstructionJSON {

    private final String process;
    private final long duration;
    private final Set<Integer> depends_on;

    public InstructionJSON(String process, long duration, Set<Integer> depends_on) {
        this.process = process;
        this.duration = duration;
        this.depends_on = depends_on;
    }

    public String getProcess() {
        return process;
    }

    public long getDuration() {
        return duration;
    }

    public Set<Integer> getDepends_on() {
        return depends_on;
    }
}
