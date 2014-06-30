package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.thm.R;

public class WeiboAuthAcitivity extends SherlockFragmentActivity implements View.OnClickListener
{

    //R.layout.authentication_sign_up;

    public static final String KEY_SIGN_IN = "sign";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (!intent.hasExtra(KEY_SIGN_IN))
        {
            finish();
            return;
        }
        if (intent.getBooleanExtra(KEY_SIGN_IN, false))
        {
            setContentView(R.layout.authentication_sign_in);
        }
        else
        {
            setContentView(R.layout.authentication_sign_up);
        }

        showViewForChinese();
    }

    private void showViewForChinese()
    {
        findViewById(R.id.btn_facebook_signin).setVisibility(View.GONE);
        findViewById(R.id.btn_twitter_signin).setVisibility(View.GONE);
        findViewById(R.id.btn_linkedin_signin).setVisibility(View.GONE);
        View v = findViewById(R.id.btn_weibo_signin);
        v.setVisibility(View.VISIBLE);
        v.setOnClickListener(this);

        View v2 = findViewById(R.id.btn_qq_signin);
        v2.setVisibility(View.VISIBLE);
        v2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {

    }
}
