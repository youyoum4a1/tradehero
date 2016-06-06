package com.androidth.general.fragments.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.fragments.social.ShareDelegateFragment;

import java.util.List;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    protected ShareDelegateFragment shareDelegateFragment;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        shareDelegateFragment = new ShareDelegateFragment(this);
        shareDelegateFragment.onCreate(savedInstanceState);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        shareDelegateFragment.onViewCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        shareDelegateFragment.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        shareDelegateFragment.onDestroy();
    }

    @NonNull protected List<SocialNetworkEnum> getEnabledSharePreferences()
    {
        return shareDelegateFragment.getEnabledSharePreferences();
    }
}
