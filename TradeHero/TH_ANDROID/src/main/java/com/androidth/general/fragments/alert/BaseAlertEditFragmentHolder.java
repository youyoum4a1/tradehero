package com.androidth.general.fragments.alert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ScrollView;
import butterknife.Bind;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertDTO;
import com.androidth.general.api.alert.AlertFormDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.models.alert.AlertSlotDTO;
import com.androidth.general.models.alert.SecurityAlertCountingHelper;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction1;
import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;

abstract public class BaseAlertEditFragmentHolder
{
    private static final long QUOTE_REFRESH_DURATION_MILLI_SECONDS = 30000;

    @Bind(R.id.alert_scroll_view) ScrollView scrollView;
    @Bind(R.id.alert_security_profile) AlertSecurityProfile alertSecurityProfile;
    protected AlertSliderViewHolder alertSliderViewHolder;

    @NonNull protected Activity activity;
    @NonNull protected final Resources resources;
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final SecurityAlertCountingHelper securityAlertCountingHelper;
    @NonNull protected final QuoteServiceWrapper quoteServiceWrapper;

    protected SubscriptionList onStopSubscriptions;
    protected AlertDTO alertDTO;
    protected AlertSliderViewHolder.Status holderStatus;

    //<editor-fold desc="Constructors">
    public BaseAlertEditFragmentHolder(
            @NonNull Activity activity,
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull SecurityAlertCountingHelper securityAlertCountingHelper,
            @NonNull QuoteServiceWrapper quoteServiceWrapper)
    {
        this.activity = activity;
        this.resources = resources;
        this.currentUserId = currentUserId;
        this.securityAlertCountingHelper = securityAlertCountingHelper;
        holderStatus = new AlertSliderViewHolder.Status(
                new AlertSliderView.Status(false, 0),
                new AlertSliderView.Status(false, 0));
        alertSliderViewHolder = new AlertSliderViewHolder(holderStatus);
        this.quoteServiceWrapper = quoteServiceWrapper;
    }
    //</editor-fold>

    public void onStart()
    {
        onStopSubscriptions = new SubscriptionList();
        fetchAlert();
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
                                new TimberAndToastOnErrorAction1("Failed to fetch Alert")));
    }

    @NonNull abstract protected Observable<AlertDTO> getAlertObservable();

    protected void linkWith(@NonNull AlertDTO alertDTO)
    {
        if (alertDTO.security == null)
        {
            throw new IllegalArgumentException("AlertDTO should not have a null security");
        }
        this.alertDTO = alertDTO;

        alertSecurityProfile.display(new AlertSecurityProfile.DTO(
                resources,
                alertDTO,
                new LiveQuoteDTO()));
        alertSliderViewHolder.setRequisite(alertDTO);
        registerSliders();
        startRefreshAnimation(alertDTO);
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
                        new TimberOnErrorAction1("Failed to listen to sliders")));
    }

    protected void startRefreshAnimation(@NonNull final AlertDTO alertDTO)
    {
        onStopSubscriptions.add(
                alertSecurityProfile.start(QUOTE_REFRESH_DURATION_MILLI_SECONDS)
                        .retry(2)
                        .subscribe(
                                new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean aBoolean)
                                    {
                                        fetchQuote(alertDTO);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to animate")
                        ));
    }

    protected void fetchQuote(@NonNull final AlertDTO alertDTO)
    {
        onStopSubscriptions.add(
                quoteServiceWrapper.getQuoteRx(alertDTO.security.getSecurityId().getSecurityIdNumber())
                        .retry(2)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<LiveQuoteDTO>()
                                {
                                    @Override public void call(LiveQuoteDTO quoteDTO)
                                    {
                                        alertSecurityProfile.display(new AlertSecurityProfile.DTO(resources, alertDTO, quoteDTO));
                                        startRefreshAnimation(alertDTO);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to fetch quote")));
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
        if (alertFormDTO.active && (alertFormDTO.upOrDown != null) && (alertFormDTO.targetPrice == 0))
        {
            THToast.show(R.string.error_alert_target_price_invalid);
            return Observable.error(new IllegalArgumentException(resources.getString(R.string.error_alert_target_price_invalid)));
        }
        return saveAlertProperRx(alertFormDTO);
    }

    @NonNull abstract protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO);
}
