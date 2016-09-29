package com.androidth.general.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.androidth.general.R;
import com.androidth.general.api.kyc.KYCAddress;
import com.androidth.general.api.market.Country;
import com.androidth.general.fragments.kyc.adapter.CountrySpinnerAdapter;
import com.neovisionaries.i18n.CountryCode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func5;

public class KYCAddressWidget extends LinearLayout
{
    public static final boolean DEFAULT_SHOW_CHECKBOX = true;

    @LayoutRes private static final int LAYOUT_COUNTRY = R.layout.spinner_live_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_COUNTRY_SELECTED_FLAG = R.layout.spinner_live_country_dropdown_item_selected;

    @Bind(R.id.info_address_line1) EditText txtLine1;
    @Bind(R.id.info_address_line2) EditText txtLine2;
    @Bind(R.id.info_city) EditText txtCity;
    @Bind(R.id.info_country) Spinner spinnerCountry;
    @Bind(R.id.info_address_processing) View loadingView;
    @Bind(R.id.info_postal_code) EditText txtPostalCode;
    @Bind(R.id.info_pick_location) Button btnPickLocation;
    @Bind(R.id.info_clear_all) Button btnClearAll;
    @Bind(R.id.info_less_than_a_year) CheckBox checkBoxLessThanAYear;
    private boolean showCheckbox = DEFAULT_SHOW_CHECKBOX;
    private KYCAddress mKycAddress;
    private CountrySpinnerAdapter nationalityAdapter;

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
        if (attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KYCAddressWidget, defStyleAttr, defStyleRes);
            showCheckbox = a.getBoolean(R.styleable.KYCAddressWidget_showCheckbox, DEFAULT_SHOW_CHECKBOX);
            a.recycle();
        }
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.address_widget_merged, this, true);
        ButterKnife.bind(this);
        checkBoxLessThanAYear.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);

        nationalityAdapter = new CountrySpinnerAdapter(getContext(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
        spinnerCountry.setAdapter(nationalityAdapter);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        btnClearAll.setOnClickListener(new OnClickListener()
        {

            @Override public void onClick(View v)
            {
                txtLine1.setText("");
                txtLine2.setText("");
                txtCity.setText("");
                txtPostalCode.setText("");
            }
        });
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        btnClearAll.setOnClickListener(null);
    }

    @NonNull public Observable<KYCAddress> getKYCAddressObservable()
    {
        return Observable.combineLatest(
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
                        KYCAddress address = new KYCAddress(onTextChangeLine1.text().toString(),
                                onTextChangeLine2.text().toString(),
                                onTextChangeCity.text().toString(),
                                CountryCode.SG,
                                onTextChangePostalCode.text().toString(),
                                onCheckedChangeEventYear.value());
                        return address;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .throttleLast(1, TimeUnit.SECONDS);
    }

    public void setLoading(boolean isLoading)
    {
        btnPickLocation.setEnabled(!isLoading);
        loadingView.setVisibility(isLoading ? VISIBLE : GONE);
    }

    public void setKYCAddress(KYCAddress kycAddress)
    {
        mKycAddress = kycAddress;
        replaceText(txtLine1, kycAddress.addressLine1);
        replaceText(txtLine2, kycAddress.addressLine2);
        replaceText(txtCity, kycAddress.city);
        replaceText(txtPostalCode, kycAddress.postalCode);
        setSelectedCountry(mKycAddress.country);
        checkBoxLessThanAYear.setChecked(kycAddress.lessThanAYear);
    }

    public void setCountries(@NonNull List<CountrySpinnerAdapter.DTO> countries, @Nullable CountryCode defaultCountry)
    {
        if (nationalityAdapter.getCount() > 0)
        {
            nationalityAdapter.clear();
        }
        nationalityAdapter.addAll(countries);
        spinnerCountry.setEnabled(countries.size() > 1);
        if (mKycAddress != null && mKycAddress.country != null)
        {
            defaultCountry = mKycAddress.country;
        }
        setSelectedCountry(defaultCountry);
    }

    private void setSelectedCountry(@Nullable CountryCode countryCode)
    {
        if (countryCode != null)
        {
            int position = nationalityAdapter.getPosition(new CountrySpinnerAdapter.DTO(Country.valueOf(countryCode.name())));
            if (position > 0 && position < nationalityAdapter.getCount())
            {
                spinnerCountry.setSelection(position);
            }
        }
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

    public Observable<OnClickEvent> getPickLocationClickedObservable()
    {
        return ViewObservable.clicks(btnPickLocation);
    }

    public boolean isValidated(){
        return (txtLine1.length()>0||txtLine2.length()>0)
                &&txtPostalCode.length()>0;
    }

    public boolean hasStayedLessThanAYear(){
        return checkBoxLessThanAYear.isChecked() && checkBoxLessThanAYear.getVisibility()== View.VISIBLE;
    }
}
