package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;

public class LiveAccountSettingActivity extends BaseActivity
{
    @Bind(R.id.my_toolbar) Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_account_setting);
        ButterKnife.bind(LiveAccountSettingActivity.this);
        setSupportActionBar(myToolbar);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.live_prompt_menu, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.close)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed()
    {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
