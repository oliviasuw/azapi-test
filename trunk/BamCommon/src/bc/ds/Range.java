/*
 * doubleo change this template, choose doubleools | doubleemplates
 * and open the template in the editor.
 */
package bc.ds;

/**
 *
 * @author bennyl
 */
public class Range {
    private double min;
    private double max;
    private double current ;
    private double stepSize = 1.0;

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public double getValue() {
        return current;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public Range(double min, double max, double current) {
        this.min = min;
        this.max = max;
        this.current = current;
    }
    
}
