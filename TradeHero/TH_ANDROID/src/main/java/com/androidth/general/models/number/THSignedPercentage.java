package com.androidth.general.models.number;

import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.base.THApp;

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
    protected THSignedPercentage(@NonNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    @Override protected String createPlainNumber()
    {
        return super.createPlainNumber() + THApp.context().getString(R.string.percentage_suffix);
    }
}
