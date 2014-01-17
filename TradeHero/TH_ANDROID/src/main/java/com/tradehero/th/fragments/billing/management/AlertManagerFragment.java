package com.tradehero.th.fragments.billing.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:04 PM To change this template use File | Settings | File Templates. */
public class AlertManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = AlertManagerFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_USER_ID = AlertManagerFragment.class.getName() + ".userId";

    private TextView planCount;
    private ImageView planCountHint;
    private ImageButton btnPlanUpgrade;
    private AlertListView alertListView;

    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private AlertListItemAdapter alertListItemAdapter;
    private DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> refreshAlertCompactListCacheTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        initViews(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.stock_alerts));

        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void initViews(View view)
    {
        planCount = (TextView) view.findViewById(R.id.manage_alerts_count);
        //planCountHint = (ImageView) view.findViewById(R.id.icn_alert_plan_hint);
        btnPlanUpgrade = (ImageButton) view.findViewById(R.id.btn_upgrade_plan);
        alertListView = (AlertListView) view.findViewById(R.id.alerts_list);

        if (alertListItemAdapter == null)
        {
            alertListItemAdapter = new AlertListItemAdapter(
                    getActivity(),
                    getActivity().getLayoutInflater(),
                    R.layout.alert_list_header,
                    R.layout.alert_list_item,
                    R.layout.alert_list_item,
                    R.layout.alert_list_header,
                    R.layout.alert_list_header
            );
        }

        if (alertListView != null)
        {
            alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    handleAlertItemClicked(view, position, id);
                }
            });
            //alertListView.setAdapter(alertListItemAdapter);
        }
    }

    @Override public void onResume()
    {
        refreshAlertCompactListCacheTask = alertCompactListCache.get().getOrFetch(
                currentUserBaseKeyHolder.getCurrentUserBaseKey(), true, alertCompactListCallback);
        refreshAlertCompactListCacheTask.execute();
        super.onResume();
    }

    @Override public void onDestroy()
    {
        alertListView.setOnItemClickListener(null);
        super.onDestroy();
    }

    private void handleAlertItemClicked(View view, int position, long id)
    {

    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    private DTOCache.Listener<UserBaseKey, AlertIdList> alertCompactListCallback = new DTOCache.Listener<UserBaseKey, AlertIdList>()
    {
        @Override public void onDTOReceived(UserBaseKey key, AlertIdList value)
        {
            alertListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_alert);
        }
    };
}
