package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;

public class ConnectAccountActivity extends BaseActivity
{
    @Bind(R.id.my_toolbar) Toolbar myToolbar;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_account);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
    }
}
