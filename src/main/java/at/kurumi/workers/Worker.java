package at.kurumi.workers;

import java.util.concurrent.Callable;

/**
 * A Worker can carry out a job according to their skill.
 */
public class Worker implements Callable<Integer> {

    enum Skill {
        PROCESS,
        MOVE
    }

    private final Skill skill;
    private final long duration;

    /**
     * Construct a Worker with a working duration.
     *
     * @param duration in seconds
     */
    public Worker(Skill skill, long duration) {
        this.skill = skill;
        this.duration = duration;
    }

    @Override
    public Integer call() throws InterruptedException {
        Thread.sleep(duration * 1000);
        return 0;
    }

    public Skill getSkill() {
        return skill;
    }

    public long getDuration() {
        return duration;
    }
}
