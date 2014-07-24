package com.tradehero.th.fragments.location;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.market.Country;

class LocationAdapter extends ArrayDTOAdapterNew<ListedLocationDTO, LocationRelativeView>
{
    protected Country currentCountry;

    //<editor-fold desc="Constructors">
    public LocationAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    public void setCurrentCountry(Country currentCountry)
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
