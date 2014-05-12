package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.th.R;

public class TrendingFilterSpinnerIconAdapter extends SpinnerIconAdapter
{
    private int itemResId;
    private int itemDropDrownResId;

    public TrendingFilterSpinnerIconAdapter(Context context, CharSequence[] objects, Drawable[] icons, Drawable[] dropDownIcons)
    {
        super(context,
                R.layout.trending_filter_spinner_item,
                R.id.trending_filter_spinner_item_label,
                R.id.trending_filter_spinner_item_icon,
                R.id.trending_filter_spinner_item_icon,
                objects, icons, dropDownIcons);

        this.itemResId = R.layout.trending_filter_spinner_item;
    }

    @Override public void setDropDownViewResource(int resource)
    {
        super.setDropDownViewResource(resource);
        this.itemDropDrownResId = resource;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = View.inflate(getContext(), this.itemResId, null);
        updateText(convertView, position);
        updateIcon(convertView, position);
        return convertView;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        convertView = View.inflate(getContext(), itemDropDrownResId, null);
        updateText(convertView, position);
        updateDropDownIcon(convertView, position);
        return convertView;
    }
}
