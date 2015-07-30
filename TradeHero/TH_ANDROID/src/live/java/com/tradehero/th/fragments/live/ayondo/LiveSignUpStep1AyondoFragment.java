package com.tradehero.th.fragments.live.ayondo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.live.CountrySpinnerAdapter;
import com.tradehero.th.fragments.live.DatePickerDialogFragment;
import com.tradehero.th.fragments.live.VerifyCodeDigitView;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSRequestFactory;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import com.tradehero.th.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.persistence.prefs.PhoneNumberVerifiedPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
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
    @LayoutRes private static final int LAYOUT_COUNTRY = R.layout.spinner_live_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_COUNTRY_SELECTED_FLAG = R.layout.spinner_live_country_dropdown_item_selected;
    @LayoutRes private static final int LAYOUT_PHONE_COUNTRY = R.layout.spinner_live_phone_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_PHONE_SELECTED_FLAG = R.layout.spinner_live_phone_country_dropdown_item_selected;
    private static final int REQUEST_PICK_DATE = 2805;

    @Bind(R.id.info_username) TextView userName;
    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.info_full_name) TextView fullName;
    @Bind(R.id.sign_up_email) EditText email;
    @Bind(R.id.country_code_spinner) Spinner spinnerPhoneCountryCode;
    @Bind(R.id.number_right) EditText phoneNumber;
    @Bind(R.id.info_dob) TextView dob;
    @Bind(R.id.btn_verify_phone) TextView buttonVerifyPhone;
    @Bind(R.id.info_nationality) Spinner spinnerNationality;
    @Bind(R.id.info_residency) Spinner spinnerResidency;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject SMSServiceWrapper smsServiceWrapper;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject PhoneNumberVerifiedPreference phoneNumberVerifiedPreference;

    private Observable<CountryDTOForSpinner> countryDTOSpinnerObservable;
    private Random randomiser;
    @Nullable private String expectedCode;
    @Nullable private BehaviorSubject<SMSSentConfirmationDTO> confirmationSubject;
    private Subscription confirmationSubscription;
    private Pattern emailPattern;
    private String emailInvalidMessage;

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

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                createBrokerObservable(),
                Observable.merge(
                        WidgetObservable.text(userName)
                                .map(new Func1<OnTextChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(
                                            OnTextChangeEvent userNameEvent)
                                    {
                                        return KYCAyondoFormFactory.fromUserNameEvent(userNameEvent);
                                    }
                                }),
                        WidgetObservable.text(fullName)
                                .map(new Func1<OnTextChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(
                                            OnTextChangeEvent fullNameEvent)
                                    {
                                        return KYCAyondoFormFactory.fromFullNameEvent(fullNameEvent);
                                    }
                                }),
                        WidgetObservable.text(email)
                                .map(new Func1<OnTextChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(
                                            OnTextChangeEvent emailEvent)
                                    {
                                        return KYCAyondoFormFactory.fromEmailEvent(emailEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(title)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent titleEvent)
                                    {
                                        return KYCAyondoFormFactory.fromTitleEvent(titleEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(spinnerNationality)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent nationalityEvent)
                                    {
                                        return KYCAyondoFormFactory.fromNationalityEvent(nationalityEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(spinnerResidency)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent residencyEvent)
                                    {
                                        return KYCAyondoFormFactory.fromResidencyEvent(residencyEvent);
                                    }
                                }),
                        WidgetObservable.text(dob)
                                .map(new Func1<OnTextChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnTextChangeEvent dobEvent)
                                    {
                                        return KYCAyondoFormFactory.fromDobEvent(dobEvent);
                                    }
                                })),
                new Func2<LiveBrokerDTO, KYCAyondoForm, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(LiveBrokerDTO brokerDTO, KYCAyondoForm update)
                    {
                        return new LiveBrokerSituationDTO(brokerDTO, update);
                    }
                })
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO update)
                            {
                                onNext(update);
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to title, full name, email, nationality o residency spinners, or dob")));

        emailPattern = Pattern.compile(getString(R.string.regex_email_validator));
        emailInvalidMessage = getString(R.string.validation_incorrect_pattern_email);
        onDestroyViewSubscriptions.add(
                WidgetObservable.text(email)
                        .filter(new Func1<OnTextChangeEvent, Boolean>()
                        {
                            @Override public Boolean call(OnTextChangeEvent onTextChangeEvent)
                            {
                                return !TextUtils.isEmpty(onTextChangeEvent.text());
                            }
                        })
                        .map(new Func1<OnTextChangeEvent, String>()
                        {
                            @Override public String call(OnTextChangeEvent onTextChangeEvent)
                            {
                                if (!emailPattern.matcher(onTextChangeEvent.text()).matches())
                                {
                                    return emailInvalidMessage;
                                }
                                return null;
                            }
                        })
                        .subscribe(new Action1<String>()
                        {
                            @Override public void call(@Nullable String errorMessage)
                            {
                                email.setError(errorMessage);
                            }
                        }, new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Failed to validate email");
                            }
                        })
        );

        //Some kind of a note for validating display name
        //Observable.combineLatest(
        //    nameObservable,
        //    nameObservable.throttle
        //        .flatMap(name -> serverValidation(name)),
        //        (name, valid) -> Pair)
        //    .filter

        // Maybe move this until we get the KYCForm, and use the KYCForm to fetch the list of country of residence.
        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
                        {
                            @Override public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situation)
                            {
                                //noinspection ConstantConditions
                                populate((KYCAyondoForm) situation.kycForm);

                                final Integer dialingPrefix = ((KYCAyondoForm) situation.kycForm).getMobileNumberDialingPrefix();
                                final String phoneNumber = ((KYCAyondoForm) situation.kycForm).getMobileNumber();
                                if (dialingPrefix != null && phoneNumber != null)
                                {
                                    String numberText = "+" + dialingPrefix + phoneNumber;
                                    return liveServiceWrapper.getPhoneNumberVerifiedStatus(numberText)
                                            .map(new Func1<PhoneNumberVerifiedStatusDTO, LiveBrokerSituationDTO>()
                                            {
                                                @Override
                                                public LiveBrokerSituationDTO call(PhoneNumberVerifiedStatusDTO verifiedStatus)
                                                {
                                                    KYCAyondoForm update = new KYCAyondoForm();
                                                    if (verifiedStatus.verified)
                                                    {
                                                        update.setVerifiedMobileNumberDialingPrefix(dialingPrefix);
                                                        update.setVerifiedMobileNumber(phoneNumber);
                                                    }
                                                    onNext(new LiveBrokerSituationDTO(situation.broker, update));
                                                    situation.kycForm.pickFrom(update);
                                                    populateVerifyMobile((KYCAyondoForm) situation.kycForm, dialingPrefix, phoneNumber);
                                                    return situation;
                                                }
                                            });
                                }
                                return Observable.just(situation);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                getCountryDTOSpinnerObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<CountryDTOForSpinner>()
                        {
                            @Override public void call(CountryDTOForSpinner options)
                            {
                                LollipopArrayAdapter<GenderDTO> genderAdapter = new LollipopArrayAdapter<>(
                                        getActivity(),
                                        GenderDTO.createList(getResources(), options.genders));
                                title.setAdapter(genderAdapter);
                                title.setEnabled(options.genders.size() > 1);

                                CountrySpinnerAdapter phoneCountryCodeAdapter =
                                        new CountrySpinnerAdapter(getActivity(), LAYOUT_PHONE_SELECTED_FLAG, LAYOUT_PHONE_COUNTRY);
                                phoneCountryCodeAdapter.addAll(options.allowedMobilePhoneCountryDTOs);
                                spinnerPhoneCountryCode.setAdapter(phoneCountryCodeAdapter);
                                spinnerPhoneCountryCode.setEnabled(options.allowedMobilePhoneCountryDTOs.size() > 1);

                                CountrySpinnerAdapter residencyAdapter =
                                        new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
                                residencyAdapter.addAll(options.allowedResidencyCountryDTOs);
                                spinnerResidency.setAdapter(residencyAdapter);
                                spinnerResidency.setEnabled(options.allowedResidencyCountryDTOs.size() > 1);

                                CountrySpinnerAdapter nationalityAdapter =
                                        new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
                                nationalityAdapter.addAll(options.allowedNationalityCountryDTOs);
                                spinnerNationality.setAdapter(nationalityAdapter);
                                spinnerNationality.setEnabled(options.allowedNationalityCountryDTOs.size() > 1);
                            }
                        }),
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func3<LiveBrokerSituationDTO, CountryDTOForSpinner, UserProfileDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situation,
                            CountryDTOForSpinner options,
                            UserProfileDTO currentUserProfile)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        //noinspection ConstantConditions
                        update.pickFrom(populateGender((KYCAyondoForm) situation.kycForm, options.genders));
                        update.pickFrom(populateMobileCountryCode((KYCAyondoForm) situation.kycForm, currentUserProfile,
                                options.allowedMobilePhoneCountryDTOs));
                        update.pickFrom(
                                populateNationality((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedNationalityCountryDTOs));
                        update.pickFrom(
                                populateResidency((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedResidencyCountryDTOs));
                        onNext(new LiveBrokerSituationDTO(situation.broker, update));
                        situation.kycForm.pickFrom(update);
                        return situation;
                    }
                })
                .subscribe(
                        new EmptyAction1<LiveBrokerSituationDTO>(),
                        new TimberOnErrorAction1("Failed to load phone drop down lists")));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable()
                        .observeOn(AndroidSchedulers.mainThread()),
                Observable.combineLatest(
                        AdapterViewObservable.selects(spinnerPhoneCountryCode)
                                .filter(new Func1<OnSelectedEvent, Boolean>()
                                {
                                    @Override public Boolean call(OnSelectedEvent onSelectedEvent)
                                    {
                                        boolean ok = onSelectedEvent instanceof OnItemSelectedEvent;
                                        if (!ok)
                                        {
                                            buttonVerifyPhone.setEnabled(false);
                                        }
                                        return ok;
                                    }
                                })
                                .cast(OnItemSelectedEvent.class),
                        WidgetObservable.text(phoneNumber)
                                .filter(new Func1<OnTextChangeEvent, Boolean>()
                                {
                                    @Override public Boolean call(OnTextChangeEvent onTextChangeEvent)
                                    {
                                        boolean ok = onTextChangeEvent.text().length() > 0;
                                        if (!ok)
                                        {
                                            buttonVerifyPhone.setEnabled(false);
                                        }
                                        return ok;
                                    }
                                }),
                        new Func2<OnItemSelectedEvent, OnTextChangeEvent, Pair<Integer, String>>()
                        {
                            @Override public Pair<Integer, String> call(OnItemSelectedEvent dialingPrefixEvent, OnTextChangeEvent typedNumberEvent)
                            {
                                int dialingPrefix = ((CountrySpinnerAdapter.DTO) dialingPrefixEvent.parent.getItemAtPosition(
                                        dialingPrefixEvent.position)).phoneCountryCode;
                                String newNumber = typedNumberEvent.text().toString();
                                return Pair.create(dialingPrefix, newNumber);
                            }
                        })
                        .flatMap(new Func1<Pair<Integer, String>, Observable<PhoneNumberAndVerifiedDTO>>()
                        {
                            @Override public Observable<PhoneNumberAndVerifiedDTO> call(final Pair<Integer, String> phoneNumberPair)
                            {
                                String numberText = "+" + phoneNumberPair.first + phoneNumberPair.second;
                                return liveServiceWrapper.getPhoneNumberVerifiedStatus(numberText)
                                        .map(new Func1<PhoneNumberVerifiedStatusDTO, PhoneNumberAndVerifiedDTO>()
                                        {
                                            @Override public PhoneNumberAndVerifiedDTO call(PhoneNumberVerifiedStatusDTO verifiedStatusDTO)
                                            {
                                                return new PhoneNumberAndVerifiedDTO(
                                                        phoneNumberPair.first,
                                                        phoneNumberPair.second,
                                                        verifiedStatusDTO.verified);
                                            }
                                        });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func2<LiveBrokerSituationDTO, PhoneNumberAndVerifiedDTO, Boolean>()
                {
                    @Override public Boolean call(
                            LiveBrokerSituationDTO situation,
                            PhoneNumberAndVerifiedDTO phoneNumberAndVerifiedDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        int dialingPrefix = phoneNumberAndVerifiedDTO.dialingPrefix;
                        String newNumber = phoneNumberAndVerifiedDTO.typedNumber;
                        if (phoneNumberAndVerifiedDTO.verified)
                        {
                            update.setVerifiedMobileNumberDialingPrefix(dialingPrefix);
                            update.setVerifiedMobileNumber(newNumber);
                        }
                        //noinspection ConstantConditions
                        situation.kycForm.pickFrom(update);
                        //noinspection ConstantConditions
                        populateVerifyMobile((KYCAyondoForm) situation.kycForm, dialingPrefix, newNumber);
                        update.setMobileNumberDialingPrefix(dialingPrefix);
                        update.setMobileNumber(newNumber);
                        onNext(new LiveBrokerSituationDTO(situation.broker, update));
                        return true;
                    }
                })
                .subscribe(
                        new EmptyAction1<Boolean>(),
                        new TimberAndToastOnErrorAction1("Failed to listen to phone number updates")));

        onDestroyViewSubscriptions.add(ViewObservable.clicks(dob)
                .flatMap(new Func1<OnClickEvent, Observable<KYCAyondoFormOptionsDTO>>()
                {
                    @Override public Observable<KYCAyondoFormOptionsDTO> call(OnClickEvent onClickEvent)
                    {
                        return getKYCAyondoFormOptionsObservable().take(1);
                    }
                })
                .map(new Func1<KYCAyondoFormOptionsDTO, Calendar>()
                {
                    @Override public Calendar call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                    {
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR) - kycAyondoFormOptionsDTO.minAge;
                        c.set(Calendar.YEAR, year);
                        return c;
                    }
                })
                .subscribe(new Action1<Calendar>()
                {
                    @Override public void call(Calendar calendar)
                    {
                        Calendar selected = null;
                        if (!TextUtils.isEmpty(dob.getText()))
                        {
                            Date d = DateUtils.parseString(getResources(), dob.getText().toString(), R.string.info_date_format);
                            if (d != null)
                            {
                                selected = Calendar.getInstance();
                                selected.setTime(d);
                            }
                        }
                        DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance(calendar, selected);
                        dpf.setTargetFragment(LiveSignUpStep1AyondoFragment.this, REQUEST_PICK_DATE);
                        dpf.show(getChildFragmentManager(), dpf.getClass().getName());
                    }
                }, new TimberOnErrorAction1("Failed to listen to DOB clicks")));

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
            final String phoneNumberInt = phoneNumber.getText().toString();
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

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        String fullNameText = kycForm.getFullName();
        if (fullName != null && fullNameText != null && !fullNameText.equals(fullName.getText().toString()))
        {
            fullName.setText(fullNameText);
        }

        String emailText = kycForm.getEmail();
        if (email != null && emailText != null && !emailText.equals(email.getText().toString()))
        {
            email.setText(emailText);
        }

        String mobileNumberText = kycForm.getMobileNumber();
        if (phoneNumber != null && mobileNumberText != null && !mobileNumberText.equals(phoneNumber.getText().toString()))
        {
            phoneNumber.setText(mobileNumberText);
        }

        String dobText = kycForm.getDob();
        if (dob != null && dobText != null && !dobText.equals(dob.getText().toString()))
        {
            dob.setText(dobText);
        }
    }

    @NonNull protected Observable<CountryDTOForSpinner> getCountryDTOSpinnerObservable()
    {
        Observable<CountryDTOForSpinner> copy = countryDTOSpinnerObservable;
        if (copy == null)
        {
            copy = createCountryDTOSpinnerObservable().share().cache(1);
            countryDTOSpinnerObservable = copy;
        }
        return copy;
    }

    @NonNull protected Observable<CountryDTOForSpinner> createCountryDTOSpinnerObservable()
    {
        return getKYCAyondoFormOptionsObservable()
                .observeOn(Schedulers.computation())
                .map(new Func1<KYCAyondoFormOptionsDTO, CountryDTOForSpinner>()
                {
                    @Override public CountryDTOForSpinner call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                    {
                        return new CountryDTOForSpinner(getActivity(), kycAyondoFormOptionsDTO);
                    }
                });
    }

    protected void populateVerifyMobile(@NonNull KYCAyondoForm kycForm, int countryCode, @NonNull String typedNumber)
    {
        if (buttonVerifyPhone != null)
        {
            boolean verified = Integer.valueOf(countryCode).equals(kycForm.getVerifiedMobileNumberDialingPrefix())
                    && typedNumber.equals(kycForm.getVerifiedMobileNumber());
            buttonVerifyPhone.setEnabled(!verified && !TextUtils.isEmpty(typedNumber));
            buttonVerifyPhone.setText(verified ? R.string.verified : R.string.verify);
        }
    }

    @NonNull protected KYCAyondoForm populateGender(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull List<Gender> genders)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        Gender savedGender = kycForm.getGender();
        Integer genderIndex = populateSpinner(title, savedGender, genders);
        if (savedGender == null)
        {
            Gender chosenGender;
            if (genderIndex != null)
            {
                chosenGender = genders.get(genderIndex);
            }
            else
            {
                chosenGender = ((GenderDTO) title.getSelectedItem()).gender;
            }

            if (chosenGender != null)
            {
                update.setGender(chosenGender);
            }
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateMobileCountryCode(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        Integer savedMobileNumberDialingPrefix = kycForm.getMobileNumberDialingPrefix();
        final List<CountrySpinnerAdapter.DTO> candidates;
        if (savedMobileNumberDialingPrefix != null)
        {
            candidates = CountrySpinnerAdapter.getFilterByPhoneCountryCode(liveCountryDTOs, savedMobileNumberDialingPrefix);
        }
        else
        {
            candidates = new ArrayList<>();
        }

        KYCAyondoForm update = new KYCAyondoForm();
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        Integer index = setSpinnerOnFirst(spinnerPhoneCountryCode, candidates, liveCountryDTOs);
        if (savedMobileNumberDialingPrefix == null)
        {
            CountrySpinnerAdapter.DTO chosenDTO;
            if (index != null)
            {
                chosenDTO = liveCountryDTOs.get(index);
            }
            else
            {
                chosenDTO = (CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem();
            }

            if (chosenDTO != null)
            {
                update.setMobileNumberDialingPrefix(chosenDTO.phoneCountryCode);
            }
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateNationality(
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

        KYCAyondoForm update = new KYCAyondoForm();
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        Integer index = setSpinnerOnFirst(spinnerNationality, candidates, liveCountryDTOs);
        if (savedNationality == null)
        {
            CountrySpinnerAdapter.DTO chosenDTO;
            if (index != null)
            {
                chosenDTO = liveCountryDTOs.get(index);
            }
            else
            {
                chosenDTO = (CountrySpinnerAdapter.DTO) spinnerNationality.getSelectedItem();
            }

            if (chosenDTO != null)
            {
                update.setNationality(CountryCode.getByCode(chosenDTO.country.name()));
            }
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateResidency(
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

        KYCAyondoForm update = new KYCAyondoForm();
        candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        Integer index = setSpinnerOnFirst(spinnerResidency, candidates, liveCountryDTOs);
        if (savedResidency == null)
        {
            CountrySpinnerAdapter.DTO chosenDTO;
            if (index != null)
            {
                chosenDTO = liveCountryDTOs.get(index);
            }
            else
            {
                chosenDTO = (CountrySpinnerAdapter.DTO) spinnerResidency.getSelectedItem();
            }

            if (chosenDTO != null)
            {
                update.setResidency(CountryCode.getByCode(chosenDTO.country.name()));
            }
        }
        return update;
    }

    @NonNull protected List<CountrySpinnerAdapter.DTO> getFilteredByCountries(
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs,
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile)
    {
        List<CountrySpinnerAdapter.DTO> defaultOnes = new ArrayList<>();
        Country formCountry = kycForm.getCountry();
        if (formCountry == null)
        {
            throw new NullPointerException("Country should not be null at this stage");
        }
        defaultOnes.addAll(CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs, Collections.singletonList(formCountry)));
        Country userCountry = currentUserProfile.getCountry();
        if (userCountry != null)
        {
            defaultOnes.addAll(CountrySpinnerAdapter.getFilterByCountry(liveCountryDTOs, Collections.singletonList(userCountry)));
        }
        return defaultOnes;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_verify_phone)
    protected void onVerifyPhoneClicked(View view)
    {
        final int phoneCountryCode = ((CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem()).phoneCountryCode;
        final String phoneNumberInt = phoneNumber.getText().toString();
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
                        new TimberOnErrorAction1("Failed to get confirmation from sms"));

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
            final String phoneNumberInt,
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
                                                        new EmptyAction1<SMSSentConfirmationDTO>(),
                                                        new TimberOnErrorAction1("Failed to collect SMS confirmation")));
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
                                                    new TimberOnErrorAction1("Failed to get confirmation from sms")));
                                }
                                else if (userAction instanceof VerifyCodeDigitView.UserActionVerify)
                                {
                                    if (((VerifyCodeDigitView.UserActionVerify) userAction).code.equals(expectedCode))
                                    {
                                        KYCAyondoForm update = new KYCAyondoForm();
                                        LiveSignUpStep1AyondoFragment.this.expectedCode = null;
                                        LiveSignUpStep1AyondoFragment.this.confirmationSubject = null;
                                        update.setVerifiedMobileNumberDialingPrefix(phoneCountryCode);
                                        update.setVerifiedMobileNumber(phoneNumberInt);
                                        //noinspection ConstantConditions
                                        situation.kycForm.pickFrom(update);
                                        phoneNumberVerifiedPreference.addVerifiedNumber(phoneNumberText);
                                        populateVerifyMobile((KYCAyondoForm) situation.kycForm, phoneCountryCode, phoneNumberInt);
                                        alertDialog.dismiss();
                                        onNext(new LiveBrokerSituationDTO(situation.broker, update));
                                    }
                                    else
                                    {
                                        onStopSubscriptions.add(AlertDialogRxUtil.build(getActivity())
                                                .setMessage(R.string.sms_verification_not_match)
                                                .setPositiveButton(R.string.ok)
                                                .build()
                                                .subscribe(
                                                        new EmptyAction1<OnDialogClickEvent>(),
                                                        new TimberOnErrorAction1("Failed to prompt user to start sms verification again")));
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
                                new EmptyAction1<VerifyCodeDigitView.UserAction>(),
                                new TimberOnErrorAction1("Failed to send SMS")
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
                .subscribe(new Action1<OnDialogClickEvent>()
                           {
                               @Override public void call(OnDialogClickEvent clickEvent)
                               {
                                   Timber.d("ClickEvent " + clickEvent);
                               }
                           },
                        new TimberAndToastOnErrorAction1("Failed to listen to VerifyView")));
        return verifyView.getUserActionObservable();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_DATE && resultCode == Activity.RESULT_OK)
        {
            Calendar c = DatePickerDialogFragment.getCalendarFromIntent(data);
            dob.setText(DateUtils.getDisplayableDate(getResources(), c.getTime(), R.string.info_date_format));
        }
    }
}
