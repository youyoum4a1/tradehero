package com.tradehero.th.fragments.social.hero;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import timber.log.Timber;

public class PrimiumHeroFragment extends HeroesTabContentFragment
{

    public PrimiumHeroFragment()
    {
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Timber.d("onAttach");
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override public void onStart()
    {
        super.onStart();
        Timber.d("onStart");
    }


    @Override public void onResume()
    {
        super.onResume();
        Timber.d("onResume");
    }

    @Override public void onPause()
    {
        super.onPause();
        Timber.d("onPause");
    }

    @Override public void onStop()
    {
        super.onStop();
        Timber.d("onStop");
    }



    @Override public void onDestroyView()
    {
        super.onDestroyView();
        Timber.d("onDestroyView");
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        Timber.d("onDestroy");
    }

    @Override public void onDetach()
    {
        super.onDetach();
        Timber.d("onDetach");
    }
}