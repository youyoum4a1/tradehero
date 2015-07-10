package com.tradehero.th.fragments.live.ayondo;

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
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.live.CountrySpinnerAdapter;
import com.tradehero.th.fragments.live.VerifyCodeDigitView;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSRequestFactory;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import com.tradehero.th.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.validation.TextValidator;
import com.tradehero.th.widget.validation.ValidatedText;
import com.tradehero.th.widget.validation.ValidationMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import rx.functions.Func4;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final long DEFAULT_POLL_INTERVAL_MILLISEC = 1000;
    private static final String KEY_EXPECTED_CODE = LiveSignUpStep1AyondoFragment.class.getName() + ".expectedCode";
    @LayoutRes private static final int LAYOUT_COUNTRY = R.layout.spinner_live_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_COUNTRY_SELECTED_FLAG = R.layout.spinner_live_country_dropdown_item_selected;
    @LayoutRes private static final int LAYOUT_PHONE_COUNTRY = R.layout.spinner_live_phone_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_PHONE_SELECTED_FLAG = R.layout.spinner_live_phone_country_dropdown_item_selected;

    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.et_firstname) TextView firstName;
    @Bind(R.id.et_lastname) TextView lastName;
    @Bind(R.id.sign_up_email) ValidatedText email;
    TextValidator emailValidator;
    @Bind(R.id.country_code_spinner) Spinner spinnerPhoneCountryCode;
    @Bind(R.id.number_right) EditText phoneNumber;
    @Bind(R.id.btn_verify_phone) TextView buttonVerifyPhone;
    @Bind(R.id.info_nationality) Spinner spinnerNationality;
    @Bind(R.id.info_residency) Spinner spinnerResidency;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject SMSServiceWrapper smsServiceWrapper;

    private Random randomiser;
    private CountrySpinnerAdapter phoneCountryCodeAdapter;
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

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                WidgetObservable.text(firstName),
                new Func2<LiveBrokerSituationDTO, OnTextChangeEvent, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situation, OnTextChangeEvent firstNameEvent)
                    {
                        //noinspection ConstantConditions
                        ((KYCAyondoForm) situation.kycForm).setFirstName(firstNameEvent.text().toString());
                        onNext(situation);
                        return null;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to first name")));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                WidgetObservable.text(lastName),
                new Func2<LiveBrokerSituationDTO, OnTextChangeEvent, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situation, OnTextChangeEvent lastNameEvent)
                    {
                        //noinspection ConstantConditions
                        ((KYCAyondoForm) situation.kycForm).setLastName(lastNameEvent.text().toString());
                        onNext(situation);
                        return null;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to last name")));

        emailValidator = email.getValidator();
        email.setOnFocusChangeListener(emailValidator);
        email.addTextChangedListener(emailValidator);
        onDestroyViewSubscriptions.add(emailValidator.getValidationMessageObservable()
                .subscribe(
                        new Action1<ValidationMessage>()
                        {
                            @Nullable private String previousMessage;

                            @Override public void call(ValidationMessage validationMessage)
                            {
                                email.setStatus(validationMessage.getValidStatus());
                                String message = validationMessage.getMessage();
                                if (message != null && !TextUtils.isEmpty(message) && !message.equals(previousMessage))
                                {
                                    THToast.show(validationMessage.getMessage());
                                }
                                previousMessage = message;
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to email validation")));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                WidgetObservable.text(email),
                new Func2<LiveBrokerSituationDTO, OnTextChangeEvent, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situation, OnTextChangeEvent emailEvent)
                    {
                        //noinspection ConstantConditions
                        ((KYCAyondoForm) situation.kycForm).setEmail(emailEvent.text().toString());
                        onNext(situation);
                        return null;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to email")));

        phoneCountryCodeAdapter = new CountrySpinnerAdapter(getActivity(), LAYOUT_PHONE_SELECTED_FLAG, LAYOUT_PHONE_COUNTRY);
        spinnerPhoneCountryCode.setAdapter(phoneCountryCodeAdapter);

        nationalityAdapter = new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
        spinnerNationality.setAdapter(nationalityAdapter);

        residencyAdapter = new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
        spinnerResidency.setAdapter(residencyAdapter);

        // Maybe move this until we get the KYCForm, and use the KYCForm to fetch the list of country of residence.
        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable()
                        .observeOn(AndroidSchedulers.mainThread()),
                getKYCAyondoFormOptionsObservable()
                        .observeOn(Schedulers.computation())
                        .map(new Func1<KYCAyondoFormOptionsDTO, CountryDTOForSpinner>()
                        {
                            @Override public CountryDTOForSpinner call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                            {
                                return new CountryDTOForSpinner(getActivity(), kycAyondoFormOptionsDTO);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func3<LiveBrokerSituationDTO, CountryDTOForSpinner, UserProfileDTO, Object>()
                {
                    @Override public Object call(LiveBrokerSituationDTO situation,
                            CountryDTOForSpinner options,
                            UserProfileDTO currentUserProfile)
                    {
                        phoneCountryCodeAdapter.setNotifyOnChange(false);
                        phoneCountryCodeAdapter.clear();
                        phoneCountryCodeAdapter.addAll(options.allowedMobilePhoneCountryDTOs);
                        phoneCountryCodeAdapter.setNotifyOnChange(true);
                        phoneCountryCodeAdapter.notifyDataSetChanged();

                        residencyAdapter.setNotifyOnChange(false);
                        residencyAdapter.clear();
                        residencyAdapter.addAll(options.allowedResidencyCountryDTOs);
                        residencyAdapter.setNotifyOnChange(true);
                        residencyAdapter.notifyDataSetChanged();

                        nationalityAdapter.setNotifyOnChange(false);
                        nationalityAdapter.clear();
                        nationalityAdapter.addAll(options.allowedNationalityCountryDTOs);
                        nationalityAdapter.setNotifyOnChange(true);
                        nationalityAdapter.notifyDataSetChanged();

                        //noinspection ConstantConditions
                        populateMobileCountryCode((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedMobilePhoneCountryDTOs);
                        populateNationality((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedNationalityCountryDTOs);
                        populateResidency((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedResidencyCountryDTOs);
                        Integer countryCode = ((KYCAyondoForm) situation.kycForm).getMobileNumberCountryCode();
                        Long phoneNumber = ((KYCAyondoForm) situation.kycForm).getMobileNumber();
                        if (countryCode != null && phoneNumber != null)
                        {
                            populateVerifyMobile((KYCAyondoForm) situation.kycForm, countryCode, phoneNumber);
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<Object>()
                {
                    @Override public void call(Object o)
                    {
                    }
                }));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                userProfileCache.getOne(currentUserId.toUserBaseKey()).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                getBrokerSituationObservable(),
                AdapterViewObservable.selects(spinnerPhoneCountryCode),
                WidgetObservable.text(phoneNumber),
                new Func4<UserProfileDTO, LiveBrokerSituationDTO, OnSelectedEvent, OnTextChangeEvent, Boolean>()
                {
                    @Override public Boolean call(UserProfileDTO currentUserProfile, LiveBrokerSituationDTO situation, OnSelectedEvent countryEvent,
                            OnTextChangeEvent onTextChangeEvent)
                    {
                        if (onTextChangeEvent.text().length() > 0 && countryEvent instanceof OnItemSelectedEvent)
                        {
                            try
                            {
                                int newCountryCode = ((CountrySpinnerAdapter.DTO) countryEvent.parent.getItemAtPosition(
                                        ((OnItemSelectedEvent) countryEvent).position)).phoneCountryCode;
                                long newNumber = Long.parseLong(onTextChangeEvent.text().toString());
                                //noinspection ConstantConditions
                                populateVerifyMobile((KYCAyondoForm) situation.kycForm, newCountryCode, newNumber);
                                ((KYCAyondoForm) situation.kycForm).setMobileNumberCountryCode(newCountryCode);
                                ((KYCAyondoForm) situation.kycForm).setMobileNumber(newNumber);
                                onNext(situation);
                            } catch (NumberFormatException e)
                            {
                                Timber.e(e, "Failed to parse to number %s", onTextChangeEvent.text().toString());
                            }
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

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                AdapterViewObservable.selects(spinnerNationality),
                new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent nationalityEvent)
                    {
                        CountryCode newNationality =
                                CountryCode.getByCode(((CountrySpinnerAdapter.DTO) nationalityEvent.parent.getItemAtPosition(
                                        ((OnItemSelectedEvent) nationalityEvent).position)).country.name());
                        //noinspection ConstantConditions
                        ((KYCAyondoForm) situationDTO.kycForm).setNationality(newNationality);
                        onNext(situationDTO);
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
                        new TimberOnErrorAction("Failed to listen to nationality")));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                AdapterViewObservable.selects(spinnerResidency),
                new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent residencyEvent)
                    {
                        CountryCode newResidency =
                                CountryCode.getByCode(((CountrySpinnerAdapter.DTO) residencyEvent.parent.getItemAtPosition(
                                        ((OnItemSelectedEvent) residencyEvent).position)).country.name());
                        //noinspection ConstantConditions
                        ((KYCAyondoForm) situationDTO.kycForm).setResidency(newResidency);
                        onNext(situationDTO);
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
                        new TimberOnErrorAction("Failed to listen to nationality")));

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
            final int phoneCountryCode = ((CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem()).phoneCountryCode;
            final long phoneNumberInt = Long.parseLong(phoneNumber.getText().toString());
            final String phoneNumberText = "+" + phoneCountryCode + phoneNumberInt;
            final VerifyCodeDigitView verifyCodeDigitView =
                    (VerifyCodeDigitView) LayoutInflater.from(getActivity()).inflate(R.layout.verify_phone_number, null);
            offerToEnterCode(
                    phoneCountryCode,
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
        email.removeTextChangedListener(emailValidator);
        email.setOnFocusChangeListener(null);
        emailValidator = null;
        phoneCountryCodeAdapter = null;
        residencyAdapter = null;
        nationalityAdapter = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        confirmationSubject = null;
        unsubscribe(confirmationSubscription);
        super.onDestroy();
    }

    @Override public void onNext(@NonNull LiveBrokerSituationDTO situationDTO)
    {
        super.onNext(situationDTO);
        if (!(situationDTO.kycForm instanceof KYCAyondoForm))
        {
            Timber.e(new IllegalArgumentException(), "Should not submit a situation.KYC of type: %s", situationDTO.kycForm);
        }
        // TODO
    }

    @NonNull @Override protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                    {
                        //noinspection ConstantConditions
                        populate((KYCAyondoForm) situationDTO.kycForm);
                    }
                });
    }

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        String firstNameText = kycForm.getFirstName();
        if (firstName != null && firstNameText != null && !firstNameText.equals(firstName.getText().toString()))
        {
            firstName.setText(firstNameText);
        }

        String lastNameText = kycForm.getLastName();
        if (lastName != null && lastNameText != null && !lastNameText.equals(lastName.getText().toString()))
        {
            lastName.setText(lastNameText);
        }

        String emailText = kycForm.getEmail();
        if (email != null && emailText != null && !emailText.equals(email.getText().toString()))
        {
            email.setText(emailText);
        }

        Long mobileNumber = kycForm.getMobileNumber();
        if (phoneNumber != null && mobileNumber != null)
        {
            String formatted = String.format("%d", mobileNumber);
            if (!formatted.equals(phoneNumber.getText().toString()))
            {
                phoneNumber.setText(formatted);
            }
        }
    }

    protected void populateVerifyMobile(@NonNull KYCAyondoForm kycForm, int countryCode, long typedNumber)
    {
        if (buttonVerifyPhone != null)
        {
            boolean verified = Integer.valueOf(countryCode).equals(kycForm.getVerifiedMobileNumberCountryCode())
                    && Long.valueOf(typedNumber).equals(kycForm.getVerifiedMobileNumber());
            buttonVerifyPhone.setText(verified ? R.string.verified : R.string.verify);
            buttonVerifyPhone.setEnabled(!verified);
        }
    }

    protected void populateMobileCountryCode(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        Integer savedMobileNumberCountryCode = kycForm.getMobileNumberCountryCode();
        final List<CountrySpinnerAdapter.DTO> candidates;
        if (savedMobileNumberCountryCode != null)
        {
            candidates = CountrySpinnerAdapter.getFilterByPhoneCountryCode(liveCountryDTOs, savedMobileNumberCountryCode);
        }
        else
        {
            candidates = new ArrayList<>();
        }

        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        setSpinnerOnFirst(spinnerPhoneCountryCode, candidates, liveCountryDTOs);
    }

    protected void populateNationality(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        CountryCode savedNationality = kycForm.getNationality();
        List<CountrySpinnerAdapter.DTO> candidates;
        if (savedNationality != null)
        {
            try
            {
                candidates = CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs,
                        Collections.singletonList(Enum.valueOf(Country.class, savedNationality.getAlpha2())));
            } catch (Exception e)
            {
                candidates = new ArrayList<>();
            }
        }
        else
        {
            candidates = new ArrayList<>();
        }

        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        setSpinnerOnFirst(spinnerNationality, candidates, liveCountryDTOs);
    }

    protected void populateResidency(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        CountryCode savedResidency = kycForm.getResidency();
        List<CountrySpinnerAdapter.DTO> candidates;
        if (savedResidency != null)
        {
            try
            {
                candidates = CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs,
                        Collections.singletonList(Enum.valueOf(Country.class, savedResidency.getAlpha2())));
            } catch (Exception e)
            {
                candidates = new ArrayList<>();
            }
        }
        else
        {
            candidates = new ArrayList<>();
        }

        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        setSpinnerOnFirst(spinnerResidency, candidates, liveCountryDTOs);
    }

    @NonNull protected List<CountrySpinnerAdapter.DTO> getFilteredByCountries(
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs,
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile)
    {
        List<CountrySpinnerAdapter.DTO> defaultOnes = new ArrayList<>();
        defaultOnes.addAll(CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs, Collections.singletonList(kycForm.getCountry())));
        Country userCountry = currentUserProfile.getCountry();
        if (userCountry != null)
        {
            defaultOnes.addAll(CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs, Collections.singletonList(userCountry)));
        }
        return defaultOnes;
    }

    protected void setSpinnerOnFirst(
            @NonNull Spinner spinner,
            @NonNull List<CountrySpinnerAdapter.DTO> candidates,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        Integer countryIndex = null;
        int candidatePhoneCountryIndex;
        for (CountrySpinnerAdapter.DTO candidate : candidates)
        {
            candidatePhoneCountryIndex = liveCountryDTOs.indexOf(candidate);
            if (candidatePhoneCountryIndex >= 0)
            {
                countryIndex = candidatePhoneCountryIndex;
                break;
            }
        }

        if (countryIndex != null)
        {
            spinner.setSelection(countryIndex);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_verify_phone)
    protected void onVerifyPhoneClicked(View view)
    {
        buttonVerifyPhone.setEnabled(false);

        final int phoneCountryCode = ((CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem()).phoneCountryCode;
        final long phoneNumberInt = Long.parseLong(phoneNumber.getText().toString());
        final String phoneNumberText = "+" + phoneCountryCode + phoneNumberInt;
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
                phoneCountryCode,
                phoneNumberInt,
                phoneNumberText,
                expectedCode,
                verifyCodeDigitView,
                confirmationSubject);
    }

    protected void offerToEnterCode(
            final int phoneCountryCode,
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
                        getBrokerSituationObservable(),
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
                        new Func3<AlertDialog, LiveBrokerSituationDTO, VerifyCodeDigitView.UserAction, VerifyCodeDigitView.UserAction>()
                        {
                            @Override
                            public VerifyCodeDigitView.UserAction call(AlertDialog alertDialog, LiveBrokerSituationDTO situation,
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
                                        //noinspection ConstantConditions
                                        ((KYCAyondoForm) situation.kycForm).setVerifiedMobileNumberCountryCode(phoneCountryCode);
                                        ((KYCAyondoForm) situation.kycForm).setVerifiedMobileNumber(phoneNumberInt);
                                        alertDialog.dismiss();
                                        onNext(situation);
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
