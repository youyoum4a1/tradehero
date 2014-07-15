package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserTransactionHistoryListCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SettingsTransactionHistoryFragment extends DashboardFragment
{
    private ListView transactionListView;
    private SettingsTransactionHistoryAdapter transactionListViewAdapter;
    private ProgressDialog progressDialog;

    @Inject UserTransactionHistoryListCache userTransactionHistoryListCache;
    @Inject CurrentUserId currentUserId;
    @Inject THLocalyticsSession localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;

    protected DTOCacheNew.Listener<UserTransactionHistoryListType, UserTransactionHistoryDTOList> transactionListCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transactionListCacheListener = createTransactionHistoryListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getResources().getString(R.string.settings_transaction_header));
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

        progressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(AnalyticsConstants.Settings_TransactionHistory);

        fetchTransactionList();
    }

    @Override public void onDestroyView()
    {
        detachTransactionListFetchTask();
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

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroy()
    {
        transactionListCacheListener = null;
        super.onDestroy();
    }

    protected void detachTransactionListFetchTask()
    {
        userTransactionHistoryListCache.unregister(transactionListCacheListener);
    }

    protected void fetchTransactionList()
    {
        detachTransactionListFetchTask();
        UserTransactionHistoryListType key = new UserTransactionHistoryListType(currentUserId.toUserBaseKey());
        userTransactionHistoryListCache.register(key, transactionListCacheListener);
        userTransactionHistoryListCache.getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<UserTransactionHistoryListType, UserTransactionHistoryDTOList> createTransactionHistoryListener()
    {
        return new SettingsTransactionHistoryListListener();
    }

    protected class SettingsTransactionHistoryListListener implements DTOCacheNew.Listener<UserTransactionHistoryListType, UserTransactionHistoryDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserTransactionHistoryListType key, @NotNull UserTransactionHistoryDTOList value)
        {
            transactionListViewAdapter.setItems(value);
            transactionListViewAdapter.notifyDataSetChanged();
            progressDialog.hide();
        }

        @Override public void onErrorThrown(@NotNull UserTransactionHistoryListType key, @NotNull Throwable error)
        {
            THToast.show("Unable to fetch transaction history. Please try again later.");
            progressDialog.hide();
        }
    }
}

