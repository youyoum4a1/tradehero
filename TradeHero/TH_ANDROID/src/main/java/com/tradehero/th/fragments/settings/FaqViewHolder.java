package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class FaqViewHolder extends OneSettingViewHolder
{
    @NonNull private final Analytics analytics;

    //<editor-fold desc="Constructors">
    @Inject public FaqViewHolder(@NonNull Analytics analytics)
    {
        this.analytics = analytics;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_faq;
    }

    @Override protected void handlePrefClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_FAQ));
            String faqUrl = preferenceFragmentCopy.getString(R.string.th_faq_url);
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, faqUrl);
            preferenceFragmentCopy.getNavigator().pushFragment(WebViewFragment.class, bundle);
        }
    }
}
