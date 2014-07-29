package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.preference.Preference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FaqViewHolder extends BaseSettingViewHolder
{
    @NotNull private final Analytics analytics;
    protected Preference settingFaq;

    //<editor-fold desc="Constructors">
    @Inject public FaqViewHolder(@NotNull Analytics analytics)
    {
        this.analytics = analytics;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        settingFaq = preferenceFragment.findPreference(
                preferenceFragment.getString(R.string.key_settings_primary_faq));
        settingFaq.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                handleFaqClicked();
                return true;
            }
        });
    }

    @Override public void destroyViews()
    {
        settingFaq = null;
        super.destroyViews();
    }

    private void handleFaqClicked()
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_FAQ));

        String faqUrl = preferenceFragment.getString(R.string.th_faq_url);
        Bundle bundle = new Bundle();
        WebViewFragment.putUrl(bundle, faqUrl);
        preferenceFragment.getNavigator().pushFragment(WebViewFragment.class, bundle);
    }
}
