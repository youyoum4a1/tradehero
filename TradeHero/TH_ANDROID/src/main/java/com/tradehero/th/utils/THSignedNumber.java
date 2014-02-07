package com.tradehero.th.utils;

import java.text.DecimalFormat;

/** Created with IntelliJ IDEA. User: nia Date: 16/10/13 Time: 3:46 PM */
public class THSignedNumber
{
    public static final String TAG = THSignedNumber.class.getSimpleName();

    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_MONEY = 2;

    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    private final boolean withSign;
    private final int signType;
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
        this(type, number, withSign, null, TYPE_SIGN_ARROW);
    }

    public THSignedNumber(int type, Double number, String currency)
    {
        this(type, number, true, currency, TYPE_SIGN_ARROW);
    }

    public THSignedNumber(int type, Double number, boolean withSign, String currency)
    {
        this(type, number, withSign, currency, TYPE_SIGN_ARROW);
    }

    public THSignedNumber(int type, Double number, boolean withSign, String currency, int signType)
    {
        this.type = type;
        this.number = number;
        this.withSign = withSign;
        this.signType = signType;

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

    private String getSignPrefix()
    {
        switch (signType)
        {
            case TYPE_SIGN_ARROW:
                return NumberDisplayUtils.getArrowPrefix(number);

            case TYPE_SIGN_MINUS_ONLY:
                return NumberDisplayUtils.getMinusOnlyPrefix(number);

            case TYPE_SIGN_PLUS_MINUS_ALWAYS:
                return NumberDisplayUtils.getPlusMinusPrefix(number);

            default:
                throw new IllegalArgumentException("Unhandled signType: " + signType);
        }
    }

    // Private
    private String signedFormattedPercentage(int precision)
    {
        sign = withSign ? getSignPrefix() : "";
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
        sign = withSign ? getSignPrefix() : "";
        if (precision < 0)
        {
            precision = precisionFromNumber();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#,###");
        if (precision > 0)
        {
            sb.append('.');
            for (int i = 0; i < precision; ++i)
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
        double absNumber = Math.abs(number);
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
