package com.tradehero.th.utils;

import java.text.DecimalFormat;
import org.jetbrains.annotations.NotNull;

public class THSignedNumber
{
    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_MONEY = 2;

    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;

    private final boolean withSign;
    private final int signType;
    private final int type;
    private final String currency;
    private final Double number;
    private String formattedNumber;
    private int color;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    {
        private Double number;
        private int type = TYPE_MONEY;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_ARROW;
        private String currency = null;

        protected abstract BuilderType self();

        public BuilderType money()
        {
            type = TYPE_MONEY;
            return self();
        }

        public BuilderType percentage()
        {
            type = TYPE_PERCENTAGE;
            return self();
        }

        public BuilderType number(double number)
        {
            this.number = number;
            return self();
        }

        public BuilderType withSign()
        {
            withSign = WITH_SIGN;
            return self();
        }

        public BuilderType withOutSign()
        {
            withSign = WITHOUT_SIGN;
            return self();
        }

        public BuilderType signTypeArrow()
        {
            signType = TYPE_SIGN_ARROW;
            return self();
        }

        public BuilderType signTypePlusMinusAlways()
        {
            signType = TYPE_SIGN_PLUS_MINUS_ALWAYS;
            return self();
        }

        public BuilderType signTypeMinusOnly()
        {
            signType = TYPE_SIGN_MINUS_ONLY;
            return self();
        }

        public BuilderType currency(String currency)
        {
            this.currency = currency;
            return self();
        }

        public THSignedNumber build()
        {
            return new THSignedNumber(this);
        }
    }

    private static class Builder2 extends Builder<Builder2>
    {
        @Override protected Builder2 self()
        {
            return this;
        }
    }

    public static Builder<?> builder()
    {
        return new Builder2();
    }

    //<editor-fold desc="Constructors">
    protected THSignedNumber(@NotNull Builder<?> builder)
    {
        this.withSign = builder.withSign;
        this.signType = builder.signType;
        this.type = builder.type;
        this.number = builder.number;

        if (type == TYPE_MONEY && builder.currency == null)
        {
            this.currency = SecurityUtils.getDefaultCurrency();
        }
        else
        {
            this.currency = builder.currency;
        }
    }
    //</editor-fold>

    public int getColor()
    {
        return color;
    }

    public int getType()
    {
        return type;
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
        String sign = withSign ? getSignPrefix() : "";
        if (precision < 0)
        {
            precision = precisionFromNumber();
        }

        color = ColorUtils.getColorResourceIdForNumber(number);
        String numberFormat = "%s%." + precision + "f";

        String trailingZeroRemovedNumber = String.format(numberFormat, sign, Math.abs(number));
        trailingZeroRemovedNumber = removeTrailingZeros(trailingZeroRemovedNumber);

        return  trailingZeroRemovedNumber + "%";
    }

    private String removeTrailingZeros(String formattedNumber)
    {
        if (formattedNumber != null && formattedNumber.contains("."))
        {
            int length = formattedNumber.length();
            do
            {
                length--;
            }
            while (length > 0 && formattedNumber.charAt(length) == '0');

            formattedNumber = formattedNumber.substring(0, length + 1);

            if (formattedNumber.endsWith("."))
            {
                formattedNumber = formattedNumber.substring(0, length);
            }
        }
        return formattedNumber;
    }

    private String signedFormattedMoney(int precision)
    {
        String sign = withSign ? getSignPrefix() : "";
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

        color = ColorUtils.getColorResourceIdForNumber(number);
        String numberFormat = "%s%s %s";

        String trailingZeroRemovedNumber = df.format(Math.abs(number));
        trailingZeroRemovedNumber = removeTrailingZeros(trailingZeroRemovedNumber);

        return String.format(numberFormat,
                sign,
                currency,
                trailingZeroRemovedNumber
                );
    }

    protected int precisionFromNumber()
    {
        int precision;
        double absNumber = Math.abs(number);

        if (absNumber == 0)
        {
            precision = 0;
        }
        else
        {
            precision = Math.max(0, 3 - (int) Math.floor(Math.log10(absNumber)));
        }
        return precision;
    }
}
