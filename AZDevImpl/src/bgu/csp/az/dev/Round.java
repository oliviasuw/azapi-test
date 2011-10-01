/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemSequence;
import bgu.csp.az.impl.pseq.RandomProblemSequence;

import static bam.utils.JavaUtils.*;

/**
 *
 * @author bennyl
 */
public class Round {

    int length;
    int n;
    int d;
    int maxCost;
    String type;
    float p1;
    int number;
    Problem fromProblem = null;

    public Round(int length, int n, int d, int maxCost, String type, float p1, int number) {
        this.length = length;
        this.n = n;
        this.d = d;
        this.maxCost = maxCost;
        this.type = type;
        this.p1 = p1;
        this.number = number;
    }

    public Round(Problem p) {
        this(1, //Round Length
                p.getNumberOfVariables(),
                p.getDomainSize(0),
                cint(p.getMetadata().get(RandomProblemSequence.MAX_COST_PROBLEM_METADATA)),
                "Resurected Problem",
                cfloat(p.getMetadata().get(RandomProblemSequence.P1_PROBLEM_METADATA)),
                0);//Round Number
        this.fromProblem = p;
    }

    public float getP1() {
        return p1;
    }

    public int getDomainSize() {
        return d;
    }

    public int getLength() {
        return length;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public int getNumberOfVariables() {
        return n;
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Round #" + number;
    }

    public ProblemSequence generateProblemSequance() {
        if (fromProblem != null) {
            return new ProblemSequence() {

                boolean hnext = true;

                @Override
                public Problem next() {
                    hnext = false;
                    return fromProblem;
                }

                @Override
                public boolean hasNext() {
                    return hnext;
                }
            };
        }

        return new ProblemSequence() {

            int current = 0;

            @Override
            public Problem next() {
                current++;
                return new RandomProblemSequence(p1, (float) current / length, maxCost, n, d, System.currentTimeMillis(), 1).next();
            }

            @Override
            public boolean hasNext() {
                return current <= length;
            }
        };
    }
}
