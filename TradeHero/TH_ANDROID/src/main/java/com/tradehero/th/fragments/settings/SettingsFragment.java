package com.tradehero.th.fragments.settings;

import android.net.Uri;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
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
    public static final String APP_VERSION = "1.5.2";

    private static final int ITEM_SEND_LOVE = 0;
    private static final int ITEM_SEND_FEEDBACK = 1;
    private static final int ITEM_FAQ = 2;

    private static final int ITEM_SIGN_OUT = 2;
    private static final int ITEM_ABOUT = 3;

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
                    case ITEM_SEND_LOVE: // TODO: use real TradeHero Android market URL
                        final String appName = "TradeHero";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
                        }
                        break;
                    case ITEM_SEND_FEEDBACK:
                        String appVersion = "";
                        try
                        {
                            PackageInfo pInfo = getSherlockActivity().getPackageManager().getPackageInfo(getSherlockActivity().getPackageName(), 0);
                            appVersion = pInfo.versionName;
                        }
                        catch (PackageManager.NameNotFoundException e)
                        {
                            appVersion = APP_VERSION;
                        }

                        String deviceDetails = "\n\n-----\nTradeHero " + appVersion +
                                "\n" + getDeviceName() + "" +
                                "\nAndroid Ver. " + android.os.Build.VERSION.SDK_INT +
                                "-----\n";
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@tradehero.mobi" });
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
                        startActivity(Intent.createChooser(intent, ""));
                        break;
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
                    case ITEM_ABOUT:
                        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                        Bundle bundle = new Bundle();
                        navigator.pushFragment(AboutFragment.class, bundle);
                        break;
                }
            }
        });
        miscListView.setAdapter(miscListViewAdapter);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
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
