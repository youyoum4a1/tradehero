package com.androidth.general.models.number;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import com.androidth.general.R;
import com.androidth.general.base.THApp;
import com.androidth.general.utils.THColorUtils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.oshkimaadziig.george.androidutils.SpanFormatter;

public class THSignedNumber
{
    public static final int DESIRED_RELEVANT_DIGIT_COUNT = 4;
    public static final boolean DEFAULT_USE_000_SUFFIX = false;
    public static final boolean DEFAULT_USE_000_LONG_SUFFIX = true;

    //<editor-fold desc="Constants">
    public static final int TYPE_SIGN_ARROW = 0;
    public static final int TYPE_SIGN_PLUS_MINUS_ALWAYS = 1;
    public static final int TYPE_SIGN_MINUS_ONLY = 2;

    public static final boolean WITH_SIGN = true;
    public static final boolean WITHOUT_SIGN = false;
    public static final boolean USE_DEFAULT_COLOR = true;
    public static final boolean DO_NOT_USE_DEFAULT_COLOR = false;
    public static String DECIMAL_SEPARATOR = String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());
    //</editor-fold>

    private final boolean withSign;
    private final int signType;
    private final Double value;
    private final int relevantDigitCount;
    protected final boolean use000Suffix;
    private final boolean use000LongSuffix;
    @ColorRes private final int fallbackColorResId;
    private final boolean defaultColorForBackground;
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
        private final double value;
        @Nullable private Double signValue;
        private boolean withSign = WITH_SIGN;
        private int signType = TYPE_SIGN_MINUS_ONLY;
        private int relevantDigitCount = DESIRED_RELEVANT_DIGIT_COUNT;
        protected boolean use000Suffix = DEFAULT_USE_000_SUFFIX;
        private boolean use000LongSuffix = DEFAULT_USE_000_LONG_SUFFIX;
        public boolean useDefaultColor = DO_NOT_USE_DEFAULT_COLOR;
        @ColorRes private Integer fallbackColorResId = R.color.darker_grey;
        @Nullable @ColorRes private Integer signColorResId;
        @Nullable @ColorRes private Integer valueColorResId;
        private boolean boldSign;
        private boolean boldValue;
        @Nullable private String format;
        private boolean defaultColorForBackground;

        //<editor-fold desc="Constructors">
        protected Builder(double value)
        {
            this.value = value;
        }
        //</editor-fold>

        protected abstract BuilderType self();

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

        public BuilderType defaultColorForBackground()
        {
            this.defaultColorForBackground = true;
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

        public BuilderType with000Suffix()
        {
            this.use000Suffix = true;
            return self();
        }

        public BuilderType without000Suffix()
        {
            this.use000Suffix = false;
            return self();
        }

        public BuilderType useLongSuffix()
        {
            this.use000LongSuffix = true;
            return self();
        }

        public BuilderType useShortSuffix()
        {
            this.use000LongSuffix = false;
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
        this.use000Suffix = builder.use000Suffix;
        this.use000LongSuffix = builder.use000LongSuffix;
        this.useDefaultColor = builder.useDefaultColor;
        this.boldSign = builder.boldSign;
        this.boldValue = builder.boldValue;
        this.fallbackColorResId = builder.fallbackColorResId;
        this.defaultColorForBackground = builder.defaultColorForBackground;
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

    @ColorRes protected int getColorResId()
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

    protected int getColor(@ColorRes int colorResId)
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
        textView.setText(createSpanned());
    }

    public Spanned createSpanned()
    {
        Spanned result = (Spanned) getCombinedSpan();
        if (format != null)
        {
            result = SpanFormatter.format(format, result);
        }
        return result;
    }

    @NonNull protected Spanned getSpannedSign()
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

    @NonNull protected Spanned getSpannedValue()
    {
        if (valueSpanBuilder == null)
        {
            valueSpanBuilder = initSpanned(createPlainNumber(), boldValue, valueColorResId);
        }
        return valueSpanBuilder;
    }

    @NonNull protected Spanned initSpanned(@NonNull String text, boolean bold, @Nullable Integer colorResId)
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
            if (defaultColorForBackground)
            {
                signSpanBuilder.setSpan(new BackgroundColorSpan(getColor()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                signSpanBuilder.setSpan(new ForegroundColorSpan(getColor()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return signSpanBuilder;
    }

    @NonNull protected CharSequence getCombinedSpan()
    {
        return TextUtils.concat(getSpannedSign(), getSpannedValue());
    }

    protected String getFormatted()
    {
        return getCombinedSpan().toString();
    }

    protected String createPlainNumber()
    {
        int precision;
        DecimalFormat df;
        String formatted;
        if (use000Suffix)
        {
            Suffix000 suffix = Suffix000.from(value);
            precision = getPrecisionFromNumber(value / suffix.divisor, relevantDigitCount);
            df = new DecimalFormat(getStringFormat(precision).toString());
            formatted = df.format(Math.abs(value / suffix.divisor));
            formatted = removeTrailingZeros(formatted);
            formatted += THApp.context().getString(use000LongSuffix ? suffix.suffixResLong : suffix.suffixRes);
        }
        else
        {
            precision = getPrecisionFromNumber();
            df = new DecimalFormat(getStringFormat(precision).toString());
            formatted = df.format(Math.abs(value));
            formatted = removeTrailingZeros(formatted);
        }
        return formatted;
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
        String toAppend = "#,###";
        sb.append(toAppend);
        if (precision > 0)
        {
            sb.append(".");
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
        return THApp.context().getString(getArrowPrefixResId(value));
    }

    public static int getArrowPrefixResId(double value)
    {
        return value > 0 ? R.string.arrow_prefix_positive :
                value < 0 ? R.string.arrow_prefix_negative :
                        R.string.arrow_prefix_zero;
    }

    public static String getMinusOnlyPrefix(double value)
    {
        return THApp.context().getString(getMinusOnlyPrefixResId(value));
    }

    public static int getMinusOnlyPrefixResId(double value)
    {
        return value < 0 ? R.string.sign_prefix_negative : R.string.sign_prefix_zero;
    }

    public static String getPlusMinusPrefix(double value)
    {
        return THApp.context().getString(getPlusMinusPrefixResId(value));
    }

    public static int getPlusMinusPrefixResId(double value)
    {
        return value > 0 ? R.string.sign_prefix_positive :
                value < 0 ? R.string.sign_prefix_negative :
                        R.string.sign_prefix_zero;
    }
    //</editor-fold>
}