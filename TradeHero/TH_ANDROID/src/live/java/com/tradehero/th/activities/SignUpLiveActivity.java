package com.tradehero.th.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.tradehero.th.fragments.live.LiveSignUpMainFragment;

public class SignUpLiveActivity extends OneFragmentActivity
{
    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return LiveSignUpMainFragment.class;
    }
}