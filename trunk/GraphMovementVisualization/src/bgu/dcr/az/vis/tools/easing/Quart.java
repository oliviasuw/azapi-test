package bgu.dcr.az.vis.tools.easing;

public class Quart implements EasingFunction {

    public float easeIn(float t, float b, float c, float d) {
        return c * (t /= d) * t * t * t + b;
    }

    public float easeOut(float t, float b, float c, float d) {
        return -c * ((t = t / d - 1) * t * t * t - 1) + b;
    }

    public float easeInOut(float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t * t * t + b;
        }
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    }
}
