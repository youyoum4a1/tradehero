package com.androidth.general.fragments.location;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.adapters.ArrayDTOAdapterNew;
import com.androidth.general.api.market.Country;

class LocationAdapter extends ArrayDTOAdapterNew<ListedLocationDTO, LocationRelativeView>
{
    @Nullable protected Country currentCountry;

    //<editor-fold desc="Constructors">
    public LocationAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    public void setCurrentCountry(@Nullable Country currentCountry)
    {
        this.currentCountry = currentCountry;
    }

    @Override public LocationRelativeView getView(int position, View convertView, ViewGroup viewGroup)
    {
        LocationRelativeView view = super.getView(position, convertView, viewGroup);
        view.setCurrentCountry(currentCountry);
        return view;
    }
}
