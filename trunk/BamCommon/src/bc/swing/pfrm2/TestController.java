/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.ds.TimeDelta;
import bc.dsl.SwingDSL;
import bc.swing.pfrm2.view.FormNodeView;
import bc.swing.pfrm2.view.TableNodeView;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;

/**
 *
 * @author bennyl
 */
@NodeDef(id = "TestController", view = FormNodeView.class, att = {FormNodeView.ATT_COLOR_LABELS, "false"})
public class TestController extends Controller {

    private String testString;
    private String testString2 = "this is the second String";
    private List<String> someData = Arrays.asList("a b", "c d", "e f", "g h");
    private Set<String> bols = new HashSet<String>();
    
    @NodeDef(id = "test string", att = {FormNodeView.ATT_CAPTION, "TestString Cap"})
    public String getTestString() {
        return testString;
    }

    @NodeDef(id = "second test")
    public String getTestString2() {
        return testString2;
    }

    private void cfgTestString2(Node n) {
        n.putAtt(Att.FOREGROUND_COLOR, Color.blue);
    }

    @ItemExpander(columns = {"TEST1", "TEST2", "TEST3"}, forId = "some data")
    private Object expandSomeData(String column, String from) {
        if (column.equals("TEST3")){
            return bols.contains(from);
        }
        
        if (column.equals("TEST1")) {
            return from.split("\\s+")[0];
        }
        
        return from.split("\\s+")[1];
    }
    
    @ItemEditor(columns={"TEST3"}, forId="some data")
    private void editSomeData(String column, String from, Boolean newVal){
        if (newVal){
            bols.add(from);
        }else {
            bols.remove(from);
        }
    }

    @NodeDef(id = "some data", view=TableNodeView.class)
    public List<String> getSomeData() {
        return someData;
    }

    public static void main(String[] args) {
        SwingDSL.configureUI();
        TestController testController = new TestController();
        TimeDelta td = new TimeDelta();
        td.setStart();
        Node n = testController.getNode();
        td.setEnd();
        
        System.out.println("Time for node creation: " + td.toString());
//        n.writeToXML();
        
        JFrame test = new JFrame("TEST");
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.setContentPane(n.getView());
        test.pack();
        test.setVisible(true);

    }
}
