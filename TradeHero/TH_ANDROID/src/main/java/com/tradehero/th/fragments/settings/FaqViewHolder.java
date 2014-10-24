package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FaqViewHolder extends OneSettingViewHolder
{
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
            String faqUrl = preferenceFragmentCopy.getString(R.string.th_faq_url);
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, faqUrl);
            preferenceFragmentCopy.getNavigator().pushFragment(WebViewFragment.class, bundle);
        }
    }
}
