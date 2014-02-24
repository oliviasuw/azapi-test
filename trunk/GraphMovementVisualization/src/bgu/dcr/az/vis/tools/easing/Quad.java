package bgu.dcr.az.vis.tools.easing;

public class Quad  implements EasingFunction{

    public float easeIn(float t, float b, float c, float d) {
        return c * (t /= d) * t + b;
    }

    public float easeOut(float t, float b, float c, float d) {
        return -c * (t /= d) * (t - 2) + b;
    }

    public float easeInOut(float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t + b;
        }
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }
}
