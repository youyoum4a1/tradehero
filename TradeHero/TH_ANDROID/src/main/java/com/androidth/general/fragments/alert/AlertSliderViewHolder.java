package com.androidth.general.fragments.alert;

import android.support.annotation.NonNull;
import butterknife.BindView;
import com.androidth.general.R;
import com.androidth.general.api.alert.AlertDTO;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;

public class AlertSliderViewHolder
{
    @BindView(R.id.alert_slider_target) AlertSliderView alertSliderTarget;
    @BindView(R.id.alert_slider_percentage) AlertSliderView alertSliderPercentage;

    private Status currentStatus;

    //<editor-fold desc="Constructors">
    public AlertSliderViewHolder(@NonNull Status status)
    {
        this.currentStatus = status;
    }
    //</editor-fold>

    public void setRequisite(@NonNull AlertDTO alertDTO)
    {
        alertSliderTarget.setRequisite(
                new AlertSliderView.Requisite(alertDTO.security.currencyDisplay, 0, alertDTO.security.lastPrice * 2));
        alertSliderPercentage.setRequisite(new AlertSliderView.Requisite("", -0.5f, 0.5f));

        if (!alertDTO.active)
        {
            alertSliderTarget.display(new AlertSliderView.Status(false, alertDTO.targetPrice));
            alertSliderPercentage.display(new AlertSliderView.Status(false, 0));
        }
        else if (alertDTO.priceMovement != null)
        {
            alertSliderTarget.display(new AlertSliderView.Status(false, alertDTO.targetPrice));
            alertSliderPercentage.display(new AlertSliderView.Status(true, alertDTO.priceMovement));
        }
        else
        {
            alertSliderTarget.display(new AlertSliderView.Status(true, alertDTO.targetPrice));
            alertSliderPercentage.display(new AlertSliderView.Status(false, 0));
        }
    }

    @NonNull public Observable<Status> getStatusObservable()
    {
        return Observable.combineLatest(
                alertSliderTarget.getStatusObservable(),
                alertSliderPercentage.getStatusObservable(),
                new Func2<AlertSliderView.Status, AlertSliderView.Status, Status>()
                {
                    @Override public Status call(
                            AlertSliderView.Status status,
                            AlertSliderView.Status status2)
                    {
                        return new Status(status, status2);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Status>()
                {
                    @Override public void call(Status status)
                    {
                        if (!currentStatus.targetStatus.enabled && status.targetStatus.enabled)
                        {
                            alertSliderPercentage.display(new AlertSliderView.Status(false, status.percentageStatus.sliderValue));
                        }
                        else if (!currentStatus.percentageStatus.enabled && status.percentageStatus.enabled)
                        {
                            alertSliderTarget.display(new AlertSliderView.Status(false, status.targetStatus.sliderValue));
                        }
                        currentStatus = status;
                    }
                });
    }

    public static class Status
    {
        @NonNull public final AlertSliderView.Status targetStatus;
        @NonNull public final AlertSliderView.Status percentageStatus;

        public Status(@NonNull AlertSliderView.Status targetStatus, @NonNull AlertSliderView.Status percentageStatus)
        {
            this.targetStatus = targetStatus;
            this.percentageStatus = percentageStatus;
        }
    }
}
