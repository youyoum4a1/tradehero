package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class SettingsResideMenuItem extends LinearLayout
{
    @Inject UnreadSettingPreferenceHolder unreadSettingPreferenceHolder;
    @InjectView(R.id.unread_icon) ImageView unreadIcon;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItem(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        unreadIcon.setVisibility(hasUnVisitedSetting() ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean hasUnVisitedSetting()
    {
        return unreadSettingPreferenceHolder.hasUnread();
    }
}
