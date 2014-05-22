package com.tradehero.th.fragments.alert;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.SecurityAlertKnowledge;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.widget.list.BaseListHeaderView;
import dagger.Lazy;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class AlertManagerFragment extends BasePurchaseManagerFragment
{
    public static final String BUNDLE_KEY_USER_ID = AlertManagerFragment.class.getName() + ".userId";

    @InjectView(R.id.manage_alerts_count) TextView alertPlanCount;
    @InjectView(R.id.icn_manage_alert_count) ImageView alertPlanCountIcon;
    @InjectView(R.id.progress_animator) BetterViewAnimator progressAnimator;
    @InjectView(R.id.btn_upgrade_plan) ImageButton btnPlanUpgrade;
    @InjectView(R.id.alerts_list) StickyListHeadersListView alertListView;
    protected BaseListHeaderView footerView;

    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected SecurityAlertKnowledge securityAlertKnowledge;

    private Milestone.OnCompleteListener userProfileRetrievedMilestoneCompleteListener;
    private UserProfileRetrievedMilestone userProfileRetrievedMilestone;

    private AlertListItemAdapter alertListItemAdapter;
    private DTOCache.GetOrFetchTask<UserBaseKey, AlertIdList> refreshAlertCompactListCacheTask;
    private DTOCache.Listener<UserBaseKey, AlertIdList> alertCompactListCallback;
    private int currentDisplayLayoutId;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileRetrievedMilestoneCompleteListener = new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                displayAlertCount();
                displayAlertCountIcon();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                THToast.show(new THException(throwable));
            }
        };
        alertCompactListCallback = new DTOCache.Listener<UserBaseKey, AlertIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, AlertIdList value, boolean fromCache)
            {
                progressAnimator.setDisplayedChildByLayoutId(R.id.alerts_list);
                alertListItemAdapter.notifyDataSetChanged();
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show(R.string.error_fetch_alert);
            }
        };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        footerView = (BaseListHeaderView) inflater.inflate(R.layout.alert_manage_subscription_view, null);
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
            alertListView.addFooterView(footerView);
        }

        detachUserProfileMilestone();
        userProfileRetrievedMilestone =
                new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
        userProfileRetrievedMilestone.setOnCompleteListener(userProfileRetrievedMilestoneCompleteListener);
        userProfileRetrievedMilestone.launch();

        displayAlertCount();
        displayAlertCountIcon();

        btnPlanUpgrade.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                showProductDetailListForPurchase(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS);
            }
        });

        footerView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                handleManageSubscriptionClicked();
            }
        });
    }

    @Override public void onResume()
    {
        super.onResume();

        if (currentDisplayLayoutId != 0)
        {
            progressAnimator.setDisplayedChildByLayoutId(currentDisplayLayoutId);
        }

        detachAlertCompactListCacheFetchTask();
        refreshAlertCompactListCacheTask = alertCompactListCache.get().getOrFetch(
                currentUserId.toUserBaseKey(), true, alertCompactListCallback);
        refreshAlertCompactListCacheTask.execute();
    }

    @Override public void onPause()
    {
        currentDisplayLayoutId = progressAnimator.getDisplayedChildLayoutId();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileMilestone();
        detachAlertCompactListCacheFetchTask();

        if (alertListView != null)
        {
            alertListView.setOnItemClickListener(null);
        }
        alertListView = null;

        alertListItemAdapter = null;

        if (btnPlanUpgrade != null)
        {
            btnPlanUpgrade.setOnClickListener(null);
        }
        btnPlanUpgrade = null;

        if (footerView != null)
        {
            footerView.setOnClickListener(null);
        }
        footerView = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userProfileRetrievedMilestoneCompleteListener = null;
        alertCompactListCallback = null;
        super.onDestroy();
    }

    protected void detachUserProfileMilestone()
    {
        if (userProfileRetrievedMilestone != null)
        {
            userProfileRetrievedMilestone.setOnCompleteListener(null);
        }
        userProfileRetrievedMilestone = null;
    }

    protected void detachAlertCompactListCacheFetchTask()
    {
        if (refreshAlertCompactListCacheTask != null)
        {
            refreshAlertCompactListCacheTask.setListener(null);
        }
        refreshAlertCompactListCacheTask = null;
    }

    @Override public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest uiBillingRequest = super.getShowProductDetailRequest(domain);
        uiBillingRequest.startWithProgressDialog = true;
        uiBillingRequest.popIfBillingNotAvailable = true;
        uiBillingRequest.popIfProductIdentifierFetchFailed = true;
        uiBillingRequest.popIfInventoryFetchFailed = true;
        uiBillingRequest.popIfPurchaseFailed = true;
        uiBillingRequest.purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener()
        {
            @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                displayAlertCount();
                displayAlertCountIcon();
            }

            @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
            {
            }
        };
        return uiBillingRequest;
    }

    private void displayAlertCount()
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (currentUserProfile != null)
        {
            int count = currentUserProfile.getUserAlertPlansAlertCount();
            if (count == 0)
            {
                alertPlanCount.setText(R.string.stock_alerts_no_alerts);
                btnPlanUpgrade.setVisibility(View.VISIBLE);
            }
            else if (count < IABConstants.ALERT_PLAN_UNLIMITED)
            {
                alertPlanCount.setText(String.format(getString(R.string.stock_alert_count_alert_format), count));
                btnPlanUpgrade.setVisibility(View.VISIBLE);
            }
            else
            {
                alertPlanCount.setText(R.string.stock_alert_plan_unlimited);
                btnPlanUpgrade.setVisibility(View.GONE);
            }
        }
    }

    private void displayAlertCountIcon()
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (currentUserProfile != null)
        {
            int count = currentUserProfile.getUserAlertPlansAlertCount();
            alertPlanCountIcon.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            alertPlanCountIcon.setImageResource(securityAlertKnowledge.getStockAlertIcon(count));
        }
    }

    private void handleAlertItemClicked(AlertId alertId)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(AlertViewFragment.BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
        getNavigator().pushFragment(AlertViewFragment.class, bundle);
    }

    private void handleManageSubscriptionClicked()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(IABConstants.GOOGLE_PLAY_ACCOUNT_URL));
        getActivity().startActivity(intent);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
