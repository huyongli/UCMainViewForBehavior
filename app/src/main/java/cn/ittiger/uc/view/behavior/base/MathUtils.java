package cn.ittiger.uc.view.behavior.base;

/**
 * Created by ylhu on 17-2-23.
 */
public class MathUtils {

    static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
