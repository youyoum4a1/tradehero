package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    @OnClick(R.id.btn_open_account)
    public void openAccount()
    {
        startActivity(new Intent(this, IdentityPromptActivity.class));
        finish();
    }
}
