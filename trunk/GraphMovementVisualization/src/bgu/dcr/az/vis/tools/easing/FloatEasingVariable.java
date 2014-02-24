/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public class FloatEasingVariable extends EasingVariable<Float> {

    public FloatEasingVariable(EasingFunction function, EasingFunctinType efType, float initialValue) {
        super(function, efType, initialValue);
    }

    public FloatEasingVariable(EasingFunction function, EasingFunctinType efType) {
        super(function, efType, 0f);
    }

    @Override
    protected void onTargetValueChanged() {
    }

    @Override
    protected Float doUpdate() {
        return ease(getBeginingValue(), getTargetValue() - getBeginingValue());
    }
}
