package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClickSticky;
import com.tradehero.common.billing.BillingConstants;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.SecurityAlertKnowledge;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.widget.list.BaseListHeaderView;
import dagger.Lazy;
import javax.inject.Inject;
import rx.android.app.AppObservable;
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

    @Inject protected AlertCompactListCacheRx alertCompactListCache;
    @Inject protected Lazy<UserProfileCacheRx> userProfileCache;
    @Inject protected SecurityAlertKnowledge securityAlertKnowledge;

    protected UserProfileDTO currentUserProfile;
    private AlertListItemAdapter alertListItemAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alertListItemAdapter = new AlertListItemAdapter(getActivity(), currentUserId, R.layout.alert_list_item);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        footerView = (BaseListHeaderView) inflater.inflate(R.layout.alert_manage_subscription_view, null);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        alertListView.addFooterView(footerView);
        alertListView.setAdapter(alertListItemAdapter);
        alertListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        displayAlertCount();
        displayAlertCountIcon();

        footerView.setOnClickListener(this::handleManageSubscriptionClicked);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getString(R.string.stock_alerts));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        if (alertListItemAdapter.getCount() == 0)
        {
            progressAnimator.setDisplayedChildByLayoutId(0);
        }
        else
        {
            progressAnimator.setDisplayedChildByLayoutId(R.id.alerts_list);
        }
        fetchUserProfile();
        fetchAlertCompactList();
    }

    @Override public void onDestroyView()
    {
        if (alertListView != null)
        {
            alertListView.setOnScrollListener(null);
        }
        alertListView = null;

        if (footerView != null)
        {
            footerView.setOnClickListener(null);
        }
        footerView = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alertListItemAdapter = null;
        super.onDestroy();
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(
                        pair -> linkWith(pair.second),
                        error -> THToast.show(new THException(error))
                ));
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.currentUserProfile = userProfileDTO;
        displayAlertCount();
        displayAlertCountIcon();
    }

    protected void fetchAlertCompactList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                alertCompactListCache.get(currentUserId.toUserBaseKey()))
                .subscribe(
                        pair -> linkWith(pair.second),
                        error -> THToast.show(new THException(error))
                ));
    }

    protected void linkWith(@NonNull AlertCompactDTOList alertCompactDTOs)
    {
        progressAnimator.setDisplayedChildByLayoutId(R.id.alerts_list);
        alertListItemAdapter.appendTail(alertCompactDTOs);
        alertListItemAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_upgrade_plan)
    protected void handleBtnPlanUpgradeClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        //noinspection unchecked
        onStopSubscriptions.add(userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                .subscribe(
                        result -> {
                            displayAlertCount();
                            displayAlertCountIcon();
                        },
                        error -> THToast.show(new THException((Throwable) error))
                ));
    }

    private void displayAlertCount()
    {
        if (currentUserProfile != null)
        {
            int count = currentUserProfile.getUserAlertPlansAlertCount();
            if (count == 0)
            {
                alertPlanCount.setText(R.string.stock_alerts_no_alerts);
                btnPlanUpgrade.setVisibility(View.VISIBLE);
            }
            else if (count < BillingConstants.ALERT_PLAN_UNLIMITED)
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
        if (currentUserProfile != null)
        {
            int count = currentUserProfile.getUserAlertPlansAlertCount();
            alertPlanCountIcon.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            alertPlanCountIcon.setImageResource(securityAlertKnowledge.getStockAlertIcon(count));
        }
    }

    @OnItemClickSticky(R.id.alerts_list)
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        AlertCompactDTO alertCompactDTO = (AlertCompactDTO) parent.getItemAtPosition(position);
        if (alertCompactDTO != null)
        {
            handleAlertItemClicked(alertCompactDTO);
        }
    }

    private void handleAlertItemClicked(@NonNull AlertCompactDTO alertCompactDTO)
    {
        Bundle bundle = new Bundle();
        AlertViewFragment.putAlertId(bundle, alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()));
        navigator.get().pushFragment(AlertViewFragment.class, bundle);
    }

    private void handleManageSubscriptionClicked(View view)
    {
        userInteractorRx.manageSubscriptions();
    }
}
