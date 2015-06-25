package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;

public class IdentityPromptActivity extends BaseActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_prompt);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.identity_prompt_no)
    public void onNoClicked()
    {
        startActivity(new Intent(this, SignUpLiveActivity.class));
    }

    @OnClick(R.id.identity_prompt_yes)
    public void onYesClicked()
    {
        //start scanner
    }
}
