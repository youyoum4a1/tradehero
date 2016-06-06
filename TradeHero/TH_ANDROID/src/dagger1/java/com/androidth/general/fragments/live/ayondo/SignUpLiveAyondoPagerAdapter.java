package com.androidth.general.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidth.general.fragments.live.PrevNextObservable;

import rx.Observable;
import rx.functions.Func1;

public class SignUpLiveAyondoPagerAdapter extends FragmentPagerAdapter
    implements PrevNextObservable
{
    @NonNull private final Bundle args;
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
        return fragments.length;
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
        return Observable.from(fragments)
                .flatMap(new Func1<LiveSignUpStepBaseAyondoFragment, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(LiveSignUpStepBaseAyondoFragment fragment)
                    {
                        return fragment.getPrevNextObservabel();
                    }
                });
    }
}
