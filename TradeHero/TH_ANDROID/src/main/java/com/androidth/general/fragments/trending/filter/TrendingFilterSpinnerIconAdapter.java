package com.androidth.general.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.adapters.ArrayDTOAdapterNew;
import com.androidth.general.common.persistence.DTO;

public class TrendingFilterSpinnerIconAdapter
        extends ArrayDTOAdapterNew<DTO, TrendingFilterSpinnerItemView>
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
            convertView = LayoutInflater.from(getContext()).inflate(dropDownResId, parent, false);
        }
        TrendingFilterSpinnerItemView dtoView = (TrendingFilterSpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
