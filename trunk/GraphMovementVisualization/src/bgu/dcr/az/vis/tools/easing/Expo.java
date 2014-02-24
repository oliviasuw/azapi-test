package bgu.dcr.az.vis.tools.easing;

public class Expo  implements EasingFunction{

    public float easeIn(float t, float b, float c, float d) {
        return (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;
    }

    public float easeOut(float t, float b, float c, float d) {
        return (t == d) ? b + c : c * (-(float) Math.pow(2, -10 * t / d) + 1) + b;
    }

    public float easeInOut(float t, float b, float c, float d) {
        if (t == 0) {
            return b;
        }
        if (t == d) {
            return b + c;
        }
        if ((t /= d / 2) < 1) {
            return c / 2 * (float) Math.pow(2, 10 * (t - 1)) + b;
        }
        return c / 2 * (-(float) Math.pow(2, -10 * --t) + 2) + b;
    }
}
