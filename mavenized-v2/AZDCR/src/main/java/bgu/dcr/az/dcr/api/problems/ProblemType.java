/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.problems;

import bgu.dcr.az.dcr.api.problems.cpack.AsymmetricBinaryMapConstraintPackage;
import bgu.dcr.az.dcr.api.problems.cpack.BinaryMapConstraintPackage;
import bgu.dcr.az.dcr.api.problems.cpack.ConstraintsPackage;
import bgu.dcr.az.dcr.api.problems.cpack.KAryTreeConstraintPackage;

/**
 *
 * @author bennyl
 */
public enum ProblemType {

    /**
     * Constraint Satisfaction Problem, K-Ary Constraints allowed
     */
    K_ARY_DCSP (false, false){
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
        }
    },
    /**
     * Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_DCOP (false, false) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
        }
    },
    /**
     * asymmetric Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_ADCOP (true, false) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
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
    ADCOP (true, true) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new AsymmetricBinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    };

    public abstract ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize);
    boolean asymmetric = false;
    boolean binary = true;

    private ProblemType() {
    }

    private ProblemType(boolean asymmetric, boolean binary) {
        this.asymmetric = asymmetric;
        this.binary = binary;
    }

    public boolean isAsymmetric() {
        return asymmetric;
    }

    public boolean isBinary() {
        return binary;
    }
}
