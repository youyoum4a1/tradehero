package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.DeviceUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ProviderSecurityV2RxByExchangeFragment extends BaseFragment
{

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        return view;
    }
}
