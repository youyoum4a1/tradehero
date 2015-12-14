package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;

public class CallToActionFragment extends BaseFragment
{
    private BaseFragment fragment;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_live_prompt, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.later)
    public void onClickLater()
    {
        if (fragment != null) {
            if (fragment instanceof TrendingMainFragment) {
                TrendingMainFragment mainFragment = (TrendingMainFragment) fragment;
                mainFragment.handleIsLive();
            }
        }
        
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public BaseFragment getFragment()
    {
        return fragment;
    }

    public void setFragment(BaseFragment fragment)
    {
        this.fragment = fragment;
    }
}
