package bgu.dcr.az.dev.debug;

import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.impl.tools.DFSPsaudoTree;

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
