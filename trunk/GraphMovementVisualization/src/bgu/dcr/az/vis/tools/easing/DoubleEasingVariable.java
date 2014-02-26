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
    private double change;
    
    public DoubleEasingVariable(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType, double initialValue, double finalValue) {
        super(function, efType, initialValue, finalValue);
        
        change = finalValue - initialValue;
    }

    public DoubleEasingVariable(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType) {
        super(function, efType, 0d, 0d);
    }

    @Override
    protected void onTargetValueChanged() {
    }

    @Override
    protected Double doUpdate(double percentage) {
        return ease(percentage, getBeginingValue(), change);
    }
    
}
