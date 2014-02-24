/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public class DoubleEasingVariable extends EasingVariableDoubleBased<Double> {

    public DoubleEasingVariable(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType, double initialValue) {
        super(function, efType, initialValue);
    }

    public DoubleEasingVariable(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType) {
        super(function, efType, 0d);
    }

    @Override
    protected void onTargetValueChanged() {
    }

    @Override
    protected Double doUpdate() {
        return ease(getBeginingValue(), getTargetValue() - getBeginingValue());
    }
}
