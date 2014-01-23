package com.tradehero.th.utils;

import java.text.DecimalFormat;

/** Created with IntelliJ IDEA. User: nia Date: 16/10/13 Time: 3:46 PM */
public class THSignedNumber
{
    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_MONEY = 2;

    private final boolean withSign;
    private int type;
    private String sign;
    private String currency;
    private Double number;
    private String formattedNumber;
    private int color;

    public THSignedNumber(int type, Double number)
    {
        this(type, number, null);
    }

    public THSignedNumber(int type, Double number, boolean withSign)
    {
        this(type, number, withSign, null);
    }

    public THSignedNumber(int type, Double number, String currency)
    {
        this(type, number, true, currency);
    }

    public THSignedNumber(int type, Double number, boolean withSign, String currency)
    {
        this.type = type;
        this.number = number;
        this.withSign = withSign;

        if (type == TYPE_MONEY && currency == null)
        {
            this.currency = SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
        }
        else
        {
            this.currency = currency;
        }
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

    public String toString(int precision)
    {
        if (formattedNumber != null)
        {
            return formattedNumber;
        }

        switch (type)
        {
            case TYPE_PERCENTAGE:
                formattedNumber = signedFormattedPercentage(precision);
                break;
            case TYPE_MONEY:
                formattedNumber = signedFormattedMoney(precision);
        }
        return formattedNumber;
    }

    @Override public String toString()
    {
        return toString(-1);
    }

    // Private
    private String signedFormattedPercentage(int precision)
    {
        sign = withSign ? NumberDisplayUtils.getArrowPrefix(number) : "";
        if (precision < 0)
        {
            precision = precisionFromNumber();
        }

        color = ColorUtils.getColorResourceForNumber(number);
        String numberFormat = "%s%." + precision + "f";

        return String.format(numberFormat,
                sign,
                Math.abs(number)) + "%";
    }

    private String signedFormattedMoney(int precision)
    {
        sign = withSign ? NumberDisplayUtils.getArrowPrefix(number) : "";
        if (precision < 0)
        {
            precision = precisionFromNumber();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#,###");
        if (precision > 0)
        {
            sb.append('.');
            for (int i=0; i<precision; ++i)
            {
                sb.append('#');
            }
        }
        DecimalFormat df = new DecimalFormat(sb.toString());

        color = ColorUtils.getColorResourceForNumber(number);
        String numberFormat = "%s%s %s";

        return String.format(numberFormat,
                sign,
                currency,
                df.format(Math.abs(number)));
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
        else
        {
            precision = 4;
        }
        return precision;
    }
}
