package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterSpinnerIconAdapterNew
        extends ArrayDTOAdapterNew<ExchangeCompactSpinnerDTO, TrendingFilterSpinnerItemView>
{
    private int dropDownResId;

    //<editor-fold desc="Constructors">
    public TrendingFilterSpinnerIconAdapterNew(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    @Override public void setDropDownViewResource(int resource)
    {
        super.setDropDownViewResource(resource);
        this.dropDownResId = resource;
    }

    @Override public TrendingFilterSpinnerItemView getDropDownView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(getContext(), dropDownResId, null);
        }
        TrendingFilterSpinnerItemView dtoView = (TrendingFilterSpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
