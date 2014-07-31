package com.tradehero.th.models.number;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import org.jetbrains.annotations.NotNull;

public class THSignedPercentage extends THSignedNumber
{
    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
        extends THSignedNumber.Builder<BuilderType>
    {
        @Override public THSignedPercentage build()
        {
            return new THSignedPercentage(this);
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
