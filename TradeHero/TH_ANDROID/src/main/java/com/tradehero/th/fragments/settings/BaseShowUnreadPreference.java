package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.tradehero.th.R;
import org.jetbrains.annotations.NotNull;

public abstract class BaseShowUnreadPreference extends Preference
    implements ShowUnreadPreference
{
    private static final int NO_ICON_RES_ID = R.drawable.default_image;

    int iconResId;

    //<editor-fold desc="Constructors">
    public BaseShowUnreadPreference(@NotNull Context context, @NotNull AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.icon});
        iconResId = a.getResourceId(0, NO_ICON_RES_ID);
        a.recycle();
    }
    //</editor-fold>

    @Override protected void onBindView(@NotNull View view)
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
