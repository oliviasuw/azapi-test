/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.mdelay;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name = "default-message-delayer")
public class DefaultMessageDelayer implements MessageDelayer {

    @Variable(name = "type",
              description = "the type of the delay: NCCC/NCSC, each depends on the corresponding statistic collectors",
              defaultValue = "NCCC")
    String type = "NCCC";
    @Variable(name = "seed",
              description = "seed for the randomization of the delay",
              defaultValue = "42")
    int seed = 42;
    @Variable(name = "maximum-delay",
              description = "the maximum delay that the delayer can produce for a message",
              defaultValue = "100")
    int maximumDelay = 100;
    @Variable(name = "minimum-delay",
              description = "the minimum delay that the delayer can produce for a message",
              defaultValue = "0")
    int minimumDelay = 0;
    
    int[][] previousTime;
    Random rnd = null;

    @Override
    public int getInitialTime() {
        return 0;
    }

    @Override
    public int extractTime(Message m) {
        if (type.equals("NCCC")) {
            return ((Long) m.getMetadata().get("nccc")).intValue();
        } else {
            return ((Long) m.getMetadata().get("ncsc")).intValue();
        }
    }

    @Override
    public void addDelay(Message m, int from, int to) {
        int delay = rnd.nextInt(maximumDelay - minimumDelay) + minimumDelay;
        int ntime = Math.max(delay + previousTime[from][to], extractTime(m) + delay);
        previousTime[from][to] = ntime;
        if (type.equals("NCCC")) {
            m.getMetadata().put("nccc", (long)ntime);
        } else {
            m.getMetadata().put("ncsc", (long)ntime);
        }
    }

    @Override
    public void initialize(Execution ex) {
        int n = ex.getGlobalProblem().getNumberOfVariables();
        previousTime = new int[n][n];
        rnd = new Random(seed);
    }
}
