/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.ds.Range;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.ParamView;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author bennyl
 */
public class NumberRangePV extends JSpinner implements ParamView {

    public void setParam(BaseParamModel param) {
        Range r = pval(param);
        SpinnerNumberModel m = null;
        try {
            m = new SpinnerNumberModel(r.getValue(), r.getMin(), r.getMax(), r.getStepSize());
        } catch (java.lang.IllegalArgumentException ex) {
            System.err.println("NumberRangePV - detected bad range value: " + r.getValue() + " defaulting to minimum in range.");
            m = new SpinnerNumberModel(r.getMin(), r.getMin(), r.getMax(), r.getStepSize());
        }
        setModel(m);
    }

    private Range pval(BaseParamModel param) {
        return (Range) param.getValue();
    }

    public void reflectChangesToParam(BaseParamModel to) {
        pval(to).setCurrent((Double) getValue());
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        setValue(pval(source).getValue());
    }
}
