package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserTransactionHistoryIdList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserTransactionHistoryCache;
import com.tradehero.th.persistence.user.UserTransactionHistoryListCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 22/10/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsTransactionHistoryFragment extends DashboardFragment
{
    private ListView transactionListView;
    private SettingsTransactionHistoryAdapter transactionListViewAdapter;
    private ProgressDialog progressDialog;

    @Inject protected UserTransactionHistoryListCache userTransactionHistoryListCache;
    @Inject protected UserTransactionHistoryCache userTransactionHistoryCache;
    protected DTOCache.GetOrFetchTask<UserTransactionHistoryListType, UserTransactionHistoryIdList> transactionHistoryListFetchTask;
    protected DTOCache.Listener<UserTransactionHistoryListType, UserTransactionHistoryIdList> transactionListCacheListener;
    @Inject protected CurrentUserId currentUserId;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transactionListCacheListener = new DTOCache.Listener<UserTransactionHistoryListType, UserTransactionHistoryIdList>()
        {
            @Override public void onDTOReceived(UserTransactionHistoryListType key, UserTransactionHistoryIdList value, boolean fromCache)
            {
                transactionListViewAdapter.setItems(userTransactionHistoryCache.get(value));
                transactionListViewAdapter.notifyDataSetChanged();
                progressDialog.hide();
            }

            @Override public void onErrorThrown(UserTransactionHistoryListType key, Throwable error)
            {
                THToast.show("Unable to fetch transaction history. Please try again later.");
                progressDialog.hide();
            }
        };
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.settings_transaction_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings_transaction_history, container, false);
        transactionListView = (ListView)view.findViewById(R.id.transaction_list);
        transactionListViewAdapter = new SettingsTransactionHistoryAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.fragment_settings_transaction_history_adapter);
        transactionListView.setAdapter(transactionListViewAdapter);

        progressDialog = ProgressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        detachTransactionFetchTask();
        transactionHistoryListFetchTask = userTransactionHistoryListCache.getOrFetch(
                new UserTransactionHistoryListType(currentUserId.toUserBaseKey()),
                transactionListCacheListener);
        transactionHistoryListFetchTask.execute();
    }

    @Override public void onDestroyView()
    {
        detachTransactionFetchTask();
        if (transactionListViewAdapter != null)
        {
            transactionListViewAdapter.setItems(null);
            transactionListViewAdapter = null;
        }

        if (transactionListView != null)
        {
            transactionListView.setAdapter(null);
            transactionListView.setOnItemClickListener(null);
            transactionListView = null;
        }

        super.onDestroyView();
    }

    protected void detachTransactionFetchTask()
    {
        if (transactionHistoryListFetchTask != null)
        {
            transactionHistoryListFetchTask.setListener(null);
        }
        transactionHistoryListFetchTask = null;
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroy()
    {
        transactionListCacheListener = null;
        super.onDestroy();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}

