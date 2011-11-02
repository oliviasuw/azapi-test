package bgu.csp.az.api;

import bgu.csp.az.api.exp.InvalidValueException;

/**
 *  This is a metadata object. It can be created in the following ways:
 * •	By hand – in a new algorithm project you can create new class that extends Algorithm, and fill the needed metadata.
 * •	By annotation – in the Agent class, you can add the annotation @Algorithm(“name”) and can (but not have to) use @Variable annotations to introduce new algorithm variables (algorithm variables are properties that the algorithm choose to let the execution environment control – this is an advance topic that the implementer can totally ignore – but if he choose to use he will have a great power)
 * •	Automatically generated – if none of the above was included in the algorithm project, an algorithm class is automatically created at run time. The name that the automatic Algorithm will have is according to the Agent class. For example, if you have a SBBAgent, the Algorithm will be called SBB and the algorithm will contain no variables.
 * 
 * @author bennyl
 */
public class AlgorithmMetadata {

    private String name; //the algorithm name
    private Class<? extends Agent> agentClass; //the class of the agent that implements this algorithm
    private ProblemType problemType;
    private boolean useIdleDetector;
    private SearchType searchType;

    
    /**
     * constract an algorithm metadata from an agent class 
     * the agent class must be annotated by @Algorithm annotation.
     * @param agentClass
     */
    public AlgorithmMetadata(Class<? extends Agent> agentClass){
        bgu.csp.az.api.ano.Algorithm a = agentClass.getAnnotation(bgu.csp.az.api.ano.Algorithm.class);
        if (a == null){
            throw new InvalidValueException("no algorithm annotation used on the given agent class");
        }
        
        this.name = a.name();
        this.agentClass = agentClass;
        this.problemType = a.problemType();
        this.useIdleDetector = a.useIdleDetector();
        this.searchType = a.searchType();
    }

    public SearchType getSearchType() {
        return searchType;
    }

    /**
     * @return the algorithm name
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if the algorithm implementer request the platform to use idle detection to close its agents
     */
    public boolean isUseIdleDetector() {
        return useIdleDetector;
    }
    
    
    /**
     * @return the class of the agent implementation that can run this algorithm
     */
    public Class<? extends Agent> getAgentClass() {
        return agentClass;
    }
    
    /**
     * @return the problem type that the algorithm implementer declare its algorithm to solve
     */
    public ProblemType getProblemType(){
        return problemType;
    }
}
