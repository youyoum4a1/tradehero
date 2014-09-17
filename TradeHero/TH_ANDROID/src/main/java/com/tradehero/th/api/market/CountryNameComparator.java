package com.tradehero.th.api.market;

import android.content.Context;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

public class CountryNameComparator implements Comparator<Country>
{
    @NotNull Context context;

    //<editor-fold desc="Constructors">
    public CountryNameComparator(@NotNull Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    @Override public int compare(@NotNull Country left, @NotNull Country right)
    {
        return context.getString(left.locationName).compareTo(context.getString(right.locationName));
    }
}
