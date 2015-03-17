package com.tradehero.th.fragments.alert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.NotifyingStickyScrollView;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction1;
import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;

abstract public class BaseAlertEditFragmentHolder
{
    @InjectView(R.id.alert_scroll_view) NotifyingStickyScrollView scrollView;
    @InjectView(R.id.alert_security_profile) AlertSecurityProfile alertSecurityProfile;
    protected AlertSliderViewHolder alertSliderViewHolder;

    @NonNull protected Activity activity;
    @NonNull protected final Resources resources;
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final SecurityAlertCountingHelper securityAlertCountingHelper;

    protected SubscriptionList onStopSubscriptions;
    protected AlertDTO alertDTO;
    protected AlertSliderViewHolder.Status holderStatus;

    //<editor-fold desc="Constructors">
    public BaseAlertEditFragmentHolder(
            @NonNull Activity activity,
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull SecurityAlertCountingHelper securityAlertCountingHelper)
    {
        this.activity = activity;
        this.resources = resources;
        this.currentUserId = currentUserId;
        this.securityAlertCountingHelper = securityAlertCountingHelper;
        holderStatus = new AlertSliderViewHolder.Status(
                new AlertSliderView.Status(false, 0),
                new AlertSliderView.Status(false, 0));
        alertSliderViewHolder = new AlertSliderViewHolder(holderStatus);
    }
    //</editor-fold>

    public void onStart()
    {
        onStopSubscriptions = new SubscriptionList();
    }

    public void onStop()
    {
        onStopSubscriptions.unsubscribe();
    }

    protected void fetchAlert()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                activity,
                resources.getString(R.string.loading_loading),
                resources.getString(R.string.alert_dialog_please_wait),
                true);
        onStopSubscriptions.add(
                getAlertObservable()
                        .observeOn(AndroidSchedulers.mainThread())
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

        alertSecurityProfile.display(new AlertSecurityProfile.DTO(resources, alertDTO));
        alertSliderViewHolder.setRequisite(alertDTO);
        registerSliders();
    }

    protected void registerSliders()
    {
        onStopSubscriptions.add(alertSliderViewHolder.getStatusObservable()
                .subscribe(
                        new Action1<AlertSliderViewHolder.Status>()
                        {
                            @Override public void call(AlertSliderViewHolder.Status status)
                            {
                                holderStatus = status;
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to sliders")));
    }

    @NonNull public Observable<AlertCompactDTO> conditionalSaveAlert()
    {
        final AlertFormDTO alertFormDTO = getFormDTO();
        if (alertFormDTO == null)
        {
            THToast.show(R.string.error_alert_insufficient_info);
            return Observable.empty();
        }

        final ProgressDialog progressDialog = ProgressDialog.show(
                activity,
                resources.getString(R.string.loading_loading),
                resources.getString(R.string.alert_dialog_please_wait),
                true);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
        return securityAlertCountingHelper.getAlertSlotsOrPurchase(currentUserId.toUserBaseKey())
                .flatMap(new Func1<AlertSlotDTO, Observable<? extends AlertCompactDTO>>()
                {
                    @Override public Observable<? extends AlertCompactDTO> call(AlertSlotDTO alertSlot)
                    {
                        return saveAlertRx(alertFormDTO);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(new DismissDialogAction1<Notification<? super AlertCompactDTO>>(progressDialog));
    }

    @Nullable protected AlertFormDTO getFormDTO()
    {
        AlertFormDTO alertFormDTO = new AlertFormDTO();
        alertFormDTO.active = holderStatus.targetStatus.enabled || holderStatus.percentageStatus.enabled;
        alertFormDTO.securityId = alertDTO.security.id;
        alertFormDTO.targetPrice = holderStatus.targetStatus.enabled ? holderStatus.targetStatus.sliderValue : alertDTO.security.lastPrice;
        alertFormDTO.priceMovement = holderStatus.percentageStatus.enabled ? holderStatus.percentageStatus.sliderValue : null;

        if (holderStatus.targetStatus.enabled)
        {
            alertFormDTO.upOrDown = holderStatus.targetStatus.sliderValue > alertDTO.security.lastPrice;
        }
        return alertFormDTO;
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
            return Observable.error(new IllegalArgumentException(resources.getString(R.string.error_alert_save_inactive)));
        }
    }

    @NonNull abstract protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO);
}
