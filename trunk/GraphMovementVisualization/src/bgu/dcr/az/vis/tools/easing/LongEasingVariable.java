/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public class LongEasingVariable extends EasingVariable<Long> {

    public LongEasingVariable(EasingFunction function, EasingFunctinType efType, long initialValue) {
        super(function, efType, initialValue);
    }

    public LongEasingVariable(EasingFunction function, EasingFunctinType efType) {
        super(function, efType, 0L);
    }

    @Override
    protected void onTargetValueChanged() {
    }

    @Override
    protected Long doUpdate() {
        return (long)ease(getBeginingValue(), getTargetValue() - getBeginingValue());
    }
}
