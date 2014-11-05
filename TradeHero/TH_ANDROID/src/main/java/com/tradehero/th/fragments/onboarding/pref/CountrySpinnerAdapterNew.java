package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.CountryNameComparator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CountrySpinnerAdapterNew
        extends ViewDTOSetAdapter<Country, CountrySpinnerItemView>
{
    @LayoutRes private int resId;
    @LayoutRes private int dropDownResId;

    //<editor-fold desc="Constructors">
    public CountrySpinnerAdapterNew(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, new CountryNameComparator(context));
        this.resId = layoutResourceId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return resId;
    }

    public void setDropDownViewResource(@LayoutRes int resource)
    {
        this.dropDownResId = resource;
    }

    @Override public CountrySpinnerItemView getDropDownView(int position, @Nullable View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(context, dropDownResId, null);
        }
        CountrySpinnerItemView dtoView = (CountrySpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
