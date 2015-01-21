package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
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
import butterknife.OnItemClickSticky;
import com.tradehero.common.billing.BillingConstants;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.SecurityAlertKnowledge;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.widget.list.BaseListHeaderView;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
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
        alertListView.setAdapter(alertListItemAdapter);
        alertListView.addFooterView(footerView);
        alertListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        displayAlertCount();
        displayAlertCountIcon();

        btnPlanUpgrade.setOnClickListener(v -> {
            detachRequestCode();
            //noinspection unchecked
            requestCode = userInteractor.run(uiBillingRequestBuilderProvider.get()
                    .domainToPresent(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                    .build());
        });

        footerView.setOnClickListener(view1 -> handleManageSubscriptionClicked());
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
        AndroidObservable.bindFragment(this, userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(createUserProfileCacheObserver());
    }

    protected void fetchAlertCompactList()
    {
        AndroidObservable.bindFragment(
                this,
                alertCompactListCache.get(currentUserId.toUserBaseKey()))
                .subscribe(createAlertCompactDTOListObserver());
    }

    @Override protected THBasePurchaseActionInteractor.Builder createPurchaseActionInteractorBuilder()
    {
        return super.createPurchaseActionInteractorBuilder()
                .setPurchaseReportedListener(new THPurchaseReporter.OnPurchaseReportedListener()
                {
                    @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
                    {
                        displayAlertCount();
                        displayAlertCountIcon();
                    }

                    @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
                    {
                    }
                });
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

    private void handleAlertItemClicked(AlertCompactDTO alertCompactDTO)
    {
        Bundle bundle = new Bundle();
        AlertViewFragment.putAlertId(bundle, alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()));
        navigator.get().pushFragment(AlertViewFragment.class, bundle);
    }

    private void handleManageSubscriptionClicked()
    {
        detachRequestCode();
        //noinspection unchecked
        requestCode = userInteractor.run(uiBillingRequestBuilderProvider.get()
                .manageSubscriptions(true)
                .build());
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new AlertManagerFragmentUserProfileCacheObserver();
    }

    protected class AlertManagerFragmentUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.currentUserProfile = userProfileDTO;
        displayAlertCount();
        displayAlertCountIcon();
    }

    protected Observer<Pair<UserBaseKey, AlertCompactDTOList>> createAlertCompactDTOListObserver()
    {
        return new AlertManagerFragmentAlertCompactListObserver();
    }

    protected class AlertManagerFragmentAlertCompactListObserver implements Observer<Pair<UserBaseKey, AlertCompactDTOList>>
    {
        @Override public void onNext(Pair<UserBaseKey, AlertCompactDTOList> pair)
        {
            progressAnimator.setDisplayedChildByLayoutId(R.id.alerts_list);
            alertListItemAdapter.appendTail(pair.second);
            alertListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_alert);
        }
    }
}
