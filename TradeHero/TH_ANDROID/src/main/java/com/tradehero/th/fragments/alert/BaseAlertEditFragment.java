package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.NotifyingStickyScrollView;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.rx.ReplaceWith;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction1;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Notification;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;

abstract public class BaseAlertEditFragment extends DashboardFragment
{
    @InjectView(R.id.alert_scroll_view) NotifyingStickyScrollView scrollView;
    @InjectView(R.id.alert_security_profile) AlertSecurityProfile alertSecurityProfile;
    protected AlertSliderViewHolder alertSliderViewHolder;

    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Picasso picasso;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SecurityAlertCountingHelper securityAlertCountingHelper;
    @Inject protected THBillingInteractorRx userInteractorRx;

    protected AlertDTO alertDTO;
    protected AlertSliderViewHolder.Status holderStatus;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        holderStatus = new AlertSliderViewHolder.Status(
                new AlertSliderView.Status(false, 0),
                new AlertSliderView.Status(false, 0));
        alertSliderViewHolder = new AlertSliderViewHolder(holderStatus);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.alert_edit_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        ButterKnife.inject(alertSliderViewHolder, view);
        scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener.get());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.alert_edit_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_save:
                conditionalSaveAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchAlert();
    }

    @Override public void onDestroyView()
    {
        scrollView.setOnScrollChangedListener(null);
        super.onDestroyView();
    }

    protected void fetchAlert()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.alert_dialog_please_wait),
                true);
        onStopSubscriptions.add(
                AppObservable.bindFragment(
                        this,
                        getAlertObservable())
                        .doOnEach(new DismissDialogAction1<Notification<? super AlertDTO>>(progressDialog))
                        .subscribe(
                                new Action1<AlertDTO>()
                                {
                                    @Override public void call(AlertDTO alertDTO)
                                    {
                                        linkWith(alertDTO);
                                    }
                                },
                                new ToastAndLogOnErrorAction("Failed to fetch Alert")));
    }

    @NonNull abstract protected Observable<AlertDTO> getAlertObservable();

    protected void linkWith(@NonNull AlertDTO alertDTO)
    {
        if (alertDTO.security == null)
        {
            throw new IllegalArgumentException("AlertDTO should not have a null security");
        }
        this.alertDTO = alertDTO;

        alertSecurityProfile.display(new AlertSecurityProfile.DTO(getResources(), alertDTO));
        alertSliderViewHolder.setRequisite(alertDTO);
        registerSliders();
    }

    protected void registerSliders()
    {
        onStopSubscriptions.add(
                AppObservable.bindFragment(this,
                        alertSliderViewHolder.getStatusObservable())
                        .subscribe(
                                new Action1<AlertSliderViewHolder.Status>()
                                {
                                    @Override public void call(AlertSliderViewHolder.Status status)
                                    {
                                        holderStatus = status;
                                    }
                                }
                        )
        );
    }

    @Nullable protected AlertFormDTO getFormDTO()
    {
        AlertFormDTO alertFormDTO = new AlertFormDTO();
        alertFormDTO.active = holderStatus.targetStatus.enabled || holderStatus.percentageStatus.enabled;
        if (alertFormDTO.active)
        {
            alertFormDTO.securityId = alertDTO.security.id;
            alertFormDTO.targetPrice = holderStatus.targetStatus.enabled ? holderStatus.targetStatus.sliderValue : alertDTO.security.lastPrice;
            alertFormDTO.priceMovement = holderStatus.percentageStatus.enabled ? holderStatus.percentageStatus.sliderValue : null;

            if (holderStatus.targetStatus.enabled)
            {
                alertFormDTO.upOrDown = holderStatus.targetStatus.sliderValue > alertDTO.security.lastPrice;
            }
        }
        return alertFormDTO;
    }

    protected void conditionalSaveAlert()
    {
        final AlertFormDTO alertFormDTO = getFormDTO();
        if (alertFormDTO == null)
        {
            THToast.show(R.string.error_alert_insufficient_info);
        }
        else
        {
            final ProgressDialog progressDialog = ProgressDialog.show(
                    getActivity(),
                    getString(R.string.loading_loading),
                    getString(R.string.alert_dialog_please_wait),
                    true);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    securityAlertCountingHelper.getAlertSlots(currentUserId.toUserBaseKey())
                            .take(1)
                            .flatMap(new Func1<AlertSlotDTO, Observable<? extends AlertSlotDTO>>()
                            {
                                @Override public Observable<? extends AlertSlotDTO> call(AlertSlotDTO alertSlotDTO)
                                {
                                    return BaseAlertEditFragment.this.conditionalPopPurchaseRx(alertSlotDTO);
                                }
                            })
                            .flatMap(new Func1<AlertSlotDTO, Observable<? extends AlertCompactDTO>>()
                            {
                                @Override public Observable<? extends AlertCompactDTO> call(AlertSlotDTO alertSlot)
                                {
                                    return BaseAlertEditFragment.this.saveAlertRx(alertFormDTO);
                                }
                            }))
                    .doOnEach(new DismissDialogAction1<Notification<? super AlertCompactDTO>>(progressDialog))
                    .subscribe(
                            new Action1<AlertCompactDTO>()
                            {
                                @Override public void call(AlertCompactDTO t1)
                                {
                                    BaseAlertEditFragment.this.handleAlertUpdated(t1);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable t1)
                                {
                                    BaseAlertEditFragment.this.handleAlertUpdateFailed(t1);
                                }
                            }));
        }
    }

    @NonNull protected Observable<AlertSlotDTO> conditionalPopPurchaseRx(@NonNull AlertSlotDTO alertSlot)
    {
        if (alertSlot.freeAlertSlots <= 0)
        {
            //noinspection unchecked
            return userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                    .map(new ReplaceWith<>(alertSlot));
        }
        return Observable.just(alertSlot);
    }

    @NonNull protected Observable<AlertCompactDTO> saveAlertRx(@NonNull AlertFormDTO alertFormDTO)
    {
        if (alertFormDTO.active) // TODO decide whether we need to submit even when it is inactive
        {
            return saveAlertProperRx(alertFormDTO);
        }
        else
        {
            THToast.show(R.string.error_alert_save_inactive);
            return Observable.error(new IllegalArgumentException(getString(R.string.error_alert_save_inactive)));
        }
    }

    @NonNull abstract protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO);

    protected void handleAlertUpdated(@NonNull AlertCompactDTO alertCompactDTO)
    {
        navigator.get().popFragment();
    }

    protected void handleAlertUpdateFailed(@NonNull Throwable e)
    {
        THToast.show(new THException(e));
    }
}
