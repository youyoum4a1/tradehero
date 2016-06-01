package com.ayondo.academy.api.market;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.Comparator;

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
