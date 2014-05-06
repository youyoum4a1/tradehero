package com.tradehero.common.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import java.util.List;


public class SpinnerIconAdapter extends ArrayAdapter<CharSequence>
{
    private int textViewResourceId;
    private int iconViewResourceId;
    private int iconDropDownResourceId;
    private Drawable[] icons;
    private Drawable[] dropDownIcons;

    //<editor-fold desc="Constructors">
    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId)
    {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, List<CharSequence> objects,
            Drawable[] icons)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, CharSequence[] objects, Drawable[] icons, Drawable[] dropDownIcons)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId)
    {
        super(context, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, List<CharSequence> objects, Drawable[] icons, Drawable[] dropDownIcons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, CharSequence[] objects, Drawable[] icons, Drawable[] dropDownIcons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }
    //</editor-fold>

    public Drawable getIcon(int position)
    {
        if (position >= icons.length)
        {
            return getContext().getResources().getDrawable(R.drawable.th_logo);
        }
        return icons[position];
    }

    public Drawable getDropDownIcon(int position)
    {
        if (position >= dropDownIcons.length)
        {
            return getContext().getResources().getDrawable(R.drawable.th_logo);
        }
        return dropDownIcons[position];
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View parentView = super.getView(position, convertView, parent);

        updateText(parentView, position);
        updateIcon(parentView, position);

        return parentView;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View parentView = super.getDropDownView(position, convertView, parent);

        updateText(parentView, position);
        updateDropDownIcon(parentView, position);

        return parentView;
    }

    protected void updateText(View container, int position)
    {
        View textView = container.findViewById(textViewResourceId);
        if (textView != null)
        {
            ((TextView) textView).setText(getItem(position));
        }
    }

    protected void updateIcon(View container, int position)
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

    protected void updateDropDownIcon(View container, int position)
    {
        if (icons != null)
        {
            View imageView = container.findViewById(iconDropDownResourceId);
            if (imageView != null)
            {
                ((ImageView) imageView).setImageDrawable(getDropDownIcon(position));
            }
        }
    }
}
