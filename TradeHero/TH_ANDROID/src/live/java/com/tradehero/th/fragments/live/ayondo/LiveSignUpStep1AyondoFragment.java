package com.tradehero.th.fragments.live.ayondo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveCountryListId;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.sms.SMSRequestFactory;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import com.tradehero.th.persistence.live.LiveCountryDTOListCache;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.GraphicUtil;
import java.util.Collections;
import java.util.Comparator;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseFragment
{
    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.phone_number) EditText phoneNumber;
    @Bind(R.id.btn_verify_phone) View buttonVerifyPhone;
    @Bind(R.id.info_nationality) Spinner spinnerNationality;
    @Bind(R.id.info_residency) Spinner spinnerResidency;

    @Inject LiveCountryDTOListCache liveCountryDTOListCache;
    @Inject SMSServiceWrapper smsServiceWrapper;

    private CountrySpinnerAdapter nationalityAdapter;
    private CountrySpinnerAdapter residencyAdapter;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_1, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ArrayAdapter stringArrayAdapter =
                new ArrayAdapter<String>(getActivity(),
                        R.layout.sign_up_dropdown_item_selected,
                        getResources().getStringArray(R.array.live_title_array))
                {
                    @Override public View getView(int position, View convertView, ViewGroup parent)
                    {
                        View v = super.getView(position, convertView, parent);
                        if (!SDKUtils.isLollipopOrHigher())
                        {
                            if (v instanceof TextView)
                            {
                                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        GraphicUtil.createStateListDrawableRes(getActivity(), R.drawable.abc_spinner_mtrl_am_alpha), null);
                            }
                        }
                        return v;
                    }
                };
        stringArrayAdapter.setDropDownViewResource(R.layout.sign_up_dropdown_item);
        title.setAdapter(stringArrayAdapter);

        nationalityAdapter = new CountrySpinnerAdapter(getActivity());
        spinnerNationality.setAdapter(nationalityAdapter);

        residencyAdapter = new CountrySpinnerAdapter(getActivity());
        spinnerResidency.setAdapter(residencyAdapter);

        // Maybe move this until we get the KYCForm, and use the KYCForm to fetch the list of country of residence.
        onDestroyViewSubscriptions.add(liveCountryDTOListCache.get(new LiveCountryListId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .map(new PairGetSecond<LiveCountryListId, LiveCountryDTOList>())
                .doOnNext(new Action1<LiveCountryDTOList>()
                {
                    @Override public void call(LiveCountryDTOList liveCountryDTOs)
                    {
                        Collections.sort(liveCountryDTOs, new Comparator<LiveCountryDTO>()
                        {
                            @Override public int compare(LiveCountryDTO lhs, LiveCountryDTO rhs)
                            {
                                return getString(lhs.country.locationName).compareToIgnoreCase(getString(rhs.country.locationName));
                            }
                        });
                    }
                })
                .subscribe(new Action1<LiveCountryDTOList>()
                {
                    @Override public void call(LiveCountryDTOList liveCountryDTOs)
                    {
                        residencyAdapter.addAll(liveCountryDTOs);
                        nationalityAdapter.addAll(liveCountryDTOs);
                    }
                }));
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onNext(@NonNull KYCForm kycForm)
    {
        // TODO
    }

    public static class CountrySpinnerAdapter extends ArrayAdapter<LiveCountryDTO>
    {

        private static final int LAYOUT_ID = R.layout.spinner_live_country_dropdown_item;
        private static final int LAYOUT_SELECTED_ID = R.layout.spinner_live_country_dropdown_item_selected;

        public CountrySpinnerAdapter(Context context)
        {
            super(context, 0);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            return getViewWithLayout(LAYOUT_SELECTED_ID, position, convertView, parent);
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getViewWithLayout(LAYOUT_ID, position, convertView, parent);
        }

        @NonNull protected View getViewWithLayout(@LayoutRes int layoutResId, int position, View convertView, ViewGroup parent)
        {
            CountryViewHolder viewHolder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                viewHolder = new CountryViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (CountryViewHolder) convertView.getTag();
            }

            LiveCountryDTO dto = getItem(position);

            viewHolder.imgCountry.setImageResource(dto.country.logoId);
            viewHolder.txtCountry.setText(dto.country.locationName);
            return convertView;
        }

        public class CountryViewHolder
        {
            @Bind(R.id.live_country_icon) ImageView imgCountry;
            @Bind(R.id.live_country_label) TextView txtCountry;

            public CountryViewHolder(View itemView)
            {
                ButterKnife.bind(this, itemView);
            }
        }
    }

    @SuppressWarnings("unused")
    @OnTextChanged(value = R.id.phone_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterPhoneChanged(Editable s)
    {
        String newNumber = s.toString();
        buttonVerifyPhone.setEnabled(!newNumber.equals("" + getKYCForm().getVerifiedMobileNumber()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_verify_phone)
    protected void onVerifyPhoneClicked(View view)
    {
        onDestroyViewSubscriptions.add(
                smsServiceWrapper.sendMessage(SMSRequestFactory.create(phoneNumber.getText().toString(), "Hello World"))
                        .subscribe(
                                new Action1<SMSSentConfirmationDTO>()
                                {
                                    @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                    {
                                        Timber.d(smsSentConfirmationDTO.toString());
                                    }
                                },
                                new ToastAndLogOnErrorAction(getString(R.string.sms_verification_send_fail), "Failed to send SMS")
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        super.call(throwable);
                                    }
                                }));
    }
}
