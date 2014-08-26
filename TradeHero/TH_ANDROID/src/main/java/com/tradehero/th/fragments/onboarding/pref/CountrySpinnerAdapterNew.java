package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.market.Country;

public class CountrySpinnerAdapterNew
        extends ArrayDTOAdapterNew<Country, CountrySpinnerItemView>
{
    private int dropDownResId;

    //<editor-fold desc="Constructors">
    public CountrySpinnerAdapterNew(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    @Override public void setDropDownViewResource(int resource)
    {
        super.setDropDownViewResource(resource);
        this.dropDownResId = resource;
    }

    @Override public CountrySpinnerItemView getDropDownView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(getContext(), dropDownResId, null);
        }
        CountrySpinnerItemView dtoView = (CountrySpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
