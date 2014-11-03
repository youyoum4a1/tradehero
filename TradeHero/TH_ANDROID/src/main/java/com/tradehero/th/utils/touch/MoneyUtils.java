package com.tradehero.th.utils.touch;

import android.content.Context;
import com.tradehero.th.R;

/**
 * Created by palmer on 14-11-3.
 */
public class MoneyUtils {

    private final static double HUNDRED_MILLION = 100000000;
    private final static double HUNDRED_MILLION_NEGATIVE = -100000000;
    private final static double MILLION = 1000000;
    private final static double MILLION_NEGATIVE = -1000000;
    private final static double TEN_THOUSAND = 10000;
    private final static double TEN_THOUSAND_NEGATIVE = -10000;
    private final static double TEN = 10;
    private final static double TEN_NEGATIVE = -10;

    public static String convertMoneyStr(double money, Context context, String currency) {
        int result = 0;
        if (money > 0) {
            if (money > HUNDRED_MILLION) {
                result = (int) (money / HUNDRED_MILLION);
                String unit = context.getResources().getString(R.string.hundred_million);
                return currency + " " + result + unit;
            }
            if (money > MILLION) {
                result = (int) (money / MILLION);
                String unit = context.getResources().getString(R.string.million);
                return currency + " " + result + unit;
            }
            if (money > TEN_THOUSAND) {
                result = (int) (money / TEN_THOUSAND);
                String unit = context.getResources().getString(R.string.ten_thousands);
                return currency + " " + result + unit;
            }
            if (money < TEN) {
                return currency + " " + String.format("%.2f", money);
            }
            return currency + " " + (int) money;
        }
        if (money == 0) {
            return currency + " " + String.valueOf((int) money);
        }
        if (money < 0) {
            if (money < HUNDRED_MILLION_NEGATIVE) {
                result = (int) (money / HUNDRED_MILLION);
                String unit = context.getResources().getString(R.string.hundred_million);
                return "-" + currency + " " + (0 - result) + unit;
            }
            if (money < MILLION_NEGATIVE) {
                result = (int) (money / MILLION);
                String unit = context.getResources().getString(R.string.million);
                return "-" + currency + " " + (0 - result) + unit;
            }
            if (money < TEN_THOUSAND_NEGATIVE) {
                result = (int) (money / TEN_THOUSAND);
                String unit = context.getResources().getString(R.string.ten_thousands);
                return "-" + currency + " " + (0 - result) + unit;
            }
            if (money > TEN_NEGATIVE) {
                return "-" + currency + " " + String.format("%.2f", (0 - money));
            }
            return "-" + currency + " " + (int) (0 - money);
        }
        return String.valueOf(currency + " " + result);
    }

}
