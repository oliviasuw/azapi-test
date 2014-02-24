package bgu.dcr.az.vis.tools.easing;

public class Circ  implements EasingFunction{

    public float easeIn(float t, float b, float c, float d) {
        return -c * ((float) Math.sqrt(1 - (t /= d) * t) - 1) + b;
    }

    public float easeOut(float t, float b, float c, float d) {
        return c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
    }

    public float easeInOut(float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) {
            return -c / 2 * ((float) Math.sqrt(1 - t * t) - 1) + b;
        }
        return c / 2 * ((float) Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    }
}
