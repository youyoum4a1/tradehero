package com.tradehero.th.fragments.live.ayondo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveCountryListId;
import com.tradehero.th.fragments.live.VerifyCodeDigitView;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSRequestFactory;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import com.tradehero.th.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.tradehero.th.persistence.live.LiveCountryDTOListCache;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.GraphicUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final long DEFAULT_POLL_INTERVAL_MILLISEC = 1000;
    private static final String KEY_EXPECTED_CODE = LiveSignUpStep1AyondoFragment.class.getName() + ".expectedCode";

    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.phone_number) EditText phoneNumber;
    @Bind(R.id.btn_verify_phone) TextView buttonVerifyPhone;
    @Bind(R.id.info_nationality) Spinner spinnerNationality;
    @Bind(R.id.info_residency) Spinner spinnerResidency;

    @Inject LiveCountryDTOListCache liveCountryDTOListCache;
    @Inject SMSServiceWrapper smsServiceWrapper;

    private Random randomiser;
    private CountrySpinnerAdapter nationalityAdapter;
    private CountrySpinnerAdapter residencyAdapter;
    @Nullable private String expectedCode;
    @Nullable private BehaviorSubject<SMSSentConfirmationDTO> confirmationSubject;
    private Subscription confirmationSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.randomiser = new Random(System.nanoTime());
    }

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

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getKycAyondoFormObservable(),
                WidgetObservable.text(phoneNumber),
                new Func2<KYCAyondoForm, OnTextChangeEvent, Boolean>()
                {
                    @Override public Boolean call(KYCAyondoForm kycForm, OnTextChangeEvent onTextChangeEvent)
                    {
                        try
                        {
                            long newNumber = Long.parseLong(onTextChangeEvent.text().toString());
                            populateVerifyMobile(kycForm, newNumber);
                            kycForm.setMobileNumber(newNumber);
                            onNext(kycForm);
                        } catch (NumberFormatException e)
                        {
                            Timber.e(e, "Failed to parse to number %s", onTextChangeEvent.text().toString());
                        }
                        return true;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {

                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to listen to phone number")));

        if (savedInstanceState != null)
        {
            this.expectedCode = savedInstanceState.getString(KEY_EXPECTED_CODE, this.expectedCode);
        }
    }

    @Override public void onStart()
    {
        super.onStart();
        if (this.expectedCode != null && this.confirmationSubject != null)
        {
            final Integer phoneNumberInt = Integer.parseInt(phoneNumber.getText().toString());
            final String phoneNumberText = "+65" + phoneNumberInt;
            final VerifyCodeDigitView verifyCodeDigitView =
                    (VerifyCodeDigitView) LayoutInflater.from(getActivity()).inflate(R.layout.verify_phone_number, null);
            offerToEnterCode(
                    phoneNumberInt,
                    phoneNumberText,
                    this.expectedCode,
                    verifyCodeDigitView,
                    confirmationSubject);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (expectedCode != null)
        {
            outState.putString(KEY_EXPECTED_CODE, expectedCode);
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        confirmationSubject = null;
        unsubscribe(confirmationSubscription);
        super.onDestroy();
    }

    @Override public void onNext(@NonNull KYCForm kycForm)
    {
        super.onNext(kycForm);
        if (!(kycForm instanceof KYCAyondoForm))
        {
            Timber.e(new IllegalArgumentException(), "Should not submit a KYC of type: %s", kycForm);
        }
        // TODO
    }

    @NonNull @Override public Observable<KYCAyondoForm> getKycAyondoFormObservable()
    {
        return super.getKycAyondoFormObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<KYCAyondoForm>()
                {
                    @Override public void call(KYCAyondoForm kycAyondoForm)
                    {
                        populate(kycAyondoForm);
                    }
                });
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

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        Long mobileNumber = kycForm.getMobileNumber();
        if (phoneNumber != null && mobileNumber != null)
        {
            phoneNumber.setText(String.format("%d", mobileNumber));
        }
    }

    protected void populateVerifyMobile(@NonNull KYCAyondoForm kycForm, long typedNumber)
    {
        if (buttonVerifyPhone != null)
        {
            boolean verified = Long.valueOf(typedNumber).equals(kycForm.getVerifiedMobileNumber());
            buttonVerifyPhone.setText(verified ? R.string.verified : R.string.verify);
            buttonVerifyPhone.setEnabled(!verified);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_verify_phone)
    protected void onVerifyPhoneClicked(View view)
    {
        buttonVerifyPhone.setEnabled(false);

        final Long phoneNumberInt = Long.parseLong(phoneNumber.getText().toString());
        final String phoneNumberText = "+65" + phoneNumberInt;
        String expectedCode = String.format("%04d", Math.abs(randomiser.nextInt() % 10000));
        this.expectedCode = expectedCode;
        final VerifyCodeDigitView verifyCodeDigitView =
                (VerifyCodeDigitView) LayoutInflater.from(getActivity()).inflate(R.layout.verify_phone_number, null);

        final BehaviorSubject<SMSSentConfirmationDTO> confirmationSubject = BehaviorSubject.create();
        this.confirmationSubject = confirmationSubject;

        unsubscribe(confirmationSubscription);
        confirmationSubscription = createSendSMSObservable(phoneNumberText, expectedCode)
                .subscribe(
                        new Action1<SMSSentConfirmationDTO>()
                        {
                            @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                            {
                                confirmationSubject.onNext(smsSentConfirmationDTO);
                            }
                        },
                        new TimberOnErrorAction("Failed to get confirmation from sms"));

        offerToEnterCode(
                phoneNumberInt,
                phoneNumberText,
                expectedCode,
                verifyCodeDigitView,
                confirmationSubject);
    }

    protected void offerToEnterCode(
            final long phoneNumberInt,
            @NonNull final String phoneNumberText,
            @NonNull final String expectedCode,
            @NonNull final VerifyCodeDigitView verifyCodeDigitView,
            @NonNull Observable<SMSSentConfirmationDTO> smsSentConfirmationDTOObservable)
    {
        this.expectedCode = expectedCode;
        final BehaviorSubject<AlertDialog> verifyDialogSubject = BehaviorSubject.create();
        final Observable<VerifyCodeDigitView.UserAction> userActionObservable =
                displayVerifyDialog(verifyCodeDigitView, verifyDialogSubject).share();
        onStopSubscriptions.add(
                Observable.combineLatest(
                        verifyDialogSubject,
                        getKycAyondoFormObservable(),
                        updateVerifyView(smsSentConfirmationDTOObservable, phoneNumberText, expectedCode, verifyCodeDigitView)
                                .compose(new Observable.Transformer<SMSSentConfirmationDTO, VerifyCodeDigitView.UserAction>()
                                {
                                    @Override public Observable<VerifyCodeDigitView.UserAction> call(
                                            Observable<SMSSentConfirmationDTO> smsSentConfirmationDTOObservable)
                                    {
                                        onStopSubscriptions.add(smsSentConfirmationDTOObservable
                                                .subscribe(
                                                        new Action1<SMSSentConfirmationDTO>()
                                                        {
                                                            @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                                            {
                                                                // Nothing to do
                                                            }
                                                        },
                                                        new TimberOnErrorAction("Failed to collect SMS confirmation")));
                                        return userActionObservable;
                                    }
                                }),
                        new Func3<AlertDialog, KYCAyondoForm, VerifyCodeDigitView.UserAction, VerifyCodeDigitView.UserAction>()
                        {
                            @Override
                            public VerifyCodeDigitView.UserAction call(AlertDialog alertDialog, KYCAyondoForm kycForm,
                                    VerifyCodeDigitView.UserAction userAction)
                            {
                                if (userAction instanceof VerifyCodeDigitView.UserActionResend)
                                {
                                    onStopSubscriptions.add(createSendSMSObservable(phoneNumberText, expectedCode)
                                            .subscribe(
                                                    new Action1<SMSSentConfirmationDTO>()
                                                    {
                                                        @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                                        {
                                                            Observer<SMSSentConfirmationDTO> copy = confirmationSubject;
                                                            if (copy != null)
                                                            {
                                                                copy.onNext(smsSentConfirmationDTO);
                                                            }
                                                        }
                                                    },
                                                    new TimberOnErrorAction("Failed to get confirmation from sms")));
                                }
                                else if (userAction instanceof VerifyCodeDigitView.UserActionVerify)
                                {
                                    if (((VerifyCodeDigitView.UserActionVerify) userAction).code.equals(expectedCode))
                                    {
                                        LiveSignUpStep1AyondoFragment.this.expectedCode = null;
                                        LiveSignUpStep1AyondoFragment.this.confirmationSubject = null;
                                        kycForm.setVerifiedMobileNumber(phoneNumberInt);
                                        alertDialog.dismiss();
                                        onNext(kycForm);
                                    }
                                    else
                                    {
                                        onStopSubscriptions.add(AlertDialogRxUtil.build(getActivity())
                                                .setMessage(R.string.sms_verification_not_match)
                                                .setPositiveButton(R.string.ok)
                                                .build()
                                                .subscribe(
                                                        new Action1<OnDialogClickEvent>()
                                                        {
                                                            @Override public void call(OnDialogClickEvent clickEvent)
                                                            {
                                                            }
                                                        },
                                                        new TimberOnErrorAction("Failed to prompt user to start sms verification again")));
                                    }
                                }
                                else if (userAction instanceof VerifyCodeDigitView.UserActionDismiss)
                                {
                                    LiveSignUpStep1AyondoFragment.this.confirmationSubject = null;
                                    LiveSignUpStep1AyondoFragment.this.expectedCode = null;
                                    alertDialog.dismiss();
                                }
                                return userAction;
                            }
                        })
                        .subscribe(
                                new Action1<VerifyCodeDigitView.UserAction>()
                                {
                                    @Override public void call(VerifyCodeDigitView.UserAction userAction)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to send SMS")
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        super.call(throwable);
                                        buttonVerifyPhone.setEnabled(true);
                                        buttonVerifyPhone.setText(R.string.verify);
                                    }
                                }));
    }

    @NonNull protected Observable<SMSSentConfirmationDTO> updateVerifyView(
            @NonNull Observable<SMSSentConfirmationDTO> smsSentConfirmationDTOObservable,
            @NonNull final String phoneNumberText,
            @NonNull final String expectedCode,
            @NonNull final VerifyCodeDigitView verifyCodeDigitView)
    {
        return smsSentConfirmationDTOObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<SMSSentConfirmationDTO>()
                {
                    @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        verifyCodeDigitView.display(new VerifyCodeDigitView.Requisite(phoneNumberText, expectedCode, smsSentConfirmationDTO));
                    }
                });
    }

    @NonNull protected Observable<SMSSentConfirmationDTO> createSendSMSObservable(
            @NonNull final String phoneNumberText,
            @NonNull String expectedCode)
    {
        return smsServiceWrapper.sendMessage(
                SMSRequestFactory.create(
                        phoneNumberText,
                        getString(R.string.sms_verification_sms_content, expectedCode)))
                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        return createRepeatableSMSConfirmation(smsSentConfirmationDTO.getSMSId())
                                .startWith(smsSentConfirmationDTO);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<? extends SMSSentConfirmationDTO> call(final Throwable throwable)
                    {
                        String message = throwable.getMessage();
                        if (TextUtils.isEmpty(message))
                        {
                            message = getString(R.string.sms_verification_send_fail);
                        }
                        return AlertDialogRxUtil.build(getActivity())
                                .setTitle(R.string.sms_verification_send_fail_title)
                                .setMessage(message)
                                .setNegativeButton(R.string.ok)
                                .build()
                                .flatMap(new Func1<OnDialogClickEvent, Observable<SMSSentConfirmationDTO>>()
                                {
                                    @Override public Observable<SMSSentConfirmationDTO> call(OnDialogClickEvent clickEvent)
                                    {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                })
                .startWith(new EmptySMSSentConfirmationDTO(phoneNumberText, "Fake", R.string.sms_verification_button_empty_submitting));
    }

    @NonNull protected Observable<SMSSentConfirmationDTO> createRepeatableSMSConfirmation(@NonNull final SMSId smsId)
    {
        return smsServiceWrapper.getMessageStatus(smsId)
                .delaySubscription(DEFAULT_POLL_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        if (smsSentConfirmationDTO.isFinalStatus())
                        {
                            return Observable.just(smsSentConfirmationDTO);
                        }
                        return createRepeatableSMSConfirmation(smsId);
                    }
                });
    }

    @NonNull protected Observable<VerifyCodeDigitView.UserAction> displayVerifyDialog(
            @NonNull final VerifyCodeDigitView verifyView,
            @NonNull final Observer<AlertDialog> verifyDialogObserver)
    {
        onStopSubscriptions.add(AlertDialogRxUtil.build(getActivity())
                .setView(verifyView)
                .setAlertDialogObserver(verifyDialogObserver)
                .build()
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        buttonVerifyPhone.setEnabled(true);
                    }
                })
                .subscribe(new Action1<OnDialogClickEvent>()
                           {
                               @Override public void call(OnDialogClickEvent clickEvent)
                               {
                                   Timber.d("ClickEvent " + clickEvent);
                               }
                           },
                        new ToastAndLogOnErrorAction("Failed to listen to VerifyView")));
        return verifyView.getUserActionObservable();
    }
}
