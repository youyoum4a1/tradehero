package com.androidth.general.fragments.alert;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.number.THSignedPercentage;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AlertSliderView extends RelativeLayout
        implements DTOView<AlertSliderView.Status>
{
    @SuppressWarnings("FieldCanBeLocal") private final int SWITCHER_INDEX_DISABLED = 0;
    @SuppressWarnings("FieldCanBeLocal") private final int SWITCHER_INDEX_ENABLED = 1;
    private final int SLIDER_INITIAL_VALUE = 50;

    @BindView(R.id.alert_slider_toggle) Switch alertToggle;
    @BindView(R.id.value_switcher) ViewSwitcher valueSwitcher;
    @BindView(R.id.alert_value) TextView alertValue;
    @BindView(R.id.alert_seek_bar) SeekBar alertSlider;

    @NonNull public final Type type;
    private Requisite requisite;
    @NonNull Status status;
    @NonNull private BehaviorSubject<Status> statusSubject;

    //<editor-fold desc="Constructors">
    public AlertSliderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.type = getType(context, attrs);
        status = new Status(false, SLIDER_INITIAL_VALUE);
        statusSubject = BehaviorSubject.create(status);
    }

    public AlertSliderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.type = getType(context, attrs);
        status = new Status(false, SLIDER_INITIAL_VALUE);
        statusSubject = BehaviorSubject.create(status);
    }
    //</editor-fold>

    @NonNull private static Type getType(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AlertSliderView);
        Type type = Type.values()[a.getInt(R.styleable.AlertSliderView_alertSliderType, -1)];
        a.recycle();
        return type;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        alertSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                handleSlider(progress, fromUser);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
    }

    @Override protected void onDetachedFromWindow()
    {
        alertSlider.setOnSeekBarChangeListener(null);
        super.onDetachedFromWindow();
    }

    public void setRequisite(@NonNull Requisite requisite)
    {
        this.requisite = requisite;
        display(status);
    }

    @NonNull public Observable<Status> getStatusObservable()
    {
        return statusSubject.asObservable();
    }

    @Override public void display(Status status)
    {
        this.status = status;
        if (alertToggle != null)
        {
            alertToggle.setChecked(status.enabled);
        }
        if (valueSwitcher != null)
        {
            valueSwitcher.setDisplayedChild(status.enabled ? SWITCHER_INDEX_ENABLED : SWITCHER_INDEX_DISABLED);
        }
        if (alertSlider != null && requisite != null)
        {
            alertSlider.setEnabled(status.enabled);
            int sliderValue = (int) (alertSlider.getMax() * (status.sliderValue - requisite.min) / (requisite.max - requisite.min));
            alertSlider.setProgress(sliderValue);
        }
    }

    @SuppressWarnings("unused")
    @OnCheckedChanged(R.id.alert_slider_toggle)
    protected void handlePercentageCheckedChange(CompoundButton button, boolean isChecked)
    {
        this.status = new Status(isChecked, status.sliderValue);
        display(status);
        statusSubject.onNext(status);
    }

    protected void handleSlider(int progress, boolean fromUser)
    {
        double value = requisite.min + (progress * (requisite.max - requisite.min)) / alertSlider.getMax();
        this.status = new Status(status.enabled, value);

        THSignedNumber.Builder valueDisplay;
        switch (type)
        {
            case PRICE:
                valueDisplay = THSignedMoney.builder(value)
                        .withOutSign()
                        .currency(requisite.currencyDisplay)
                        .format(getResources().getString(R.string.stock_alert_target_price_change_format));
                break;

            case PERCENTAGE:
                valueDisplay = THSignedPercentage.builder(100 * value)
                        .withSign()
                        .format(getResources().getString(R.string.stock_alert_percentage_change_format));
                break;

            default:
                throw new IllegalArgumentException("Unhandled AlertSliderView.Type." + type);
        }
        valueDisplay.boldValue()
                .build().into(alertValue);

        if (fromUser)
        {
            statusSubject.onNext(status);
        }
    }

    public static class Status
    {
        public final boolean enabled;
        public final double sliderValue;

        public Status(boolean enabled, double sliderValue)
        {
            this.enabled = enabled;
            this.sliderValue = sliderValue;
        }
    }

    public enum Type
    {
        PRICE, PERCENTAGE
    }

    public static class Requisite
    {
        @NonNull public final String currencyDisplay;
        public final double min;
        public final double max;

        public Requisite(@NonNull String currencyDisplay, double min, double max)
        {
            this.currencyDisplay = currencyDisplay;
            this.min = min;
            this.max = max;
        }
    }
}
