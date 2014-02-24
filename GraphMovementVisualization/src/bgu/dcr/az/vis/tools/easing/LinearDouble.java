package bgu.dcr.az.vis.tools.easing;

public class LinearDouble implements EasingFunctionDoubleBased{

    public double easeNone(double t, double b, double c, double d) {
        return c * t / d + b;
    }

    public double easeIn(double t, double b, double c, double d) {
        return c * t / d + b;
    }

    public double easeOut(double t, double b, double c, double d) {
        return c * t / d + b;
    }

    public double easeInOut(double t, double b, double c, double d) {
        return c * t / d + b;
    }
}
