package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.WebViewFragment;

/** Created with IntelliJ IDEA. User: tho Date: 11/18/13 Time: 12:09 PM Copyright (c) TradeHero */
public class SettingsActivity extends SherlockPreferenceActivity
        implements Preference.OnPreferenceClickListener
{

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settings);

        Preference settingFaq = findPreference(getString(R.string.settings_primary_faq));
        settingFaq.setOnPreferenceClickListener(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPreferenceClick(Preference preference)
    {
        if (preference.getKey().equals(getString(R.string.settings_primary_faq)))
        {
            Intent intent = preference.getIntent();
            intent.putExtra(DashboardActivity.EXTRA_FRAGMENT, WebViewFragment.class.getName());
            intent.putExtra(WebViewFragment.BUNDLE_KEY_URL, getString(R.string.th_faq_url));

            // let android do the rest
            return false;
        }
        return true;
    }
}
