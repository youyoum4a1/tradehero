package com.androidth.general.fragments.kyc.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidth.general.fragments.kyc.LiveSignUpStep1AyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStep2AyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStep3AyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStep4AyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStep5AyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStepBaseAyondoFragment;
import com.androidth.general.fragments.kyc.LiveSignUpStepBaseFragment;
import com.androidth.general.fragments.kyc.PrevNextObservable;

import rx.Observable;

public class SignUpLiveAyondoPagerAdapter extends FragmentPagerAdapter
    implements PrevNextObservable
{
    @NonNull private final Bundle args;
    private boolean showFirstStepOnly = false;

    @NonNull private final LiveSignUpStepBaseAyondoFragment[] fragments = new LiveSignUpStepBaseAyondoFragment[] {
            new LiveSignUpStep1AyondoFragment(),
            new LiveSignUpStep2AyondoFragment(),
            new LiveSignUpStep3AyondoFragment(),
            new LiveSignUpStep4AyondoFragment(),
            new LiveSignUpStep5AyondoFragment()};


    public SignUpLiveAyondoPagerAdapter(@NonNull FragmentManager fm, @NonNull Bundle args)
    {
        super(fm);
        this.args = args;
    }

    @Override public int getCount()
    {
        if(showFirstStepOnly){
            return 1;
        }else{
            return fragments.length;
        }
    }

    @Override public CharSequence getPageTitle(int position)
    {
        return String.valueOf(position + 1);
    }

    @Override public Fragment getItem(int position)
    {
        Fragment f = fragments[position];
        f.setArguments(args);
        return f;
    }

    @NonNull @Override public Observable<Boolean> getPrevNextObservable()
    {
        if(showFirstStepOnly){
            return Observable.just(fragments[0])
                    .flatMap(LiveSignUpStepBaseFragment::getPrevNextObservabel);
        }else{
            return Observable.from(fragments)
                    .flatMap(LiveSignUpStepBaseFragment::getPrevNextObservabel);
        }
    }

    public void setShowFirstStepOnly(boolean showFirstStepOnly) {
        this.showFirstStepOnly = showFirstStepOnly;
    }
}