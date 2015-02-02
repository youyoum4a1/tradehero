package com.tradehero.th.utils;

/**
 * Created by palmer on 15/1/30.
 */
public class IntegerUtils {

    public static boolean isOne(int target, int number) {
        if (number == 0) {
            return false;
        }
        int result = 0;
        if (target == 0) {
            result = number % 2;
            if (result == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            int next = number >> target;
            result = next % 2;
            if (result == 0) {
                return false;
            } else {
                return true;
            }
        }
    }
}
