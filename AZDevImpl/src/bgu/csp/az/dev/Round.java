/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bgu.csp.az.api.AlgorithmMetadata;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemSequence;
import bgu.csp.az.impl.pbuild.MapProblemBuilder;
import bgu.csp.az.impl.prob.MatrixProblem;
import bgu.csp.az.impl.pseq.RandomProblemSequence;

import static bam.utils.JavaUtils.*;
import bgu.csp.az.impl.pseq.ConnectivityProblemSequence;
import bgu.csp.az.impl.pbuild.MatrixProblemBuilder;
import bgu.csp.az.impl.prob.MapProblem;

/**
 *
 * @author bennyl
 */
public class Round {

    int length;
    int n;
    int d;
    int maxCost;
    float p1;
    float p2Tick = 0.1f;
    float p2Start = 0.1f;
    float p2End = 0.9f;
    int number;
    Problem fromProblem = null;

    public Round(int length, int n, int d, int maxCost, float p1, int number) {
        this.length = length;
        this.n = n;
        this.d = d;
        this.maxCost = maxCost;
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
                cint(p.getMetadata().get(RandomProblemSequence.MAX_COST_PROBLEM_METADATA)),
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

    @Override
    public String toString() {
        return "Round #" + number;
    }

    public ProblemSequence generateProblemSequance(final AlgorithmMetadata alg) {
        if (fromProblem != null) {
            return generateProblemSequanceFromAProblem();
        }

        return new ProblemSequence() {

            int current = 0;
            float steps = (p2End - p2Start) / p2Tick + 1;
            int split = (int) Math.ceil(length / steps);
            float cp2 = p2Start;

            @Override
            public Problem next() {
                current++;
                if (current % split == 0) {
                    cp2 += p2Tick;
                }
                switch (alg.getProblemType()) {
                    case CSP: 
                        maxCost = 1;
                        //INTENDED FALL DOWN - DONT PUT BREAK HERE!
                    case COP:
                        return new RandomProblemSequence(p1, cp2, maxCost, n, d, System.currentTimeMillis(), 1, new MapProblemBuilder(n, d), alg.getProblemType()).next();
                    case ADCOP:
                        return new RandomProblemSequence(p1, cp2, maxCost, n, d, System.currentTimeMillis(), 1, new MapProblemBuilder(n, d), alg.getProblemType()).next(); 
                    case CONNECTED_COP:
                        return new ConnectivityProblemSequence(p1, cp2, maxCost, n, d, System.currentTimeMillis(), 1).next();                       
                    default:
                        throw new UnsupportedOperationException("Not Supporting this problem type: " + alg.getProblemType());
                }
            }

            @Override
            public boolean hasNext() {
                return current <= length;
            }
        };

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
