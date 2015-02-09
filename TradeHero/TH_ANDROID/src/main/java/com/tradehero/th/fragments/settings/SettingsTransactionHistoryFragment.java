package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserTransactionHistoryListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;

public class SettingsTransactionHistoryFragment extends DashboardFragment
{
    @InjectView(R.id.transaction_list) ListView transactionListView;
    private SettingsTransactionHistoryAdapter transactionListViewAdapter;

    @Inject UserTransactionHistoryListCacheRx userTransactionHistoryListCache;
    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transactionListViewAdapter = new SettingsTransactionHistoryAdapter(
                getActivity(),
                R.layout.fragment_settings_transaction_history_adapter);
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
        return inflater.inflate(R.layout.fragment_settings_transaction_history, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        transactionListView.setAdapter(transactionListViewAdapter);
        transactionListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_TransactionHistory));

        fetchTransactionList();
    }

    @Override public void onDestroyView()
    {
        transactionListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        transactionListViewAdapter.setItems(null);
        transactionListViewAdapter = null;
        super.onDestroy();
    }

    protected void fetchTransactionList()
    {
        ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_tradehero_only),
                true);

        UserTransactionHistoryListType key = new UserTransactionHistoryListType(currentUserId.toUserBaseKey());
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userTransactionHistoryListCache.get(key))
                .finallyDo(progressDialog::dismiss)
                .subscribe(new SettingsTransactionHistoryListObserver()));
    }

    protected class SettingsTransactionHistoryListObserver implements Observer<Pair<UserTransactionHistoryListType, UserTransactionHistoryDTOList>>
    {
        @Override public void onNext(
                Pair<UserTransactionHistoryListType, UserTransactionHistoryDTOList> pair)
        {
            transactionListViewAdapter.setItems(pair.second);
            transactionListViewAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show("Unable to fetch transaction history. Please try again later.");
        }
    }
}

