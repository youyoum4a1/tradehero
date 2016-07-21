package com.androidth.general.fragments.alert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidth.general.common.billing.BillingConstants;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.androidth.general.R;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.system.SystemStatusKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.billing.ProductIdentifierDomain;
import com.androidth.general.billing.SecurityAlertKnowledge;
import com.androidth.general.billing.THBillingInteractorRx;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.system.SystemStatusCache;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.widget.list.BaseListHeaderView;
import java.util.List;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class AlertManagerFragment extends BaseFragment
{
    public static final String BUNDLE_KEY_USER_ID = AlertManagerFragment.class.getName() + ".userId";

    @BindView(R.id.manage_alerts_header) View planHeader;
    @BindView(R.id.manage_alerts_count) TextView alertPlanCount;
    @BindView(R.id.icn_manage_alert_count) ImageView alertPlanCountIcon;
    @BindView(R.id.progress_animator) BetterViewAnimator progressAnimator;
    @BindView(R.id.btn_upgrade_plan) ImageButton btnPlanUpgrade;
    @BindView(R.id.alerts_list) StickyListHeadersListView alertListView;
    protected BaseListHeaderView footerView;

    @Inject CurrentUserId currentUserId;
    @Inject THBillingInteractorRx userInteractorRx;
    @Inject SystemStatusCache systemStatusCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject UserProfileCacheRx userProfileCache;

    protected UserProfileDTO currentUserProfile;
    private AlertListItemAdapter alertListItemAdapter;

    private Unbinder unbinder;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alertListItemAdapter = new AlertListItemAdapter(getActivity(), currentUserId, R.layout.alert_list_item);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        footerView = (BaseListHeaderView) inflater.inflate(R.layout.alert_manage_subscription_view, null);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        alertListView.addFooterView(footerView);
        alertListView.setAdapter(alertListItemAdapter);
        alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListItemClick(parent, view, position, id);
            }
        });

        displayAlertCount();
        displayAlertCountIcon();

        footerView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                handleManageSubscriptionClicked();
            }
        });
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchSystemStatus();
        fetchUserProfile();
        fetchAlertCompactList();
    }

    @Override public void onDestroyView()
    {
        if (alertListView != null)
        {
            alertListView.setOnScrollListener(null);
            alertListView.setOnItemClickListener(null);
        }
        alertListView = null;

        if (footerView != null)
        {
            footerView.setOnClickListener(null);
        }
        footerView = null;
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alertListItemAdapter = null;
        super.onDestroy();
    }

    protected void fetchSystemStatus()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                systemStatusCache.getOne(new SystemStatusKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<SystemStatusKey, SystemStatusDTO>>()
                        {
                            @Override public void call(Pair<SystemStatusKey, SystemStatusDTO> statusPair)
                            {
                                linkWith(statusPair.second);
                            }
                        },
                        new TimberOnErrorAction1("Failed to fetch system status")));
    }

    protected void linkWith(@NonNull SystemStatusDTO status)
    {
        planHeader.setVisibility(status.alertsAreFree ? View.GONE : View.VISIBLE);
        if (status.alertsAreFree)
        {
            alertListView.removeFooterView(footerView);
        }
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new ToastOnErrorAction1()
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
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                alertCompactListCache.get(currentUserId.toUserBaseKey())
                        .subscribeOn(Schedulers.computation())
                        .flatMap(new Func1<Pair<UserBaseKey, AlertCompactDTOList>, Observable<List<AlertItemView.DTO>>>()
                        {
                            @Override public Observable<List<AlertItemView.DTO>> call(
                                    Pair<UserBaseKey, AlertCompactDTOList> alertCompactDTOListPair)
                            {
                                return Observable.from(alertCompactDTOListPair.second)
                                        .map(new Func1<AlertCompactDTO, AlertItemView.DTO>()
                                        {
                                            @Override public AlertItemView.DTO call(AlertCompactDTO alertCompactDTO)
                                            {
                                                return new AlertItemView.DTO(getResources(), alertCompactDTO);
                                            }
                                        })
                                        .toList();
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<? extends AlertItemView.DTO>>()
                        {
                            @Override public void call(List<? extends AlertItemView.DTO> pair)
                            {
                                linkWith(pair);
                            }
                        },
                        new ToastOnErrorAction1()
                ));
    }

    protected void linkWith(@NonNull List<? extends AlertItemView.DTO> alertCompactDTOs)
    {
        alertListItemAdapter.clear();
        alertListItemAdapter.appendTail(alertCompactDTOs);
        alertListItemAdapter.notifyDataSetChanged();
        if (alertListItemAdapter.getCount() == 0)
        {
            progressAnimator.setDisplayedChildByLayoutId(R.id.empty_item);
        }
        else
        {
            progressAnimator.setDisplayedChildByLayoutId(R.id.alerts_list);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_upgrade_plan)
    protected void handleBtnPlanUpgradeClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        //noinspection unchecked
        onStopSubscriptions.add(userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                .subscribe(
                        new Action1()
                        {
                            @Override public void call(Object result)
                            {
                                AlertManagerFragment.this.displayAlertCount();
                                AlertManagerFragment.this.displayAlertCountIcon();
                            }
                        },
                        new ToastOnErrorAction1()
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
            alertPlanCountIcon.setImageResource(SecurityAlertKnowledge.getStockAlertIcon(count));
        }
    }

    protected void onListItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        AlertItemView.DTO viewDTO = (AlertItemView.DTO) parent.getItemAtPosition(position);
        if (viewDTO != null)
        {
            handleAlertItemClicked(viewDTO.alertCompactDTO);
            alertListItemAdapter.notifyDataSetChanged();
        }
    }

    private void handleAlertItemClicked(@NonNull AlertCompactDTO alertCompactDTO)
    {
        AlertEditDialogFragment.newInstance(alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()))
                .show(
                        getFragmentManager(),
                        BaseAlertEditDialogFragment.class.getName());
    }

    private void handleManageSubscriptionClicked()
    {
        userInteractorRx.manageSubscriptions();
    }
}
