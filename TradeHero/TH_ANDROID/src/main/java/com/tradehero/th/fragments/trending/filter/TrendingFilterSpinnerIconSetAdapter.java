package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterSpinnerIconSetAdapter
        extends ViewDTOSetAdapter<ExchangeCompactSpinnerDTO, TrendingFilterSpinnerItemView>
{
    @LayoutRes private int resId;
    @LayoutRes private int dropDownResId;

    //<editor-fold desc="Constructors">
    public TrendingFilterSpinnerIconSetAdapter(
            @NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context);
        this.resId = layoutResourceId;
    }
    //</editor-fold>

    public void setDropDownViewResource(@LayoutRes int resource)
    {
        this.dropDownResId = resource;
    }

    @Override protected int getViewResId(int position)
    {
        return resId;
    }

    @Override public TrendingFilterSpinnerItemView getDropDownView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(dropDownResId, parent, false);
        }
        TrendingFilterSpinnerItemView dtoView = (TrendingFilterSpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
