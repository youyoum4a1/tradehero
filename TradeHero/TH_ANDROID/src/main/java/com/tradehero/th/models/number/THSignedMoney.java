package com.tradehero.th.models.number;

import com.tradehero.th.utils.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THSignedMoney extends THSignedNumber
{
    private final String currency;

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
            extends THSignedNumber.Builder<BuilderType>
    {
        @Nullable private String currency;

        public BuilderType currency(String currency)
        {
            this.currency = currency;
            return self();
        }

        @Override public THSignedMoney build()
        {
            return new THSignedMoney(this);
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
    protected THSignedMoney(@NotNull Builder<?> builder)
    {
        super(builder);
        if (builder.currency == null)
        {
            this.currency = SecurityUtils.getDefaultCurrency();
        }
        else
        {
            this.currency = builder.currency;
        }
    }
    //</editor-fold>

    @Override protected String getFormatted()
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
}
