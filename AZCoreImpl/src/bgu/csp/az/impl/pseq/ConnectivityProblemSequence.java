/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pseq;

import bgu.csp.az.impl.prob.MatrixProblem;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.ProblemType;
import java.util.Random;
import bgu.csp.az.impl.pbuild.MatrixProblemBuilder;

/**
 *
 * @author bennyl
 */
public class ConnectivityProblemSequence extends RandomProblemSequence {

    Random rnd;

    public ConnectivityProblemSequence(Configuration sd) {
        super(sd);
        rnd = new Random(sd.getSeed());
    }

    public ConnectivityProblemSequence(float p1, float p2, int maxCost, int n, int d, long seed, int numberOfProblems) {
        super(p1, p2, maxCost, n, d, seed, numberOfProblems, new MatrixProblemBuilder(n, d), ProblemType.CONNECTED_COP);
        rnd = new Random(seed);
    }

    @Override
    public Problem next() {
        MatrixProblem p = (MatrixProblem) super.next();
        while (true){
            boolean[] connections = new boolean[p.getNumberOfVariables()];
            calcConnectivity(p, 0, connections);
            if (allTrue(connections)){
                return p;
            }
            
            connect(0, getUnconnected(connections), p);
        }
    }

    private int getUnconnected(boolean[] connections){
        for (int i=0; i<connections.length; i++) if (!connections[i]) return i;
        return -1;
    }
    
    private void connect(int var1, int var2, MatrixProblem p) {
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
