package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import android.widget.Button;

/** Created with IntelliJ IDEA. User: nia Date: 18/10/13 Time: 5:21 PM To change this template use File | Settings | File Templates. */
public class AboutFragment extends DashboardFragment
{
    public static final String TAG = AboutFragment.class.getSimpleName();
    private View view;
    private Button privacyButton;
    private Button termsButton;

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
        view = inflater.inflate(R.layout.fragment_about, container, false);

        privacyButton = (Button) view.findViewById(R.id.settings_privacy);
        privacyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = getResources().getString(R.string.th_privacy_terms_url);
                Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                Bundle bundle = new Bundle();
                bundle.putString(WebViewFragment.BUNDLE_KEY_URL, url);
                navigator.pushFragment(WebViewFragment.class, bundle);
            }
        });

        termsButton = (Button) view.findViewById(R.id.settings_terms);
        termsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = getResources().getString(R.string.th_terms_url);
                Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                Bundle bundle = new Bundle();
                bundle.putString(WebViewFragment.BUNDLE_KEY_URL, url);
                navigator.pushFragment(WebViewFragment.class, bundle);
            }
        });
        return view;
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
