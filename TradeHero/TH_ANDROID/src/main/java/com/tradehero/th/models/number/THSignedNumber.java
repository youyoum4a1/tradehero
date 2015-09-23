package com.tradehero.th.models.number;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.utils.ColorUtils;
import java.text.DecimalFormat;
import org.jetbrains.annotations.NotNull;

public class THSignedNumber
{
    public static final int DESIRED_RELEVANT_DIGIT_COUNT = 2;

    //<editor-fold desc="Constants">
    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;
    //</editor-fold>

    private final boolean withSign;
    private final int signType;
    private final Double value;
    private final int relevantDigitCount;
    private String formattedNumber;
    private Integer colorResId;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    {
        private double value;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_MINUS_ONLY;
        private int relevantDigitCount = DESIRED_RELEVANT_DIGIT_COUNT;

        //<editor-fold desc="Constructors">
        protected Builder(double value)
        {
            this.value = value;
        }
        //</editor-fold>

        protected abstract BuilderType self();

        public BuilderType value(double number)
        {
            this.value = number;
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
        //<editor-fold desc="Constructors">
        private Builder2(double value)
        {
            super(value);
        }
        //</editor-fold>

        @Override protected Builder2 self()
        {
            return this;
        }
    }

    public static Builder<?> builder(double value)
    {
        return new Builder2(value);
    }

    //<editor-fold desc="Constructors">
    protected THSignedNumber(@NotNull Builder<?> builder)
    {
        this.withSign = builder.withSign;
        this.signType = builder.signType;
        this.value = builder.value;
        this.relevantDigitCount = builder.relevantDigitCount;
    }
    //</editor-fold>

    public int getColorResId()
    {
        if (colorResId == null)
        {
            colorResId = ColorUtils.getColorResourceIdForNumber(value);
        }
        return colorResId;
    }

    public int getColor()
    {
        return Application.context().getResources().getColor(getColorResId());
    }

    @Override public String toString()
    {
        if (formattedNumber == null)
        {
            formattedNumber = getFormatted();
        }
        return formattedNumber;
    }

    protected String getFormatted()
    {
        return String.format(
                "%s%s",
                getConditionalSignPrefix(),
                createPlainNumber());
    }

    protected String createPlainNumber()
    {
        //int precision = getPrecisionFromNumber();
        int precision = 2;
        DecimalFormat df = new DecimalFormat(getStringFormat(precision).toString());
        String formatted = df.format(Math.abs(value));

        return formatted;
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

    public static StringBuilder getStringFormat(int precision)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("##,##0");
        if (precision > 0)
        {
            sb.append('.');
            for (int i = 0; i < precision; ++i)
            {
                sb.append('0');
            }
        }
        //sb.append("#,###");
        //if (precision > 0)
        //{
        //    sb.append('.');
        //    for (int i = 0; i < precision; ++i)
        //    {
        //        sb.append('#');
        //    }
        //}
        return sb;
    }

    //<editor-fold desc="Precision">
    protected int getPrecisionFromNumber()
    {
        return getPrecisionFromNumber(value, relevantDigitCount);
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
                return getArrowPrefix(value);

            case TYPE_SIGN_MINUS_ONLY:
                return getMinusOnlyPrefix(value);

            case TYPE_SIGN_PLUS_MINUS_ALWAYS:
                return getPlusMinusPrefix(value);

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
