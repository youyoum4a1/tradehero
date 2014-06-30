package com.tradehero.common.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.thm.R;
import java.util.List;
import timber.log.Timber;

public class SpinnerIconAdapter extends ArrayAdapter<CharSequence>
{
    private final int textViewResourceId;
    private final int iconViewResourceId;
    private final int iconDropDownResourceId;
    private int[] icons;
    private int[] dropDownIcons;

    //<editor-fold desc="Constructors">
    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId)
    {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, List<CharSequence> objects,
            int[] icons)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
    }

    public SpinnerIconAdapter(Context context, int resource, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, CharSequence[] objects, int[] icons, int[] dropDownIcons)
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

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, List<CharSequence> objects, int[] icons, int[] dropDownIcons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }

    public SpinnerIconAdapter(Context context, int textViewResourceId, int iconViewResourceId, int iconDropDownResourceId, CharSequence[] objects, int[] icons, int[] dropDownIcons)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.iconViewResourceId = iconViewResourceId;
        this.iconDropDownResourceId = iconDropDownResourceId;
        this.icons = icons;
        this.dropDownIcons = dropDownIcons;
    }
    //</editor-fold>

    public int getIcon(int position)
    {
        if (position >= icons.length)
        {
            return R.drawable.th_logo;
        }
        return icons[position];
    }

    public int getDropDownIcon(int position)
    {
        if (position >= dropDownIcons.length)
        {
            return R.drawable.th_logo;
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
                try
                {
                    ((ImageView) imageView).setImageResource(getIcon(position));
                }
                catch (OutOfMemoryError e)
                {
                    Timber.e(e, "at position %d, text %s", position, getItem(position));
                }
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
                try
                {
                    ((ImageView) imageView).setImageResource(getDropDownIcon(position));
                }
                catch (OutOfMemoryError e)
                {
                    Timber.e(e, "at position %d, text %s", position, getItem(position));
                }
            }
        }
    }
}
