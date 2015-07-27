package com.tradehero.th.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.KYCAddress;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func5;

public class KYCAddressWidget extends LinearLayout
{
    @Bind(R.id.info_address_line1) EditText txtLine1;
    @Bind(R.id.info_address_line2) EditText txtLine2;
    @Bind(R.id.info_city) EditText txtCity;
    @Bind(R.id.info_address_processing) View loadingView;
    @Bind(R.id.info_postal_code) EditText txtPostalCode;
    @Bind(R.id.info_pick_location) Button btnPickLocation;
    @Bind(R.id.info_clear_all) Button btnClearAll;
    @Bind(R.id.info_less_than_a_year) CheckBox checkBoxLessThanAYear;
    private Observable<KYCAddress> observable;

    public KYCAddressWidget(Context context)
    {
        this(context, null);
    }

    public KYCAddressWidget(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public KYCAddressWidget(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KYCAddressWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.address_widget_merged, this, true);
        ButterKnife.bind(this);

        if (attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KYCAddressWidget, defStyleAttr, defStyleRes);
            boolean showCheckbox = a.getBoolean(R.styleable.KYCAddressWidget_showCheckbox, true);
            a.recycle();

            checkBoxLessThanAYear.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);
        }

        observable = Observable.combineLatest(
                WidgetObservable.text(txtLine1, true),
                WidgetObservable.text(txtLine2, true),
                WidgetObservable.text(txtCity, true),
                WidgetObservable.text(txtPostalCode, true),
                WidgetObservable.input(checkBoxLessThanAYear, true),
                new Func5<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnCheckedChangeEvent, KYCAddress>()
                {
                    @Override public KYCAddress call(OnTextChangeEvent onTextChangeLine1,
                            OnTextChangeEvent onTextChangeLine2,
                            OnTextChangeEvent onTextChangeCity,
                            OnTextChangeEvent onTextChangePostalCode,
                            OnCheckedChangeEvent onCheckedChangeEventYear)
                    {
                        return new KYCAddress(onTextChangeLine1.text().toString(),
                                onTextChangeLine2.text().toString(),
                                onTextChangeCity.text().toString(),
                                onTextChangePostalCode.text().toString(),
                                onCheckedChangeEventYear.value());
                    }
                })
                .throttleLast(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<KYCAddress>()
                {
                    @Override public void call(KYCAddress kycAddress)
                    {
                        boolean enabled =
                                !(TextUtils.isEmpty(txtLine1.getText())
                                        && TextUtils.isEmpty(txtLine2.getText())
                                        && TextUtils.isEmpty(txtCity.getText())
                                        && TextUtils.isEmpty(txtPostalCode.getText()));
                        checkBoxLessThanAYear.setEnabled(enabled);
                        if (!enabled)
                        {
                            checkBoxLessThanAYear.setChecked(false);
                        }
                    }
                });
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        btnClearAll.setOnClickListener(new OnClickListener()
        {

            @Override public void onClick(View v)
            {
                txtCity.setText("");
                txtLine1.setText("");
                txtLine2.setText("");
                txtPostalCode.setText("");
            }
        });
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        btnClearAll.setOnClickListener(null);
    }

    public void setLoading(boolean isLoading)
    {
        btnPickLocation.setEnabled(!isLoading);
        loadingView.setVisibility(isLoading ? VISIBLE : GONE);
    }

    public void setKYCAddress(KYCAddress kycAddress)
    {
        replaceText(txtLine1, kycAddress.addressLine1);
        replaceText(txtLine2, kycAddress.addressLine2);
        replaceText(txtCity, kycAddress.city);
        replaceText(txtPostalCode, kycAddress.postalCode);
        checkBoxLessThanAYear.setChecked(kycAddress.lessThanAYear);
    }

    private void replaceText(EditText text, String value)
    {
        if (value != null && !value.equals(text.getText().toString()))
        {
            text.setText(value);
        }
        else if (value == null)
        {
            text.setText("");
        }
    }

    public Observable<KYCAddress> getKYCAddressObservable()
    {
        return observable;
    }

    public Observable<OnClickEvent> getPickLocationClickedObservable()
    {
        return ViewObservable.clicks(btnPickLocation);
    }
}
