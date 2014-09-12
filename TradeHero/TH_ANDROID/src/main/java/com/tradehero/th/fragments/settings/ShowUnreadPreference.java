package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.persistence.prefs.IsVisitedSettings;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-9-11.
 */
public class ShowUnreadPreference extends Preference
{

    @Inject @IsVisitedSettings BooleanPreference mIsVisitedSettingsPreference;

    public ShowUnreadPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShowUnreadPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowUnreadPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        DaggerUtils.inject(this);
        if (!mIsVisitedSettingsPreference.get())
        {
            ImageView icon = (ImageView)view.findViewById(android.R.id.icon);
            if (icon != null)
            {
                icon.setBackgroundResource(R.drawable.refer_friend);
                icon.setImageResource(R.drawable.red_circle);
                icon.setPadding(80, 0, 0, 80);
            }
        }
    }

}
