package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import android.widget.Button;
import com.tradehero.th.utils.Constants;

/** Created with IntelliJ IDEA. User: nia Date: 18/10/13 Time: 5:21 PM To change this template use File | Settings | File Templates. */
public class AboutFragment extends DashboardFragment
{
    public static final String TAG = AboutFragment.class.getSimpleName();

    @InjectView(R.id.settings_about_version) protected TextView versionHolder;
    @InjectView(R.id.settings_privacy) protected Button privacyButton;
    @InjectView(R.id.settings_terms) protected Button termsButton;

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.settings_about_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, view);

        privacyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pushUrl(R.string.th_privacy_terms_url);
            }
        });

        termsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pushUrl(R.string.th_terms_url);
            }
        });

        versionHolder.setText(getString(R.string.settings_version_holder, Constants.TH_CLIENT_VERSION_VALUE));

        return view;
    }

    @Override public void onDestroyView()
    {
        if (privacyButton != null)
        {
            privacyButton.setOnClickListener(null);
        }
        privacyButton = null;

        if (termsButton != null)
        {
            termsButton.setOnClickListener(null);
        }
        termsButton = null;
        super.onDestroyView();
    }

    protected void pushUrl(int urlResId)
    {
        String url = getResources().getString(urlResId);
        DashboardNavigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.BUNDLE_KEY_URL, url);
        navigator.pushFragment(WebViewFragment.class, bundle);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
