package com.tradehero.th.utils;

import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: nia Date: 16/10/13 Time: 3:46 PM */
public class THSignedNumber
{
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

    public THSignedNumber(int type, Double number)
    {
        this(type, null, number);
        formattedNumber = toString();
    }

    public THSignedNumber(int type, String currency, Double number)
    {
        this.type = type;
        this.number = number;
        if (type == TYPE_MONEY && currency == null)
        {
            this.currency = REF_CURRENCY;
        }
        formattedNumber = toString();
    }

    public int getColor()
    {
        return color;
    }

    public int getType()
    {
        return type;
    }

    public boolean isSigned()
    {
        return sign != null;
    }

    public String toString()
    {
        if (formattedNumber != null)
        {
            return formattedNumber;
        }

        switch (type)
        {
            case TYPE_PERCENTAGE:
                formattedNumber = signedFormattedPercentage();
                break;
            case TYPE_MONEY:
                formattedNumber = signedFormattedMoney();
        }
        return formattedNumber;
    }

    // Private
    private String signedFormattedPercentage()
    {
        sign = NumberDisplayUtils.getArrowPrefix(number);
        precision = precisionFromNumber();
        color = ColorUtils.getColorResourceForNumber(number);
        String numberFormat = "%s%." + precision + "f";

        return String.format(numberFormat,
                sign,
                Math.abs(number)) + "%";
    }

    private String signedFormattedMoney()
    {
        sign = NumberDisplayUtils.getPlusMinusPrefix(number);
        precision = precisionFromNumber();
        color = ColorUtils.getColorResourceForNumber(number);
        String numberFormat = "%s%s %." + precision + "f";

        return String.format(numberFormat,
                sign,
                currency,
                Math.abs(number));
    }

    private int precisionFromNumber()
    {
        int precision = 4;
        Double absNumber = Math.abs(number);
        if (absNumber >= 1000)
        {
            precision = 0;
        }
        else if (absNumber >= 100)
        {
            precision = 1;
        }
        else if (absNumber >= 10)
        {
            precision = 2;
        }
        else if (absNumber >= 1)
        {
            precision = 3;
        }
        return precision;
    }
}
