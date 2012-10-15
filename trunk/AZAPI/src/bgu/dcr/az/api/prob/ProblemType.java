/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob;

import bgu.dcr.az.api.prob.cpack.ConstraintsPackage;
import bgu.dcr.az.api.prob.cpack.KAryMapConstraintPackage;
import bgu.dcr.az.api.prob.cpack.BinaryMapConstraintPackage;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.prob.cpack.AsymmetricBinaryMapConstraintPackage;

/**
 *
 * @author bennyl
 */
public enum ProblemType {

    /**
     * Constraint Satisfaction Problem, K-Ary Constraints allowed
     */
    K_ARY_DCSP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryMapConstraintPackage(numvars);
        }
    },
    /**
     * Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_DCOP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryMapConstraintPackage(numvars);
        }
    },
    /**
     * asymmetric Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_ADCOP (true) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryMapConstraintPackage(numvars);
        }
    },
    /**
     * Constraint Satisfaction Problem, Only Binary and unary constraints
     * allowed
     */
    DCSP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new BinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    },
    /**
     * Constraint Optimization Problem, Only Binary and unary constraints
     * allowed
     */
    DCOP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new BinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    },
    /**
     * asymmetric Constraint Optimization Problem, Only Binary and unary
     * constraints allowed
     */
    ADCOP (true) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new AsymmetricBinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    };

    public abstract ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize);
    boolean asymmetric = false;

    private ProblemType() {
    }

    private ProblemType(boolean asymmetric) {
        this.asymmetric = asymmetric;
    }

    public boolean isAsymmetric() {
        return asymmetric;
    }
}
