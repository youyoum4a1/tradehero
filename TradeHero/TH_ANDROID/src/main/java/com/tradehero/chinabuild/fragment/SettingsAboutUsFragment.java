package com.tradehero.chinabuild.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.Constants;

public class SettingsAboutUsFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.app_version) TextView mVersionCode;
    @InjectView(R.id.txt_term_of_service) TextView mServiceText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.settings_about_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_about_us_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (packageInfo != null)
        {
            mVersionCode.setText("全民股神 V"+packageInfo.versionName+"."+packageInfo.versionCode);
        }
        mServiceText.setOnClickListener(this);
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
            case R.id.txt_term_of_service:
                Uri uri = Uri.parse(Constants.PRIVACY_TERMS_OF_SERVICE);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                try
                {
                    startActivity(it);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    THToast.show("Unable to open url: " + uri);
                }
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
