package bgu.dcr.az.texen;

import java.util.Random;

/**
 * simple tickable - run in a loop and then terminated will fail the test if
 *
 * @author User
 */
public class IdleTickable extends AbstractTestingTickable {

    private boolean run = false;
    private Random r;

    public IdleTickable(int id, TickablesExecutorService exec, int numTickables, TestResult result) {
        super(id, exec, numTickables, result);
        r = new Random(id);
    }

    @Override
    public void _tick() {

        if (!run) {
            run = true;
            System.out.println("Agent " + id + " ticking");

            for (int i = 0; i < 3; i++) {
                requestTick(r.nextInt(numTickables));
            }
        }

    }

}
