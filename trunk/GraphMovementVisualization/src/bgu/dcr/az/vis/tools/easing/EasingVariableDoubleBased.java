/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.tools.easing;

/**
 *
 * @author Shl
 */
public abstract class EasingVariableDoubleBased<T> {

    private static final LinearDouble LINEAR = new LinearDouble();
    private final EasingFunctionDoubleBased function;
    private final EasingFunctinTypeDouble efType;
    private T beginingValue;
    private T currentValue;
    private T targetValue;

    public EasingVariableDoubleBased(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType, T initialValue, T targetValue) {
        this.function = function;
        this.efType = efType;
        this.beginingValue = initialValue;
        this.currentValue = initialValue;
        this.targetValue = targetValue;
    }

    protected double ease(double percentage, double begining, double change) {
        return efType.ease(function, percentage, begining, change, 1);
    }
    
    protected abstract void onTargetValueChanged();

    protected abstract T doUpdate(double percentage);

    public T getCurrentValue() {
        return currentValue;
    }

    protected T getTargetValue() {
        return targetValue;
    }

    protected T getBeginingValue() {
        return beginingValue;
    }

    public void change(T targetValue, long transitionTime) {
        this.targetValue = targetValue;
        this.beginingValue = currentValue;
    }

    public void update(double percentage) {
        if (percentage >= 0 && percentage <= 1) {
            currentValue = doUpdate(percentage);
        } else {
            currentValue = percentage < 0 ? beginingValue : targetValue;
        }
    }

    public static enum EasingFunctinTypeDouble {

        EASE_IN {
                    @Override
                    public double ease(EasingFunctionDoubleBased func, double t, double b, double c, double d) {
                        return func.easeIn(t, b, c, d);
                    }
                }, EASE_OUT {
                    @Override
                    public double ease(EasingFunctionDoubleBased func, double t, double b, double c, double d) {
                        return func.easeOut(t, b, c, d);
                    }
                }, EASE_IN_OUT {
                    @Override
                    public double ease(EasingFunctionDoubleBased func, double t, double b, double c, double d) {
                        return func.easeInOut(t, b, c, d);
                    }
                }, EASE_NONE {
                    @Override
                    public double ease(EasingFunctionDoubleBased func, double t, double b, double c, double d) {
                        return LINEAR.easeNone(t, b, c, d);
                    }
                };

        public abstract double ease(EasingFunctionDoubleBased func, double t, double b, double c, double d);
    }
}
