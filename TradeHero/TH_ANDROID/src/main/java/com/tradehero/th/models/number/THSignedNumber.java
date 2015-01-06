package com.tradehero.th.models.number;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.base.THApp;
import com.tradehero.th.utils.THColorUtils;
import java.text.DecimalFormat;

public class THSignedNumber
{
    public static final int DESIRED_RELEVANT_DIGIT_COUNT = 4;

    //<editor-fold desc="Constants">
    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;
    public static final boolean COLOR_ALL = true;
    public static final boolean NO_COLOR = false;

    public static final int USE_DEFAULT_COLOR = -1;
    //</editor-fold>

    private final boolean withSign;
    private final int signType;
    private final Double value;
    private final int relevantDigitCount;
    private Double signValue;
    private String formattedNumber;
    private Integer colorResId;

    private final boolean withColor;
    private final boolean boldSign;
    private final boolean boldValue;
    private Integer signColorResId;
    private Integer valueColorResId;
    private Spanned signSpanBuilder;
    private Spanned valueSpanBuilder;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    {
        private double value;
        private Double signValue;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_MINUS_ONLY;
        private int relevantDigitCount = DESIRED_RELEVANT_DIGIT_COUNT;
        public boolean withColor = COLOR_ALL;
        private int signColorResId = USE_DEFAULT_COLOR;
        private int valueColorResId = USE_DEFAULT_COLOR;
        private boolean boldSign;
        private boolean boldValue;

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

        public BuilderType noColor()
        {
            this.withColor = NO_COLOR;
            return self();
        }

        public BuilderType boldSign()
        {
            this.boldSign = true;
            return self();
        }

        public BuilderType boldValue()
        {
            this.boldValue = true;
            return self();
        }

        public BuilderType signColor(@ColorRes int colorResId)
        {
            this.signColorResId = colorResId;
            return self();
        }

        public BuilderType valueColor(@ColorRes int colorResId)
        {
            valueColorResId = colorResId;
            return self();
        }

        public BuilderType signValue(double value)
        {
            this.signValue = value;
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
    protected THSignedNumber(@NonNull Builder<?> builder)
    {
        this.withSign = builder.withSign;
        this.signType = builder.signType;
        this.value = builder.value;
        this.relevantDigitCount = builder.relevantDigitCount;
        this.withColor = builder.withColor;
        this.boldSign = builder.boldSign;
        this.boldValue = builder.boldValue;
        if (builder.signColorResId != USE_DEFAULT_COLOR)
        {
            this.signColorResId = builder.signColorResId;
        }
        if (builder.valueColorResId != USE_DEFAULT_COLOR)
        {
            this.valueColorResId = builder.valueColorResId;
        }
        if (builder.signValue != null)
        {
            this.signValue = builder.signValue;
        }
    }
    //</editor-fold>

    public int getColorResId()
    {
        if (colorResId == null)
        {
            colorResId = THColorUtils.getColorResourceIdForNumber(value);
        }
        return colorResId;
    }

    public int getColor()
    {
        return getColor(getColorResId());
    }

    protected int getColor(int colorResId)
    {
        return THApp.context().getResources().getColor(colorResId);
    }

    @Override public String toString()
    {
        if (formattedNumber == null)
        {
            formattedNumber = getFormatted();
        }
        return formattedNumber;
    }

    public void into(TextView textView)
    {
        if (withColor)
        {
            textView.setTextColor(getColor());
        }

        Spanned result = (Spanned) getCombinedSpan();

        textView.setText(result);
    }

    protected Spanned getSpannedSign()
    {
        if (signSpanBuilder == null)
        {
            if (signValue != null && signColorResId == null && withColor)
            {
                signColorResId = THColorUtils.getColorResourceIdForNumber(signValue);
            }
            signSpanBuilder = initSpanned(getConditionalSignPrefix(), boldSign, signColorResId);
        }
        return signSpanBuilder;
    }

    protected Spanned getSpannedValue()
    {
        if (valueSpanBuilder == null)
        {
            valueSpanBuilder = initSpanned(createPlainNumber(), boldValue, valueColorResId);
        }
        return valueSpanBuilder;
    }

    protected Spanned initSpanned(String text, boolean bold, Integer colorResId)
    {
        SpannableStringBuilder signSpanBuilder = new SpannableStringBuilder(text);
        if (bold)
        {
            signSpanBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (colorResId != null)
        {
            signSpanBuilder.setSpan(new ForegroundColorSpan(getColor(colorResId)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (withColor)
        {
            signSpanBuilder.setSpan(new ForegroundColorSpan(getColor()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return signSpanBuilder;
    }

    protected CharSequence getCombinedSpan()
    {
        return TextUtils.concat(getSpannedSign(), getSpannedValue());
    }

    protected String getFormatted()
    {
        return getCombinedSpan().toString();
    }

    protected String createPlainNumber()
    {
        int precision = getPrecisionFromNumber();

        DecimalFormat df = new DecimalFormat(getStringFormat(precision).toString());
        String formatted = df.format(Math.abs(value));
        return removeTrailingZeros(formatted);
    }

    public static String removeTrailingZeros(@NonNull String formattedNumber)
    {
        if (formattedNumber.contains("."))
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
                return getArrowPrefix(signValue != null ? signValue : value);

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
        return THApp.getResourceString(getArrowPrefixResId(value));
    }

    public static int getArrowPrefixResId(double value)
    {
        return value > 0 ? R.string.arrow_prefix_positive :
                value < 0 ? R.string.arrow_prefix_negative :
                        R.string.arrow_prefix_zero;
    }

    public static String getMinusOnlyPrefix(double value)
    {
        return THApp.getResourceString(getMinusOnlyPrefixResId(value));
    }

    public static int getMinusOnlyPrefixResId(double value)
    {
        return value < 0 ? R.string.sign_prefix_negative : R.string.sign_prefix_zero;
    }

    public static String getPlusMinusPrefix(double value)
    {
        return THApp.getResourceString(getPlusMinusPrefixResId(value));
    }

    public static int getPlusMinusPrefixResId(double value)
    {
        return value > 0 ? R.string.sign_prefix_positive :
                value < 0 ? R.string.sign_prefix_negative :
                        R.string.sign_prefix_zero;
    }
    //</editor-fold>
}
