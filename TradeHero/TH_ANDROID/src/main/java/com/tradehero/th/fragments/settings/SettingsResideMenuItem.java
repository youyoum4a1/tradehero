package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.IsVisitedSettings;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsResideMenuItem extends LinearLayout
{
    @Inject @IsVisitedSettings BooleanPreference mIsVisitedSettingsPreference;
    @InjectView(R.id.unread_icon) ImageView unreadIcon;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    //<editor-fold desc="Constructors">
    public SettingsResideMenuItem(Context context)
    {
        super(context);
    }

    public SettingsResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

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
        unreadIcon.setVisibility(mIsVisitedSettingsPreference.get() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

}
