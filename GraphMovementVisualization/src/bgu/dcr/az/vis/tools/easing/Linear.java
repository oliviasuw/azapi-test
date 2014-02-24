package bgu.dcr.az.vis.tools.easing;

public class Linear  implements EasingFunction{

    public float easeNone(float t, float b, float c, float d) {
        return c * t / d + b;
    }

    public float easeIn(float t, float b, float c, float d) {
        return c * t / d + b;
    }

    public float easeOut(float t, float b, float c, float d) {
        return c * t / d + b;
    }

    public float easeInOut(float t, float b, float c, float d) {
        return c * t / d + b;
    }
}
