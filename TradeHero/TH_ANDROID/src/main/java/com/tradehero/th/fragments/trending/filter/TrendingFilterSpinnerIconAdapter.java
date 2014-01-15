package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.th.R;

/**
 * Created by xavier on 1/7/14.
 */
public class TrendingFilterSpinnerIconAdapter extends SpinnerIconAdapter
{
    public static final String TAG = TrendingFilterSpinnerIconAdapter.class.getSimpleName();

    private int firstItemResId;
    private int itemResId;
    private int firstItemDropDrownResId;
    private int itemDropDrownResId;

    public TrendingFilterSpinnerIconAdapter(Context context, CharSequence[] objects, Drawable[] icons, Drawable[] dropDownIcons)
    {
        super(context,
                R.layout.trending_filter_spinner_item,
                R.id.trending_filter_spinner_item_label,
                R.id.trending_filter_spinner_item_icon,
                R.id.trending_filter_spinner_item_icon,
                objects, icons, dropDownIcons);

        this.firstItemResId = R.layout.trending_filter_spinner_item_first;
        this.itemResId = R.layout.trending_filter_spinner_item;
        this.firstItemDropDrownResId = R.layout.trending_filter_spinner_dropdown_item_first;
    }

    @Override public void setDropDownViewResource(int resource)
    {
        super.setDropDownViewResource(resource);
        this.itemDropDrownResId = resource;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        if (position == 0)
        {
            view = View.inflate(getContext(), this.firstItemResId, null);
        }
        else
        {
            view = View.inflate(getContext(), this.itemResId, null);
        }

        updateText(view, position);
        updateIcon(view, position);
        return view;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view;
        if (position == 0)
        {
            view = View.inflate(getContext(), firstItemDropDrownResId, null);
        }
        else
        {
            view = View.inflate(getContext(), itemDropDrownResId, null);
        }

        updateText(view, position);
        updateDropDownIcon(view, position);
        return view;
    }
}
