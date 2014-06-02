package bgu.dcr.az.dcr.api;

import bgu.dcr.az.dcr.api.annotations.Algorithm;
import bgu.dcr.az.dcr.api.problems.ImmutableProblem;

/**
 * This class was replaced with {@code Agent}, it is still available for
 * historical reasons - but it is an empty class that inherit from Agent. you
 * can continue using it or inherit directly from Agent
 *
 * @author bennyl
 */
@Algorithm(".simple-agent")
public abstract class SimpleAgent extends CPAgent<ImmutableProblem> {    
}
