package com.tradehero.th.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 11/18/13 Time: 12:09 PM Copyright (c) TradeHero */
// TODO implement preference header with PreferenceActivity --> compatible with tablet view
public class SettingsActivity extends SherlockPreferenceActivity
{

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override public void onBuildHeaders(List<Header> target)
    {
        super.onBuildHeaders(target);
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
}
