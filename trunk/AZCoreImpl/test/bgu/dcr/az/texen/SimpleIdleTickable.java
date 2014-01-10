package bgu.dcr.az.texen;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * simple tickable - run in a loop and then terminated will fail the test if
 *
 * @author User
 */
public class SimpleIdleTickable extends AbstractTestingTickable {

    private boolean run = false;
    private Random r;

    public SimpleIdleTickable(int id, TickablesExecutorService exec, int numTickables, TestResult result) {
        super(id, exec, numTickables, result);
        r = new Random(id);
    }

    @Override
    public void _tick() {

    }

}
