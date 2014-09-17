package com.tradehero.th.fragments.chinabuild.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th2.R;

public class SettingFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.settings_score) RelativeLayout mScoreLayout;
    @InjectView(R.id.settings_logout) LinearLayout mLogoutLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mScoreLayout.setOnClickListener(this);
        mLogoutLayout.setOnClickListener(this);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.settings_logout:
                ActivityHelper.launchAuthentication(getActivity());
                THUser.clearCurrentUser();
                break;
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

}
