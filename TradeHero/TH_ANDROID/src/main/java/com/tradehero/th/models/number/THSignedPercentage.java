package com.tradehero.th.models.number;

import com.tradehero.th2.R;
import com.tradehero.th.base.Application;
import org.jetbrains.annotations.NotNull;

public class THSignedPercentage extends THSignedNumber
{
    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
        extends THSignedNumber.Builder<BuilderType>
    {
        //<editor-fold desc="Constructors">
        protected Builder(double value)
        {
            super(value);
        }
        //</editor-fold>

        @Override public THSignedPercentage build()
        {
            return new THSignedPercentage(this);
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
    protected THSignedPercentage(@NotNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    @Override protected String getFormatted()
    {
        return String.format(
                "%s%s%s",
                getConditionalSignPrefix(),
                createPlainNumber(),
                Application.getResourceString(R.string.percentage_suffix));
    }
}
