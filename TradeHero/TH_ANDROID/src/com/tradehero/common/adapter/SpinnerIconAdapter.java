package com.tradehero.common.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.widget.trending.TrendingSecurityView;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/16/13 Time: 6:51 PM To change this template use File | Settings | File Templates. */
public class SpinnerIconAdapter extends ArrayAdapter<CharSequence>
{
    private int textViewResourceId;
    private int iconViewResourceId;
    private Drawable[] icons;

    //<editor-fold desc="Constructors">
    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId)
    {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, List<CharSequence> objects,
            Drawable[] icons)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.icons = icons;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, CharSequence[] objects, Drawable[] icons)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.icons = icons;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId)
    {
        super(context, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, List<CharSequence> objects, Drawable[] icons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.icons = icons;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, CharSequence[] objects, Drawable[] icons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.icons = icons;
    }
    //</editor-fold>

    public Drawable getIcon(int position)
    {
        return icons[position];
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_spinner_item, null);
        }

        updateText(convertView, position);
        updateIcon(convertView, position);

        return convertView;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_spinner_dropdown_item, null);
        }

        updateText(convertView, position);
        updateIcon(convertView, position);

        return convertView;
    }

    private void updateText(View container, int position)
    {
        View textView = container.findViewById(textViewResourceId);
        if (textView != null)
        {
            ((TextView) textView).setText(getItem(position));
        }
    }

    private void updateIcon(View container, int position)
    {
        if (icons != null)
        {
            View imageView = container.findViewById(iconViewResourceId);
            if (imageView != null)
            {
                ((ImageView) imageView).setImageDrawable(getIcon(position));
            }
        }
    }
}
