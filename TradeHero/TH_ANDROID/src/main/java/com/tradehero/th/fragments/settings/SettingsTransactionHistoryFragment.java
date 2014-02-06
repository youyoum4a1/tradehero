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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 22/10/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsTransactionHistoryFragment extends DashboardFragment
{

    private View view;
    private ListView transactionListView;
    private SettingsTransactionHistoryAdapter transactionListViewAdapter;
    private ProgressDialog progressDialog;
    @Inject protected UserService userService;
    @Inject protected CurrentUserId currentUserId;

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
        view = inflater.inflate(R.layout.fragment_settings_transaction_history, container, false);
        transactionListView = (ListView)view.findViewById(R.id.transaction_list);
        transactionListViewAdapter = new SettingsTransactionHistoryAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.fragment_settings_transaction_history_adapter);
        transactionListView.setAdapter(transactionListViewAdapter);

        progressDialog = ProgressDialogUtil.show(
                getActivity(),
                R.string.please_wait,
                R.string.connecting_tradehero_only);
        userService.getUserTransactions(currentUserId.get(), new Callback<List<UserTransactionHistoryDTO>>()
        {
            @Override
            public void success(List<UserTransactionHistoryDTO> dtos, Response response)
            {
                transactionListViewAdapter.setItems(dtos);
                transactionListViewAdapter.notifyDataSetChanged();
                progressDialog.hide();
            }

            @Override
            public void failure(RetrofitError error)
            {
                THToast.show("Unable to fetch transaction history. Please try again later.");
                progressDialog.hide();
            }
        });
        return view;
    }

    @Override public void onDestroyView()
    {
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

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}

