package bgu.csp.az.dev.debug;

import bgu.csp.az.api.Continuation;
import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.impl.tools.DFSPsaudoTree;

@Algorithm(name = "NestedCheck", problemType= ProblemType.DCOP)
public class NestedCheckAgent extends SimpleAgent {

    TestingNestedTool tool = new TestingNestedTool();
    DFSPsaudoTree tree = new DFSPsaudoTree();

    @Override
    public void start() {
        log("in outer agent - bfore starting nested agent");
        tree.calculate(this).andWhenDoneDo(new Continuation() {

            @Override
            public void doContinue() {
                log("back in outer agent - before starting inner nested agent");
                tool.calculate(NestedCheckAgent.this).andWhenDoneDo(new Continuation() {

                    @Override
                    public void doContinue() {
                        log("back in upper agent!");
                        log("and the nestead agent was run? " + tool.isAlreadyRun());
                        finish();
                    }
                });

            }
        });





    }
}
