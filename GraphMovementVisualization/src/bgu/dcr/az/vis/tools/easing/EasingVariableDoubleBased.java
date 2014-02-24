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

    private static LinearDouble LINEAR = new LinearDouble();
    private EasingFunctionDoubleBased function;
    private EasingFunctinTypeDouble efType;
    private T beginingValue;
    private T currentValue;
    private T targetValue;
    private long transitionTime = 0;
    private long startTime = 0;
    
    public EasingVariableDoubleBased(EasingFunctionDoubleBased function, EasingFunctinTypeDouble efType, T initialValue) {
        this.function = function;
        this.efType = efType;
        this.currentValue = initialValue;
        this.targetValue = initialValue;
    }

    protected double ease(double begining, double change) {
        return efType.ease(function, System.currentTimeMillis() - startTime, begining, change, transitionTime);
    }

    protected abstract void onTargetValueChanged();

    protected abstract T doUpdate();

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
        this.transitionTime = transitionTime;
        this.beginingValue = currentValue;
        this.startTime = System.currentTimeMillis();
    }

    public void update() {
        if (System.currentTimeMillis() - startTime <= transitionTime) {
            currentValue = doUpdate();
        }
        else {
            currentValue = targetValue;
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
