package com.androidth.general.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.fragments.base.BaseFragment;
import javax.inject.Inject;

public class TypographyExampleFragment extends BaseFragment
{
    @SuppressWarnings("unused") @Inject Context doNotRemoveOrItFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_typography_style_list, container, false);
    }
}
