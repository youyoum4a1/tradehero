package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.market.SectorCompactDTO;

public class SectorSpinnerAdapterNew
        extends ArrayDTOAdapterNew<SectorCompactDTO, SectorSpinnerItemView>
{
    @LayoutRes private int dropDownResId;

    //<editor-fold desc="Constructors">
    public SectorSpinnerAdapterNew(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    @Override public void setDropDownViewResource(@LayoutRes int resource)
    {
        super.setDropDownViewResource(resource);
        this.dropDownResId = resource;
    }

    @Override public SectorSpinnerItemView getDropDownView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(getContext(), dropDownResId, null);
        }
        SectorSpinnerItemView dtoView = (SectorSpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
