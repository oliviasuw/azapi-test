/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.scan;

import bc.swing.pfrm.DeltaHint;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public interface Adapter<IN,OUT> {
    void setParams(Map<String, String> params);
    void configure(IN in, OUT out);
    void syncIn();
    void syncOut(DeltaHint deltaHint);
    IN getIn();
    OUT getOut();
    
}
