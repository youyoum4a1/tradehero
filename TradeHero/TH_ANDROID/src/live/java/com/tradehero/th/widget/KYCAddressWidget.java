package com.tradehero.th.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.subjects.PublishSubject;

public class KYCAddressWidget extends LinearLayout
{
    @Bind(R.id.info_address_line1) EditText txtLine1;
    @Bind(R.id.info_address_line2) EditText txtLine2;
    @Bind(R.id.info_city) EditText txtCity;
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
                        KYCAddress kycAddress;
                        kycAddress = new KYCAddress(onTextChangeLine1.text().toString(), onTextChangeLine2.text().toString(),
                                onTextChangeCity.text().toString(), onTextChangePostalCode.text().toString());

                        kycAddress.lessThanAYear = onCheckedChangeEventYear.value();

                        return kycAddress;
                    }
                })
                .throttleLast(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<KYCAddress>()
                {
                    @Override public void call(KYCAddress kycAddress)
                    {
                        checkBoxLessThanAYear.setEnabled(
                                TextUtils.isEmpty(txtLine1.getText())
                                        && TextUtils.isEmpty(txtLine2.getText())
                                        && TextUtils.isEmpty(txtCity.getText())
                                        && TextUtils.isEmpty(txtPostalCode.getText()));
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

    public void setKYCAddress(KYCAddress kycAddress)
    {
        txtLine1.setText(kycAddress.addressLine1 != null ? kycAddress.addressLine1 : "");
        txtLine2.setText(kycAddress.addressLine2 != null ? kycAddress.addressLine2 : "");
        txtCity.setText(kycAddress.city != null ? kycAddress.city : "");
        txtPostalCode.setText(kycAddress.postalCode != null ? kycAddress.postalCode : "");
        checkBoxLessThanAYear.setChecked(kycAddress.lessThanAYear);
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
