package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterSpinnerIconAdapter
        extends ArrayDTOAdapterNew<ExchangeCompactSpinnerDTO, TrendingFilterSpinnerItemView>
{
    @LayoutRes private int dropDownResId;

    //<editor-fold desc="Constructors">
    public TrendingFilterSpinnerIconAdapter(
            @NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    @Override public void setDropDownViewResource(@LayoutRes int resource)
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
