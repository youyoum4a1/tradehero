package com.tradehero.th.utils;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.text.DecimalFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THSignedNumber
{
    public static final int DESIRED_RELEVANT_DIGIT_COUNT = 4;

    //<editor-fold desc="Constants">
    public static final int TYPE_PLAIN = 0;
    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_MONEY = 2;

    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;
    //</editor-fold>

    private final boolean withSign;
    private final int signType;
    private final int type;
    private final String currency;
    private final Double number;
    private final int relevantDigitCount;
    private final String formattedNumber;
    private final int colorResId;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    {
        private Double number;
        private int type = TYPE_PLAIN;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_MINUS_ONLY;
        @Nullable private String currency = null;
        private int relevantDigitCount = DESIRED_RELEVANT_DIGIT_COUNT;

        protected abstract BuilderType self();

        protected boolean isValid()
        {
            return number != null
                    && (type == TYPE_MONEY || currency == null);
        }

        public BuilderType plain()
        {
            type = TYPE_PLAIN;
            return self();
        }

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

        public BuilderType relevantDigitCount(int relevantDigitCount)
        {
            this.relevantDigitCount = relevantDigitCount;
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
        this.relevantDigitCount = builder.relevantDigitCount;

        if (!builder.isValid())
        {
            throw new IllegalArgumentException("Invalid builder");
        }

        if (type == TYPE_MONEY && builder.currency == null)
        {
            this.currency = SecurityUtils.getDefaultCurrency();
        }
        else
        {
            this.currency = builder.currency;
        }
        colorResId = ColorUtils.getColorResourceIdForNumber(number);
        formattedNumber = getFormatted();
    }
    //</editor-fold>

    public int getColorResId()
    {
        return colorResId;
    }

    public int getColor()
    {
        return Application.context().getResources().getColor(getColorResId());
    }

    protected String getFormatted()
    {
        switch (type)
        {
            case TYPE_PLAIN:
                return createFormattedPlain();
            case TYPE_PERCENTAGE:
                return createFormattedPercentage();
            case TYPE_MONEY:
                return createFormattedMoney();

            default:
                throw new IllegalArgumentException("Unhandled THSignedNumber type " + type);
        }
    }

    protected String createPlainNumber()
    {
        int precision = getPrecisionFromNumber();

        DecimalFormat df = new DecimalFormat(getStringFormat(precision).toString());
        String formatted = df.format(Math.abs(number));
        return removeTrailingZeros(formatted);
    }

    protected String createFormattedPlain()
    {
        return String.format(
                "%s%s",
                getConditionalSignPrefix(),
                createPlainNumber());
    }

    protected String createFormattedPercentage()
    {
        return String.format(
                "%s%s%s",
                getConditionalSignPrefix(),
                createPlainNumber(),
                Application.getResourceString(R.string.percentage_suffix));
    }

    protected String createFormattedMoney()
    {
        return String.format(
                "%s%s%s%s",
                getConditionalSignPrefix(),
                currency,
                getCurrencySpace(),
                createPlainNumber());
    }

    protected String getCurrencySpace()
    {
        return currency == null || currency.isEmpty() ? "" : " ";
    }

    public static String removeTrailingZeros(String formattedNumber)
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

    @Override public String toString()
    {
        return formattedNumber;
    }

    public static StringBuilder getStringFormat(int precision)
    {
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
        return sb;
    }

    //<editor-fold desc="Precision">
    protected int getPrecisionFromNumber()
    {
        return getPrecisionFromNumber(number, relevantDigitCount);
    }

    public static int getPrecisionFromNumber(double number, int relevantDigitCount)
    {
        int precision;
        double absNumber = Math.abs(number);

        if (absNumber == 0)
        {
            precision = 0;
        }
        else
        {
            precision = Math.max(0, relevantDigitCount - 1 - (int) Math.floor(Math.log10(absNumber)));
        }
        return precision;
    }
    //</editor-fold>

    //<editor-fold desc="Prefix Signs">
    protected String getConditionalSignPrefix()
    {
        return withSign ? getSignPrefix() : "";
    }

    protected String getSignPrefix()
    {
        switch (signType)
        {
            case TYPE_SIGN_ARROW:
                return getArrowPrefix(number);

            case TYPE_SIGN_MINUS_ONLY:
                return getMinusOnlyPrefix(number);

            case TYPE_SIGN_PLUS_MINUS_ALWAYS:
                return getPlusMinusPrefix(number);

            default:
                throw new IllegalArgumentException("Unhandled signType: " + signType);
        }
    }

    public static String getArrowPrefix(double value)
    {
        return Application.getResourceString(getArrowPrefixResId(value));
    }

    public static int getArrowPrefixResId(double value)
    {
        return value > 0 ? R.string.arrow_prefix_positive :
                value < 0 ? R.string.arrow_prefix_negative :
                        R.string.arrow_prefix_zero;
    }

    public static String getMinusOnlyPrefix(double value)
    {
        return Application.getResourceString(getMinusOnlyPrefixResId(value));
    }

    public static int getMinusOnlyPrefixResId(double value)
    {
        return value < 0 ? R.string.sign_prefix_negative : R.string.sign_prefix_zero;
    }

    public static String getPlusMinusPrefix(double value)
    {
        return Application.getResourceString(getPlusMinusPrefixResId(value));
    }

    public static int getPlusMinusPrefixResId(double value)
    {
        return value > 0 ? R.string.sign_prefix_positive :
                value < 0 ? R.string.sign_prefix_negative :
                        R.string.sign_prefix_zero;
    }
    //</editor-fold>
}
