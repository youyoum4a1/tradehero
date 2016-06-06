package com.androidth.general.models.number;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class THSignedFXRate extends THSignedNumber
{
    private static final int DECIMAL_PLACES_TO_BE_ENHANCED = 3;
    private static final int DECIMAL_PLACES_TO_BE_SKIPPED = 1;

    @Nullable private Integer enhancedSize;
    @Nullable @ColorRes private Integer enhancedLastDigitsColor;
    @Nullable private Integer minPrecision;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
            extends THSignedNumber.Builder<BuilderType>
    {
        @Nullable @ColorRes private Integer colorResId;
        private int minPrecision;
        private int enhancedSize;

        //<editor-fold desc="Constructors">
        protected Builder(double value)
        {
            super(value);
        }
        //</editor-fold>

        public BuilderType enhanceTo(int fontSize)
        {
            this.enhancedSize = fontSize;
            return self();
        }

        public BuilderType enhanceWithColor(@ColorRes int colorResId)
        {
            if(colorResId > 0)
            {
                this.colorResId = colorResId;
            }
            return self();
        }

        public BuilderType expectedPrecision(int precision)
        {
            this.minPrecision = precision;
            return self();
        }

        @Override public THSignedFXRate build()
        {
            return new THSignedFXRate(this);
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
    protected THSignedFXRate(@NonNull Builder<?> builder)
    {
        super(builder);
        if (builder.colorResId != null)
        {
            this.enhancedLastDigitsColor = builder.colorResId;
        }
        if (builder.enhancedSize > 0)
        {
            this.enhancedSize = builder.enhancedSize;
        }
        if (builder.minPrecision > 0)
        {
            this.minPrecision = builder.minPrecision;
        }
    }
    //</editor-fold>

    @Override @NonNull protected Spanned getSpannedValue()
    {
        SpannableStringBuilder spannedValue = (SpannableStringBuilder) super.getSpannedValue();
        int length = spannedValue.length();
        if (enhancedSize != null && enhancedSize > 0)
        {
            spannedValue.setSpan(new AbsoluteSizeSpan(enhancedSize),
                    length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length - DECIMAL_PLACES_TO_BE_SKIPPED,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannedValue.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length - DECIMAL_PLACES_TO_BE_SKIPPED,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (enhancedLastDigitsColor != null)
        {
            spannedValue.setSpan(new ForegroundColorSpan(getColor(enhancedLastDigitsColor)),
                    length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannedValue;
    }

    @Override protected String createPlainNumber()
    {
        String original = super.createPlainNumber();
        if (minPrecision != null)
        {
            int decimalPlace = original.indexOf(DECIMAL_SEPARATOR);
            int decimalCount = original.length() - decimalPlace - 1;
            if (decimalPlace < 0)
            {
                original += DECIMAL_SEPARATOR;
            }
            while (decimalCount < minPrecision)
            {
                original += "0";
                decimalCount++;
            }
        }
        return original;
    }
}
