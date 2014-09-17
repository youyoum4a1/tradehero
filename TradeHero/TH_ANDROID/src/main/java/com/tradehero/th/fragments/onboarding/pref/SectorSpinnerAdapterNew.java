package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.market.SectorCompactDTO;

public class SectorSpinnerAdapterNew
        extends ArrayDTOAdapterNew<SectorCompactDTO, SectorSpinnerItemView>
{
    private int dropDownResId;

    //<editor-fold desc="Constructors">
    public SectorSpinnerAdapterNew(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    @Override public void setDropDownViewResource(int resource)
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
