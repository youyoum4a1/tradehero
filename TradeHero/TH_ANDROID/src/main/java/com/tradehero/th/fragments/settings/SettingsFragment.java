package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 17/10/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment extends DashboardFragment
{
    public static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int ITEM_FAQ = 2;
    private static final int ITEM_SIGN_OUT = 2;

    @Inject UserService userService;

    private ProgressDialog progressDialog;
    private Timer signOutTimer;

    private View view;
    private ListView primaryListView;
    private SettingsListAdapter primaryListViewAdapter;
    private ListView notificationsListView;
    private SettingsListAdapter notificationsListViewAdapter;
    private ListView miscListView;
    private SettingsListAdapter miscListViewAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        setupPrimaryListView();
        setupNotificationsListView();
        setupMiscListView();
        return view;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        getSherlockActivity().getSupportActionBar().setTitle("Settings");
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    private void setupPrimaryListView()
    {
        primaryListViewAdapter = new SettingsListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.settings_list_item);
        primaryListViewAdapter.setItems(Arrays.asList(getResources().getStringArray(R.array.settings_primary_list)));

        primaryListView = (ListView) view.findViewById(R.id.settings_primary);
        primaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case ITEM_FAQ:
                        String faqUrl = getResources().getString(R.string.th_faq_url);
                        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                        Bundle bundle = new Bundle();
                        bundle.putString(News.URL, faqUrl);
                        navigator.pushFragment(WebViewFragment.class, bundle);
                        break;
                }
            }
        });
        primaryListView.setAdapter(primaryListViewAdapter);
    }

    private void setupNotificationsListView()
    {
        notificationsListViewAdapter = new SettingsListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.settings_list_item);
        notificationsListViewAdapter.setItems(Arrays.asList(getResources().getStringArray(R.array.settings_notifications_list)));

        notificationsListView = (ListView) view.findViewById(R.id.settings_notification);
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
            }
        });
        notificationsListView.setAdapter(notificationsListViewAdapter);
    }

    private void setupMiscListView()
    {
        miscListViewAdapter = new SettingsListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.settings_list_item);
        miscListViewAdapter.setItems(Arrays.asList(getResources().getStringArray(R.array.settings_misc_list)));

        miscListView = (ListView) view.findViewById(R.id.settings_misc);
        miscListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case ITEM_SIGN_OUT:
                        progressDialog = ProgressDialog.show(
                                getActivity(),
                                Application.getResourceString(R.string.please_wait),
                                Application.getResourceString(R.string.connecting_tradehero_only),
                                true);
                        userService.signOut(THUser.getAuthHeader(), new THCallback<Object>()
                        {
                            @Override
                            public void success(Object o, THResponse response)
                            {
                                THUser.clearCurrentUser();
                                ActivityHelper.presentFromActivity(getActivity(), AuthenticationActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                progressDialog.hide();
                            }

                            @Override public void failure(THException error)
                            {
                                progressDialog.setTitle("Failed to Sign Out");
                                progressDialog.setMessage("");
                                signOutTimer = new Timer();
                                signOutTimer.schedule(new TimerTask()
                                {
                                    public void run()
                                    {
                                        signOutTimer.cancel();
                                        progressDialog.hide();
                                        finish();
                                    }
                                }, 3000);
                            }
                        });
                        break;
                }
            }
        });
        miscListView.setAdapter(miscListViewAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
