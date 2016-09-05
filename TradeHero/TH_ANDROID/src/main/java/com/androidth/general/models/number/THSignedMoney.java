package com.androidth.general.models.number;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.text.TextUtils;
import com.androidth.general.utils.SecurityUtils;

public class THSignedMoney extends THSignedNumber
{
    private final String currency;
    private final boolean boldCurrency;
    @Nullable @ColorRes private Integer currencyColorResId;
    private Spanned currencySpanBuilder;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
            extends THSignedNumber.Builder<BuilderType>
    {
        @Nullable private String currency;
        @Nullable private Integer currencyColorResId;
        private boolean boldCurrency;

        //<editor-fold desc="Constructors">
        protected Builder(double value)
        {
            super(value);
        }
        //</editor-fold>

        public BuilderType currency(String currency)
        {
            this.currency = currency;
            return self();
        }

        public BuilderType boldCurrency()
        {
            this.boldCurrency = true;
            return self();
        }

        public BuilderType withCurrencyColor(@ColorRes int currencyColorResId)
        {
            if(currencyColorResId > 0)
            {
                this.currencyColorResId = currencyColorResId;
            }
            return self();
        }

        public BuilderType with000Suffix()
        {
            this.use000Suffix = true;
            return self();
        }

        @Override public THSignedMoney build()
        {
            return new THSignedMoney(this);
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
    protected THSignedMoney(@NonNull Builder<?> builder)
    {
        super(builder);
        this.boldCurrency = builder.boldCurrency;
        if (builder.currency == null)
        {
            this.currency = SecurityUtils.getDefaultCurrency();
        }
        else
        {
            this.currency = builder.currency;
        }
        if(builder.currencyColorResId != null)
        {
            this.currencyColorResId = builder.currencyColorResId;
        }
    }
    //</editor-fold>

    protected Spanned getSpannedCurrency()
    {
        if(currencySpanBuilder == null)
        {
            currencySpanBuilder = initSpanned(currency, boldCurrency, currencyColorResId);
        }
        return currencySpanBuilder;
    }

    @NonNull @Override protected CharSequence getCombinedSpan()
    {
        return TextUtils.concat(getSpannedSign(), getSpannedCurrency(), getCurrencySpace(), getSpannedValue());
    }

    protected String getCurrencySpace()
    {
        return currency == null || currency.isEmpty() ? "" : " ";
    }
}
