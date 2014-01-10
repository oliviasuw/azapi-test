package bgu.dcr.az.texen;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * simple tickable - run in a loop and then terminated will fail the test if
 *
 * @author User
 */
public class SanityTickable extends AbstractTestingTickable {

    private boolean run = false;
    private AtomicBoolean beingTick = new AtomicBoolean(false);

    public SanityTickable(int id, TickablesExecutorService exec, int numTickables, TestResult result) {
        super(id, exec, numTickables, result);
    }

    @Override
    public void _tick() {
        boolean before = beingTick.getAndSet(true);

        if (before) {
            throw new RuntimeException("Tickable is being handled concurrently");
        }

        if (run) {
            terminated = true;
        } else {
            run = true;
            System.out.println("Agent " + id + " ticking");

            if (id + 1 < numTickables) {
                requestTick(id + 1);
            } else {
                for (int i = 0; i < numTickables; i++) {
                    requestTick(i);
                }
            }
        }

        beingTick.set(false);
    }

}
