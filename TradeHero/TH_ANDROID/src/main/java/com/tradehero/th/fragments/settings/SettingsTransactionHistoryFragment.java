package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;

public class SettingsTransactionHistoryFragment extends DashboardFragment
{
    @InjectView(R.id.transaction_list) ListView transactionListView;
    private SettingsTransactionHistoryAdapter transactionListViewAdapter;
    private ProgressDialog progressDialog;

    @Inject UserTransactionHistoryListCacheRx userTransactionHistoryListCache;
    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Nullable protected Subscription transactionListCacheSubscription;

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

        progressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_TransactionHistory));

        fetchTransactionList();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(transactionListCacheSubscription);
        transactionListCacheSubscription = null;
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
        UserTransactionHistoryListType key = new UserTransactionHistoryListType(currentUserId.toUserBaseKey());
        unsubscribe(transactionListCacheSubscription);
        transactionListCacheSubscription = AppObservable.bindFragment(
                this,
                userTransactionHistoryListCache.get(key))
                .subscribe(createTransactionHistoryObserver());
    }

    @NonNull protected Observer<Pair<UserTransactionHistoryListType, UserTransactionHistoryDTOList>> createTransactionHistoryObserver()
    {
        return new SettingsTransactionHistoryListObserver();
    }

    protected class SettingsTransactionHistoryListObserver implements Observer<Pair<UserTransactionHistoryListType, UserTransactionHistoryDTOList>>
    {
        @Override public void onNext(
                Pair<UserTransactionHistoryListType, UserTransactionHistoryDTOList> pair)
        {
            transactionListViewAdapter.setItems(pair.second);
            transactionListViewAdapter.notifyDataSetChanged();
            progressDialog.hide();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show("Unable to fetch transaction history. Please try again later.");
            progressDialog.hide();
        }
    }
}

