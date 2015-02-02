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

    public static int toZero(int target, int number){
        switch(target){
            case 0:
                return number&14;
            case 1:
                return number&13;
            case 2:
                return number&11;
            case 3:
                return number&7;
        }
        return 0;
    }

    public static int toOne(int target, int number){
        switch(target){
            case 0:
                return number|1;
            case 1:
                return number|2;
            case 2:
                return number|4;
            case 3:
                return number|8;
        }
        return 0;
    }
}
