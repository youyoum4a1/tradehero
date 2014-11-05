package com.tradehero.th.api.market;

import android.content.Context;
import java.util.Comparator;
import android.support.annotation.NonNull;

public class CountryNameComparator implements Comparator<Country>
{
    @NonNull Context context;

    //<editor-fold desc="Constructors">
    public CountryNameComparator(@NonNull Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    @Override public int compare(@NonNull Country left, @NonNull Country right)
    {
        return context.getString(left.locationName).compareTo(context.getString(right.locationName));
    }
}
