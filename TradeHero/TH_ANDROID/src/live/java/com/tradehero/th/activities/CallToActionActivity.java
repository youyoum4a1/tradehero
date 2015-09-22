package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
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
}
