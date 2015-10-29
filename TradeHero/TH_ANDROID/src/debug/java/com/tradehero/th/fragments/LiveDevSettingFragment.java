package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import javax.inject.Inject;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.persistence.prefs.IsLiveColorRed;
import java.lang.Override;

public class LiveDevSettingFragment extends BaseFragment
{
    @Bind(R.id.red_color_toggle) ToggleButton toggleButton;

    @Inject @IsLiveColorRed BooleanPreference isLiveColorRed;

    public LiveDevSettingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_live_dev_setting, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        toggleButton.setChecked(isLiveColorRed.get());
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isLiveColorRed.set(isChecked);
            }
        });
    }
}
