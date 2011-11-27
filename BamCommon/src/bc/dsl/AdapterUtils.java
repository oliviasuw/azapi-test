/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.swing.pfrm.ViewAdapter;
import bc.swing.pfrm.scan.Adapter;
import bc.swing.pfrm.scan.BamRegistary;

/**
 *
 * @author bennyl
 */
public class AdapterUtils {

    public static <IN,OUT>  Adapter<IN,OUT> adapt(String from, String to, IN in, OUT out) {
        Adapter adapter = BamRegistary.UNIT.getAdapter(from, to);
        adapter.configure(in, out);
        return (Adapter<IN, OUT>) adapter;
    }
    
    public static <IN,OUT>  ViewAdapter<IN,OUT> vadapter(String from, String to) {
        Adapter adapter = BamRegistary.UNIT.getAdapter(from, to);
        return (ViewAdapter<IN, OUT>) adapter;
    }
}
