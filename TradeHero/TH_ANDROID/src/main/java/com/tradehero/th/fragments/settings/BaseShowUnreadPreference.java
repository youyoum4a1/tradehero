package com.ayondo.academy.fragments.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.ayondo.academy.R;

public abstract class BaseShowUnreadPreference extends Preference
    implements ShowUnreadPreference
{
    @DrawableRes private static final int NO_ICON_RES_ID = R.drawable.default_image;

    @DrawableRes int iconResId;

    //<editor-fold desc="Constructors">
    public BaseShowUnreadPreference(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.icon});
        iconResId = a.getResourceId(0, NO_ICON_RES_ID);
        a.recycle();
    }
    //</editor-fold>

    @Override protected void onBindView(@NonNull View view)
    {
        super.onBindView(view);
        if (!isVisited())
        {
            ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
            if (icon != null)
            {
                icon.setBackgroundResource(iconResId);
                icon.setImageResource(R.drawable.red_circle);
                icon.setPadding(80, 0, 0, 80);
            }
        }
    }

    @Override protected void onClick()
    {
        super.onClick();
        setVisited(true);
    }
}
