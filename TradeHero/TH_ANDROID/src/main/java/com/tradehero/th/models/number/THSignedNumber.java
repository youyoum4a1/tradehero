package com.tradehero.th.models.number;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.text.DecimalFormatSymbols;
import org.oshkimaadziig.george.androidutils.SpanFormatter;

public class THSignedNumber
{
    public static final int DESIRED_RELEVANT_DIGIT_COUNT = 4;

    //<editor-fold desc="Constants">
    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;
    public static final boolean USE_DEFAULT_COLOR = true;
    public static final boolean DO_NOT_USE_DEFAULT_COLOR = false;
    private static String DECIMAL_SEPARATOR = String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());
    private static String THOUSAND_SEPARATOR = String.valueOf(DecimalFormatSymbols.getInstance().getGroupingSeparator());
    //</editor-fold>

    private final boolean withSign;
    private final int signType;
    private final Double value;
    private final int relevantDigitCount;
    @ColorRes private final int fallbackColorResId;
    private String formattedNumber;
    @Nullable @ColorRes private Integer colorResId;

    private final boolean useDefaultColor;
    private final boolean boldSign;
    private final boolean boldValue;
    @Nullable private Double signValue;
    @Nullable @ColorRes private Integer signColorResId;
    @Nullable @ColorRes private Integer valueColorResId;
    private Spanned signSpanBuilder;
    private Spanned valueSpanBuilder;
    @Nullable private String format;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    {
        private double value;
        @Nullable private Double signValue;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_MINUS_ONLY;
        private int relevantDigitCount = DESIRED_RELEVANT_DIGIT_COUNT;
        public boolean useDefaultColor = DO_NOT_USE_DEFAULT_COLOR;
        @ColorRes private Integer fallbackColorResId = R.color.text_primary;
        @Nullable @ColorRes private Integer signColorResId;
        @Nullable @ColorRes private Integer valueColorResId;
        private boolean boldSign;
        private boolean boldValue;
        @Nullable private String format;

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

        public BuilderType withDefaultColor()
        {
            this.useDefaultColor = USE_DEFAULT_COLOR;
            return self();
        }

        public BuilderType withFallbackColor(@ColorRes int fallbackColorResId)
        {
            this.fallbackColorResId = fallbackColorResId;
            return self().withDefaultColor();
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

        public BuilderType withSignColor(@ColorRes int colorResId)
        {
            if (colorResId > 0)
            {
                this.signColorResId = colorResId;
            }
            return self();
        }

        public BuilderType withValueColor(@ColorRes int colorResId)
        {
            if (colorResId > 0)
            {
                valueColorResId = colorResId;
            }
            return self();
        }

        public BuilderType withSignValue(double value)
        {
            this.signValue = value;
            return self();
        }

        public BuilderType relevantDigitCount(int relevantDigitCount)
        {
            this.relevantDigitCount = relevantDigitCount;
            return self();
        }

        public BuilderType format(String format)
        {
            this.format = format;
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
        this.useDefaultColor = builder.useDefaultColor;
        this.boldSign = builder.boldSign;
        this.boldValue = builder.boldValue;
        this.fallbackColorResId = builder.fallbackColorResId;
        if (builder.signColorResId != null)
        {
            this.signColorResId = builder.signColorResId;
        }
        if (builder.valueColorResId != null)
        {
            this.valueColorResId = builder.valueColorResId;
        }
        if (builder.signValue != null)
        {
            this.signValue = builder.signValue;
        }
        this.format = builder.format;
    }
    //</editor-fold>

    protected int getColorResId()
    {
        if (colorResId == null)
        {
            colorResId = THColorUtils.getColorResourceIdForNumber(value, fallbackColorResId);
        }
        return colorResId;
    }

    protected int getColor()
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

    public void into(@NonNull TextView textView)
    {
        Spanned result = (Spanned) getCombinedSpan();

        if (format != null)
        {
            result = SpanFormatter.format(format, result);
        }

        textView.setText(result);
    }

    protected Spanned getSpannedSign()
    {
        if (signSpanBuilder == null)
        {
            if (signValue != null && signColorResId == null && useDefaultColor)
            {
                signColorResId = THColorUtils.getColorResourceIdForNumber(signValue, fallbackColorResId);
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
        else if (useDefaultColor)
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
        if (formattedNumber.contains(DECIMAL_SEPARATOR))
        {
            int length = formattedNumber.length();
            do
            {
                length--;
            }
            while (length > 0 && formattedNumber.charAt(length) == '0');

            formattedNumber = formattedNumber.substring(0, length + 1);

            if (formattedNumber.endsWith(DECIMAL_SEPARATOR))
            {
                formattedNumber = formattedNumber.substring(0, length);
            }
        }
        return formattedNumber;
    }

    public static StringBuilder getStringFormat(int precision)
    {
        StringBuilder sb = new StringBuilder();
        String toAppend = "#" + THOUSAND_SEPARATOR + "###";
        sb.append(toAppend);
        if (precision > 0)
        {
            sb.append(DECIMAL_SEPARATOR);
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