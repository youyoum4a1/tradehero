package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;

public class CallToActionActivity extends BaseActivity
{
    @Bind(R.id.my_toolbar) Toolbar toolbar;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_prompt);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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

    @OnClick(R.id.btn_open_account)
    public void openAccount()
    {
        startActivity(new Intent(this, IdentityPromptActivity.class));
        finish();
    }

    @OnClick(R.id.btn_connect_account)
    public void connectAccount()
    {
        startActivity(new Intent(this, ConnectAccountActivity.class));
        finish();
    }
}
