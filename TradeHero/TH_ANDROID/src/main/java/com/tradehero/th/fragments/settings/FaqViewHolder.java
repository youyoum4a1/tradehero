package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FaqViewHolder extends OneSettingViewHolder
{
    @NotNull private final Analytics analytics;

    //<editor-fold desc="Constructors">
    @Inject public FaqViewHolder(@NotNull Analytics analytics)
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
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_FAQ));

        String faqUrl = preferenceFragment.getString(R.string.th_faq_url);
        Bundle bundle = new Bundle();
        WebViewFragment.putUrl(bundle, faqUrl);
        preferenceFragment.getNavigator().pushFragment(WebViewFragment.class, bundle);
    }
}
