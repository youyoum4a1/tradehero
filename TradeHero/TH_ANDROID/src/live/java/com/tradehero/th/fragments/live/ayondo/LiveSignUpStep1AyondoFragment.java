package com.tradehero.th.fragments.live.ayondo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.kyc.ayondo.UsernameValidationResultDTO;
import com.tradehero.th.api.live.CountryUtil;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.live.CountrySpinnerAdapter;
import com.tradehero.th.fragments.live.DatePickerDialogFragment;
import com.tradehero.th.fragments.live.VerifyPhoneDialogFragment;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.CustomWidgetObservable;
import com.tradehero.th.rx.view.OnFocusChangeEvent;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.utils.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.inject.Inject;
import rx.Observable;
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
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @LayoutRes private static final int LAYOUT_COUNTRY = R.layout.spinner_live_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_COUNTRY_SELECTED_FLAG = R.layout.spinner_live_country_dropdown_item_selected;
    @LayoutRes private static final int LAYOUT_PHONE_COUNTRY = R.layout.spinner_live_phone_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_PHONE_SELECTED_FLAG = R.layout.spinner_live_phone_country_dropdown_item_selected;
    private static final int REQUEST_PICK_DATE = 2805;
    private static final int REQUEST_VERIFY_PHONE_NUMBER_CODE = 2808;

    @Bind(R.id.info_username) TextView userName;
    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.info_full_name) TextView fullName;
    @Bind(R.id.sign_up_email) EditText email;
    @Bind(R.id.country_code_spinner) Spinner spinnerPhoneCountryCode;
    @Bind(R.id.info_phone_number) EditText phoneNumber;
    @Bind(R.id.info_dob) TextView dob;
    @Bind(R.id.btn_verify_phone) TextView buttonVerifyPhone;
    @Bind(R.id.info_nationality) Spinner spinnerNationality;
    @Bind(R.id.info_residency) Spinner spinnerResidency;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LiveServiceWrapper liveServiceWrapper;

    private Pattern emailPattern;
    private String emailInvalidMessage;
    private PublishSubject<Pair<Integer, String>> verifiedPublishSubject;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        verifiedPublishSubject = PublishSubject.create();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_1, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            final Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(Observable.merge(
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
                        }))
                .withLatestFrom(brokerDTOObservable, new Func2<KYCAyondoForm, LiveBrokerDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(KYCAyondoForm update, LiveBrokerDTO brokerDTO)
                    {
                        return new LiveBrokerSituationDTO(brokerDTO, update);
                    }
                }).subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO update)
                            {
                                onNext(update);
                            }
                        },
                        new TimberOnErrorAction1(
                                "Failed to listen to user name, password, full name, email, nationality o residency spinners, or dob")));

        emailPattern = Pattern.compile(getString(R.string.regex_email_validator));
        emailInvalidMessage = getString(R.string.validation_incorrect_pattern_email);
        subscriptions.add(
                WidgetObservable.text(email)
                        .distinctUntilChanged(new Func1<OnTextChangeEvent, CharSequence>()
                        {
                            @Override public CharSequence call(OnTextChangeEvent onTextChangeEvent)
                            {
                                return onTextChangeEvent.text();
                            }
                        })
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

        subscriptions.add(
                CustomWidgetObservable.focus(userName)
                        .distinctUntilChanged(new Func1<OnFocusChangeEvent, Boolean>()
                        {
                            @Override public Boolean call(OnFocusChangeEvent onFocusChangeEvent)
                            {
                                return onFocusChangeEvent.hasFocus;
                            }
                        })
                        .filter(new Func1<OnFocusChangeEvent, Boolean>()
                        {
                            @Override public Boolean call(OnFocusChangeEvent onFocusChangeEvent)
                            {
                                return onFocusChangeEvent.view instanceof EditText
                                        && !onFocusChangeEvent.hasFocus
                                        && !TextUtils.isEmpty(((EditText) onFocusChangeEvent.view).getText());
                            }
                        })
                        .withLatestFrom(WidgetObservable.text(userName)
                                        .throttleLast(1, TimeUnit.SECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnNext(new Action1<OnTextChangeEvent>()
                                        {
                                            @Override public void call(OnTextChangeEvent onTextChangeEvent)
                                            {
                                                onTextChangeEvent.view().setError(null);
                                            }
                                        }),
                                new Func2<OnFocusChangeEvent, OnTextChangeEvent, String>()
                                {
                                    @Override public String call(OnFocusChangeEvent onFocusChangeEvent, OnTextChangeEvent onTextChangeEvent)
                                    {
                                        return onTextChangeEvent.text().toString();
                                    }
                                })
                        .startWith(liveBrokerSituationDTOObservable
                                .map(new Func1<LiveBrokerSituationDTO, String>()
                                {
                                    @Override public String call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                                    {
                                        //noinspection ConstantConditions
                                        return ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getUserName();
                                    }
                                }))
                        .observeOn(Schedulers.io())
                        .withLatestFrom(brokerDTOObservable,
                                new Func2<String, LiveBrokerDTO, Pair<LiveBrokerDTO, String>>()
                                {
                                    @Override public Pair<LiveBrokerDTO, String> call(String s, LiveBrokerDTO liveBrokerDTO)
                                    {
                                        return Pair.create(liveBrokerDTO, s);
                                    }
                                })
                        .throttleLast(2, TimeUnit.SECONDS)
                        .distinctUntilChanged(new Func1<Pair<LiveBrokerDTO,String>, String>()
                        {
                            @Override public String call(Pair<LiveBrokerDTO, String> liveBrokerDTOStringPair)
                            {
                                return liveBrokerDTOStringPair.second;
                            }
                        })
                        .flatMap(new Func1<Pair<LiveBrokerDTO, String>, Observable<UsernameValidationResultDTO>>()
                        {
                            @Override public Observable<UsernameValidationResultDTO> call(Pair<LiveBrokerDTO, String> userNamePair)
                            {
                                return liveServiceWrapper.validateUserName(userNamePair.first.id, userNamePair.second);
                            }
                        })
                        .filter(new Func1<UsernameValidationResultDTO, Boolean>()
                        {
                            @Override public Boolean call(UsernameValidationResultDTO resultDTO)
                            {
                                return resultDTO.username != null && resultDTO.username.equals(userName.getText().toString());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<UsernameValidationResultDTO>()
                        {
                            @Override public void call(UsernameValidationResultDTO resultDTO)
                            {
                                String errorText = null;
                                if (!resultDTO.isValid)
                                {
                                    errorText = getString(R.string.live_username_invalid, resultDTO.username);
                                }
                                else if (!resultDTO.isAvailable)
                                {
                                    errorText = getString(R.string.live_username_not_available, resultDTO.username);
                                }
                                userName.setError(errorText);
                            }
                        }, new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "error on validating username");
                                userName.setError(throwable.getMessage());
                            }
                        })
        );

        subscriptions.add(Observable.combineLatest(
                liveBrokerSituationDTOObservable
                        .take(1)
                        .observeOn(AndroidSchedulers.mainThread()),
                kycAyondoFormOptionsDTOObservable
                        .observeOn(Schedulers.computation())
                        .map(new Func1<KYCAyondoFormOptionsDTO, CountryDTOForSpinner>()
                        {
                            @Override public CountryDTOForSpinner call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                            {
                                return new CountryDTOForSpinner(getActivity(), kycAyondoFormOptionsDTO);
                            }
                        })
                        .distinctUntilChanged()
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
                        ////noinspection ConstantConditions
                        populate((KYCAyondoForm) situation.kycForm);
                        populateGender((KYCAyondoForm) situation.kycForm, options.genders);
                        populateMobileCountryCode((KYCAyondoForm) situation.kycForm, currentUserProfile,
                                options.allowedMobilePhoneCountryDTOs);
                        populateNationality((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedNationalityCountryDTOs);
                        populateResidency((KYCAyondoForm) situation.kycForm, currentUserProfile, options.allowedResidencyCountryDTOs);
                        return situation;
                    }
                })
                .subscribe(
                        new EmptyAction1<LiveBrokerSituationDTO>(),
                        new TimberOnErrorAction1("Failed to load phone drop down lists")));

        subscriptions.add(
                Observable.combineLatest(
                        AdapterViewObservable.selects(spinnerPhoneCountryCode)
                                .filter(new Func1<OnSelectedEvent, Boolean>()
                                {
                                    @Override public Boolean call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return onSelectedEvent instanceof OnItemSelectedEvent;
                                    }
                                })
                                .cast(OnItemSelectedEvent.class),
                        WidgetObservable.text(phoneNumber),
                        new Func2<OnItemSelectedEvent, OnTextChangeEvent, PhoneNumberDTO>()
                        {
                            @Override public PhoneNumberDTO call(OnItemSelectedEvent onSelectedEvent, OnTextChangeEvent onTextChangeEvent)
                            {
                                CountrySpinnerAdapter.DTO selectedDTO = (CountrySpinnerAdapter.DTO) onSelectedEvent.parent.getItemAtPosition(
                                        onSelectedEvent.position);
                                return new PhoneNumberDTO(
                                        selectedDTO.country,
                                        selectedDTO.phoneCountryCode,
                                        onTextChangeEvent.text().toString());
                            }
                        })
                        .doOnNext(new Action1<PhoneNumberDTO>()
                        {
                            @Override public void call(PhoneNumberDTO ignored)
                            {
                                buttonVerifyPhone.setEnabled(false);
                            }
                        })
                        .filter(new Func1<PhoneNumberDTO, Boolean>()
                        {
                            @Override public Boolean call(PhoneNumberDTO numberDTO)
                            {
                                return numberDTO.dialingPrefix > 0 && !TextUtils.isEmpty(numberDTO.typedNumber);
                            }
                        })
                        .doOnNext(new Action1<PhoneNumberDTO>()
                        {
                            @Override public void call(PhoneNumberDTO ignored)
                            {
                                buttonVerifyPhone.setEnabled(true);
                            }
                        })
                        .distinctUntilChanged()
                        .flatMap(new Func1<PhoneNumberDTO, Observable<PhoneNumberAndVerifiedDTO>>()
                        {
                            @Override public Observable<PhoneNumberAndVerifiedDTO> call(
                                    final PhoneNumberDTO numberDTO)
                            {
                                String numberText = VerifyPhoneDialogFragment.getFormattedPhoneNumber(numberDTO.dialingPrefix, numberDTO.typedNumber);
                                return liveServiceWrapper.getPhoneNumberVerifiedStatus(numberText)
                                        .map(new Func1<PhoneNumberVerifiedStatusDTO, PhoneNumberAndVerifiedDTO>()
                                        {
                                            @Override public PhoneNumberAndVerifiedDTO call(
                                                    PhoneNumberVerifiedStatusDTO verifiedStatusDTO)
                                            {
                                                return new PhoneNumberAndVerifiedDTO(
                                                        numberDTO.dialingCountry,
                                                        numberDTO.dialingPrefix,
                                                        numberDTO.typedNumber,
                                                        verifiedStatusDTO.verified);
                                            }
                                        });
                            }
                        })
                        .withLatestFrom(liveBrokerSituationDTOObservable,
                                new Func2<PhoneNumberAndVerifiedDTO, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                                {
                                    @Override public LiveBrokerSituationDTO call(PhoneNumberAndVerifiedDTO phoneNumberAndVerifiedDTO,
                                            LiveBrokerSituationDTO liveBrokerSituationDTO)
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
                                        liveBrokerSituationDTO.kycForm.pickFrom(update);
                                        //noinspection ConstantConditions
                                        populateVerifyMobile((KYCAyondoForm) liveBrokerSituationDTO.kycForm, dialingPrefix, newNumber);
                                        update.setPhonePrimaryCountryCode(phoneNumberAndVerifiedDTO.dialingCountry);
                                        update.setMobileNumber(newNumber);

                                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, update);
                                    }
                                })
                        .subscribe(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                onNext(liveBrokerSituationDTO);
                            }
                        }, new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {

                            }
                        }));

        subscriptions.add(ViewObservable.clicks(dob)
                .withLatestFrom(kycAyondoFormOptionsDTOObservable, new Func2<OnClickEvent, KYCAyondoFormOptionsDTO, KYCAyondoFormOptionsDTO>()
                {
                    @Override public KYCAyondoFormOptionsDTO call(OnClickEvent onClickEvent, KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                    {
                        return kycAyondoFormOptionsDTO;
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
                            Date d = DateUtils.parseString(dob.getText().toString(), KYCAyondoForm.DATE_FORMAT_AYONDO);
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

        subscriptions.add(ViewObservable.clicks(buttonVerifyPhone)
                .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnClickEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(OnClickEvent onClickEvent, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return liveBrokerSituationDTO;
                    }
                })
                .subscribe(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        final int phoneCountryCode = ((CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem()).phoneCountryCode;
                        final String phoneNumberInt = phoneNumber.getText().toString();

                        offerToEnterCode(
                                phoneCountryCode,
                                phoneNumberInt
                        );
                    }
                }, new TimberOnErrorAction1("Failed to present verify phone dialog")));

        subscriptions.add(verifiedPublishSubject.withLatestFrom(liveBrokerSituationDTOObservable,
                new Func2<Pair<Integer, String>, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(Pair<Integer, String> verifiedPhonePair, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        update.setVerifiedMobileNumberDialingPrefix(verifiedPhonePair.first);
                        update.setVerifiedMobileNumber(verifiedPhonePair.second);
                        //noinspection ConstantConditions
                        liveBrokerSituationDTO.kycForm.pickFrom(update);
                        liveServiceWrapper.submitPhoneNumberVerifiedStatus(
                                VerifyPhoneDialogFragment.getFormattedPhoneNumber(verifiedPhonePair.first, verifiedPhonePair.second));
                        populateVerifyMobile((KYCAyondoForm) liveBrokerSituationDTO.kycForm, verifiedPhonePair.first, verifiedPhonePair.second);
                        return null;
                    }
                }).subscribe(
                new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new TimberOnErrorAction1("")));

        return subscriptions;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        verifiedPublishSubject = null;
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

    @MainThread
    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        String userNameText = kycForm.getUserName();
        if (userName != null && userNameText != null && !userNameText.equals(userName.getText().toString()))
        {
            userName.setText(userNameText);
        }

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

    @MainThread
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

    @MainThread
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

    @MainThread
    @NonNull protected KYCAyondoForm populateMobileCountryCode(
            @NonNull final KYCAyondoForm kycForm,
            @NonNull UserProfileDTO currentUserProfile,
            @NonNull List<CountrySpinnerAdapter.DTO> liveCountryDTOs)
    {
        Integer savedMobileNumberDialingPrefix = null;
        if (kycForm.getPhonePrimaryCountryCode() != null)
        {
            savedMobileNumberDialingPrefix = CountryUtil.getPhoneCodePlusLeadingDigits(kycForm.getPhonePrimaryCountryCode());
        }
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
                update.setPhonePrimaryCountryCode(chosenDTO.country);
            }
        }
        return update;
    }

    @MainThread
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

    @MainThread
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

    @MainThread
    protected void offerToEnterCode(
            final int phoneCountryCode,
            final String phoneNumberInt)
    {
        VerifyPhoneDialogFragment.show(REQUEST_VERIFY_PHONE_NUMBER_CODE, this, phoneCountryCode, phoneNumberInt);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_DATE && resultCode == Activity.RESULT_OK)
        {
            Calendar c = DatePickerDialogFragment.getCalendarFromIntent(data);
            dob.setText(DateUtils.getDisplayableDate(c.getTime(), KYCAyondoForm.DATE_FORMAT_AYONDO));
        }
        else if (requestCode == REQUEST_VERIFY_PHONE_NUMBER_CODE && resultCode == Activity.RESULT_OK)
        {
            Pair<Integer, String> verifiedPhoneNumberPair = VerifyPhoneDialogFragment.getVerifiedFromIntent(data);
            if (verifiedPhoneNumberPair != null)
            {
                verifiedPublishSubject.onNext(verifiedPhoneNumberPair);
            }
        }
    }
}
