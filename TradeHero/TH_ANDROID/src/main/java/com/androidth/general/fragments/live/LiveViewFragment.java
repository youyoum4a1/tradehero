package com.androidth.general.fragments.live;

import android.os.Bundle;

import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.inject.HierarchyInjector;


public class LiveViewFragment extends BaseWebViewIntentFragment {

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);


    }

    @Override public void onStart(){
        super.onStart();
        setActionBarTitle("Register for Live account");
    }
}
