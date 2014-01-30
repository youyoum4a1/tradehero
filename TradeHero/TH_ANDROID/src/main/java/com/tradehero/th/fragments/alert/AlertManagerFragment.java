package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:04 PM To change this template use File | Settings | File Templates. */
public class AlertManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = AlertManagerFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_USER_ID = AlertManagerFragment.class.getName() + ".userId";

    @InjectView(R.id.manage_alerts_count) TextView alertPlanCount;
    //@InjectView(R.id.icn_alert_plan_hint) ImageView planCountHint;
    @InjectView(R.id.btn_upgrade_plan) ImageButton btnPlanUpgrade;
    @InjectView(R.id.alerts_list) StickyListHeadersListView alertListView;

    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    private AlertListItemAdapter alertListItemAdapter;
    private DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> refreshAlertCompactListCacheTask;
    private ProgressDialog progressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        ButterKnife.inject(this, view);
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

    @Override protected void initViews(View view)
    {
        if (alertListItemAdapter == null)
        {
            alertListItemAdapter = new AlertListItemAdapter(getActivity(), R.layout.alert_list_item);
        }

        if (alertListView != null)
        {
            alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    AlertId alertId = (AlertId) parent.getItemAtPosition(position);
                    if (alertId != null)
                    {
                        handleAlertItemClicked(alertId);
                    }
                }
            });
            alertListView.setAdapter(alertListItemAdapter);
        }

        UserProfileRetrievedMilestone userProfileRetrievedMilestone =
                new UserProfileRetrievedMilestone(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        userProfileRetrievedMilestone.setOnCompleteListener(userProfileRetrievedMilestoneCompleteListener);
        userProfileRetrievedMilestone.launch();

        displayAlertCount();
    }

    private void displayAlertCount()
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (currentUserProfile != null)
        {
            int count = currentUserProfile.getUserAlertPlansAlertCount();
            if (count == 0)
            {
                alertPlanCount.setText(R.string.no_alerts);
                btnPlanUpgrade.setVisibility(View.VISIBLE);
            }
            else if (count < Constants.ALERT_PLAN_UNLIMITED)
            {
                alertPlanCount.setText(String.format(getString(R.string.count_alert_format), count));
                btnPlanUpgrade.setVisibility(View.VISIBLE);
            }
            else
            {
                alertPlanCount.setText(R.string.alert_plan_unlimited);
                btnPlanUpgrade.setVisibility(View.GONE);
            }
        }
    }

    private void handleAlertItemClicked(AlertId alertId)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(AlertViewFragment.BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
        getNavigator().pushFragment(AlertViewFragment.class, bundle);
    }

    @Override public void onResume()
    {
        if (alertCompactListCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey()) != null)
        {
            alertListItemAdapter.notifyDataSetChanged();
        }
        else
        {
            progressDialog = ProgressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.please_wait);
        }
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

    private Milestone.OnCompleteListener userProfileRetrievedMilestoneCompleteListener = new Milestone.OnCompleteListener()
    {
        @Override public void onComplete(Milestone milestone)
        {
            displayAlertCount();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THToast.show(new THException(throwable));
        }
    };

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
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            alertListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            THToast.show(R.string.error_fetch_alert);
        }
    };
}
