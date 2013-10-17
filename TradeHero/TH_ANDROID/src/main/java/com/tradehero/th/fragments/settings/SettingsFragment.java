package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

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

    private void setupPrimaryListView()
    {
        primaryListViewAdapter = new SettingsListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.settings_list_item);
        primaryListViewAdapter.setItems(Arrays.asList(getResources().getStringArray(R.array.settings_primary_list)));

        primaryListView = (ListView) view.findViewById(R.id.settings_primary);
        primaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        miscListView.setAdapter(miscListViewAdapter);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
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
}
