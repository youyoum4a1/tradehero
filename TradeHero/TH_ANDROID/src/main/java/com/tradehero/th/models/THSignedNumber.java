package com.tradehero.th.models;

import com.tradehero.th.R;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 16/10/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class THSignedNumber {
    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_MONEY = 2;
    public static final String REF_CURRENCY = "US$";

    private int type;
    private String sign;
    private int precision;
    private String currency;
    private Double number;
    private String formattedNumber;
    private int color;

    public THSignedNumber(int type, Double number) {
        this(type, null, number);
        formattedNumber = toString();
    }

    public THSignedNumber(int type, String currency, Double number) {
        this.type = type;
        this.number = number;
        if (type == TYPE_MONEY && currency == null) {
            this.currency = REF_CURRENCY;
        }
        formattedNumber = toString();
    }

    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

    public boolean isSigned() {
        return sign != null;
    }

    public String toString() {
        if (formattedNumber != null) {
            return formattedNumber;
        }

        switch (type) {
            case TYPE_PERCENTAGE:
                formattedNumber = signedFormattedPercentage();
                break;
            case TYPE_MONEY:
                formattedNumber = signedFormattedMoney();
        }
        return formattedNumber;
    }

    // Private
    private String signedFormattedPercentage() {
        sign = upDownSignFromNumber();
        precision = precisionFromNumber();
        color = colorFromNumber();
        String numberFormat = "%s%." + precision + "f";

        return String.format(numberFormat,
                sign,
                number) + "%";
    }

    private String signedFormattedMoney() {
        sign = plusMinusSignFromNumber();
        precision = precisionFromNumber();
        color = colorFromNumber();
        String numberFormat = "%s%s %." + precision + "f";

        return String.format(numberFormat,
                sign,
                currency,
                number);
    }

    private String upDownSignFromNumber() {
        String sign = "";
        if (number > 0) {
            sign = "▲";
        } else if (number < 0) {
            sign = "▼";
        }
        return sign;
    }

    private String plusMinusSignFromNumber() {
        String sign = "";
        if (number > 0) {
            sign = "+";
        } else if (number < 0) {
            sign = "-";
        }
        return sign;
    }

    private int colorFromNumber() {
        int color = R.color.black;
        if (number > 0) {
            color = R.color.number_green;
        } else if (number < 0) {
            color = R.color.number_red;
        }
        return color;
    }

    private int precisionFromNumber() {
        int precision = 4;
        Double absNumber = Math.abs(number);
        if (absNumber >= 1000) {
            precision = 0;
        } else if (absNumber >= 100) {
            precision = 1;
        } else if (absNumber >= 10) {
            precision = 2;
        } else if (absNumber >= 1) {
            precision = 3;
        }
        return precision;
    }
}
