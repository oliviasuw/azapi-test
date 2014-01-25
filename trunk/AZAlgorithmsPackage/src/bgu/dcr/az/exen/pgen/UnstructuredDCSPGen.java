/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.pgen;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register("dcsp-unstructured")
public class UnstructuredDCSPGen extends AbstractProblemGenerator {

    public int n = 2;
    public int d = 2;
    public float p1 = 0.6f;
    public float p2 = 0.4f;

    /**
     * @propertyName d
     * @return
     */
    public int getD() {
        return d;
    }

    /**
     * @propertyName n
     * @return
     */
    public int getN() {
        return n;
    }

    /**
     * @propertyName p1
     * @return
     */
    public float getP1() {
        return p1;
    }

    /**
     * @propertyName p2
     * @return
     */
    public float getP2() {
        return p2;
    }

    public void setD(int d) {
        this.d = d;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setP1(float p1) {
        this.p1 = p1;
    }

    public void setP2(float p2) {
        this.p2 = p2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d);
        return sb.toString();
    }

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.DCSP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand, p2);
                }
            }
        }
    }

    private void buildConstraint(int i, int j, Problem p, boolean sym, Random rand, float p2) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j) {
                    continue;
                }
                if (rand.nextDouble() < p2) {
                    final int cost = 1;
                    p.setConstraintCost(i, vi, j, vj, cost);
                    if (sym) {
                        p.setConstraintCost(j, vj, i, vi, cost);
                    }
                }
            }
        }
    }
}
