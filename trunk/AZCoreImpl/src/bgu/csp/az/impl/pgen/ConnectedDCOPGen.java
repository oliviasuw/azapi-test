/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pgen;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.pgen.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name="dcop-connected")
public class ConnectedDCOPGen extends UnstructuredDCOPGen {

    @Override
    public void generate(Problem p, Random rand, float p1, float p2) {
        super.generate(p, rand, p1, p2);
        while (true) {
            boolean[] connections = new boolean[p.getNumberOfVariables()];
            calcConnectivity(p, 0, connections);
            if (allTrue(connections)) {
                return;
            }

            connect(0, getUnconnected(connections), p, rand);
        }
    }

    private int getUnconnected(boolean[] connections) {
        for (int i = 0; i < connections.length; i++) {
            if (!connections[i]) {
                return i;
            }
        }
        return -1;
    }

    private void connect(int var1, int var2, Problem p, Random rnd) {
        int val2 = rnd.nextInt(p.getDomainSize(var2));
        int val1 = rnd.nextInt(p.getDomainSize(var1));
        double cost = rnd.nextInt(super.maxCost - 1) + 1;
        p.setConstraintCost(var1, val1, var2, val2, cost);
        p.setConstraintCost(var2, val2, var1, val1, cost);
    }

    private void calcConnectivity(Problem p, int root, boolean[] discovered) {
        discovered[root] = true;
        for (int n : p.getNeighbors(root)) {

            if (!discovered[n]) {
                calcConnectivity(p, n, discovered);
            } else if (allTrue(discovered)) {
                return;
            }
        }
    }

    private boolean allTrue(boolean[] a) {
        for (int i = 0; i < a.length; i++) {
            if (!a[i]) {
                return false;
            }
        }
        return true;
    }
}
