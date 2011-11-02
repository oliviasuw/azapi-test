/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemSequence;
import bgu.csp.az.impl.pseq.RandomProblemSequence;

import static bam.utils.JavaUtils.*;
import bgu.csp.az.impl.pseq.ConnectivityProblemSequence;

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
    float p2Tick = 0.1f;
    float p2Start = 0.1f;
    float p2End = 0.9f;
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

    public void setP2End(float p2End) {
        this.p2End = p2End;
    }

    public void setP2Start(float p2Start) {
        this.p2Start = p2Start;
    }

    public void setP2Tick(float p2Tick) {
        this.p2Tick = p2Tick;
    }

    public float getP2End() {
        return p2End;
    }

    public float getP2Start() {
        return p2Start;
    }

    public float getP2Tick() {
        return p2Tick;
    }

    public Round(Problem p) {
        this(1, //Round Length
                p.getNumberOfVariables(),
                p.getDomainSize(0),
                cint(p.getMetadata().get(ConnectivityProblemSequence.MAX_COST_PROBLEM_METADATA)),
                "Resurected Problem",
                cfloat(p.getMetadata().get(ConnectivityProblemSequence.P1_PROBLEM_METADATA)),
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
            return generateProblemSequanceFromAProblem();
        }

        return new ProblemSequence() {

            int current = 0;
            float steps = (p2End - p2Start)/p2Tick + 1;
            int split = (int) Math.ceil(length / steps);
            float cp2 = p2Start;
            
            @Override
            public Problem next() {
                current++;
                if (current % split == 0) cp2 += p2Tick;
                return new ConnectivityProblemSequence(p1, cp2, maxCost, n, d, System.currentTimeMillis(), 1).next();
            }

            @Override
            public boolean hasNext() {
                return current <= length;
            }
        }
        ;
    }

    private ProblemSequence generateProblemSequanceFromAProblem() {
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
}
