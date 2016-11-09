package com.androidth.general.fragments.kyc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidth.general.R;
import com.androidth.general.activities.ActivityHelper;
import com.androidth.general.api.competition.EmailVerifiedDTO;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.api.kyc.CountryDocumentTypes;
import com.androidth.general.api.kyc.EmptyKYCForm;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.kyc.ayondo.ProviderQuestionnaireAnswerDto;
import com.androidth.general.api.kyc.ayondo.ProviderQuestionnaireDTO;
import com.androidth.general.api.live.CountryUtil;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.LollipopArrayAdapter;
import com.androidth.general.fragments.kyc.dto.GenderDTO;
import com.androidth.general.fragments.kyc.dto.PhoneNumberAndVerifiedDTO;
import com.androidth.general.fragments.kyc.dto.PhoneNumberDTO;
import com.androidth.general.fragments.kyc.adapter.CountrySpinnerAdapter;
import com.androidth.general.fragments.kyc.DatePickerDialogFragment;
import com.androidth.general.fragments.kyc.VerifyEmailDialogFragment;
import com.androidth.general.fragments.kyc.VerifyPhoneDialogFragment;
import com.androidth.general.fragments.web.BaseWebViewFragment;
import com.androidth.general.models.fastfill.Gender;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.KycServicesRx;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.view.adapter.AdapterViewObservable;
import com.androidth.general.rx.view.adapter.OnItemSelectedEvent;
import com.androidth.general.rx.view.adapter.OnSelectedEvent;
import com.androidth.general.utils.DateUtils;
import com.androidth.general.utils.ExceptionUtils;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.metrics.appsflyer.AppsFlyerConstants;
import com.androidth.general.utils.metrics.appsflyer.THAppsFlyer;
import com.androidth.general.widget.validation.KYCVerifyButton;
import com.androidth.general.widget.validation.VerifyButtonState;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.route.RouteProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.regex.Pattern;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
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

//@Routable({
//        "enrollchallenge/:providerId"
//})
public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @RouteProperty("providerId") protected Integer enrollProviderId;
//    @Inject THRouter thRouter;
    @Inject KycServicesRx kycServices;
    @Inject ProviderUtil providerUtil;

    private static final int PHONE_NUM_MIN_LENGTH = 7;
    private static final int REQUEST_VERIFY_EMAIL_CODE = 2809;

    @LayoutRes private static final int LAYOUT_COUNTRY = R.layout.spinner_live_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_COUNTRY_SELECTED_FLAG = R.layout.spinner_live_country_dropdown_item_selected;
    @LayoutRes private static final int LAYOUT_PHONE_COUNTRY = R.layout.spinner_live_phone_country_dropdown_item;
    @LayoutRes private static final int LAYOUT_PHONE_SELECTED_FLAG = R.layout.spinner_live_phone_country_dropdown_item_selected;
    private static final int REQUEST_PICK_DATE = 2805;
    private static final int REQUEST_VERIFY_PHONE_NUMBER_CODE = 2808;
    private static final String KEY_EXPECTED_SMS_CODE = LiveSignUpStep1AyondoFragment.class.getName() + ".expectedCode";
    private static final String KEY_SMS_ID = LiveSignUpStep1AyondoFragment.class.getName() + ".smsId";

    @Bind(R.id.nric_number) EditText nricNumber;
    @Bind(R.id.sign_up_email) EditText email;
    @Bind(R.id.info_title) Spinner title;
    @Bind(R.id.info_first_name) EditText firstName;
    @Bind(R.id.info_last_name) EditText lastName;

    @Bind(R.id.country_code_spinner) CustomSpinnerSelection spinnerPhoneCountryCode;
    @Bind(R.id.info_phone_number) EditText phoneNumber;
    @Bind(R.id.info_dob) TextView dob;
    @Bind(R.id.step_1_tnc_checkbox) CheckBox tncCheckbox;
    @Bind(R.id.step_1_tnc) TextView termsCond;

    @Bind(R.id.email_verify_button) KYCVerifyButton emailVerifybutton;
    @Bind(R.id.nric_verify_button) KYCVerifyButton nricVerifyButton;
    @Bind(R.id.phone_verify_button) KYCVerifyButton phoneVerifyButton;

    @Bind(R.id.residence_state) Spinner spinnerResidenceState;
    @Bind(R.id.how_you_know_th) Spinner spinnerHowYouKnowTH;
    @Bind(R.id.btn_join_competition) Button joinCompetitionButton;

    @Bind(R.id.tv_signup_survey_1) TextView tvSurvey1;
    @Bind(R.id.tv_signup_survey_2) TextView tvSurvey2;

    @Inject ProviderCacheRx providerCache;
    @Inject ProviderListCacheRx providerListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject protected RequestHeaders requestHeaders;

    protected ProgressDialog loadingFieldProgressDialog;

    private String expectedCode;
    private String smsId;
    private ProviderId providerId;
    private PublishSubject<Pair<Integer, String>> verifiedPublishMobileNumber;
    private PublishSubject<String> verifiedPublishIdNumber;
    private Drawable noErrorIconDrawable;
    private int providerIdInt = 0;
    private boolean hasClickedJoinButton = false;

    private static PublishSubject<String> verifiedPublishEmail;
    private VerifyEmailDialogFragment vedf;
    private Pattern emailPattern;

    Observable<ArrayList<ProviderQuestionnaireDTO>> proQuesList;

    private List<String> cityLists = new ArrayList<>();
    private List<String> howYouKnowThLists = new ArrayList<>();
    LollipopArrayAdapter<String> howYouKnowTHAdapter;
    LollipopArrayAdapter<String> cityListAdapter;
    ArrayList<ProviderQuestionnaireDTO> providerQuestionnaireDTOz;
    ProviderQuestionnaireAnswerDto[] proQueAns = new ProviderQuestionnaireAnswerDto[2];

    HubProxy proxy;
    SignalRManager signalRManager;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        thRouter.inject(this);
        verifiedPublishEmail = PublishSubject.create();
        emailPattern = Pattern.compile(getString(R.string.regex_email_validator));

        verifiedPublishMobileNumber = PublishSubject.create();
        verifiedPublishIdNumber = PublishSubject.create();

        if (savedInstanceState != null)
        {
            expectedCode = savedInstanceState.getString(KEY_EXPECTED_SMS_CODE, null);
            smsId = savedInstanceState.getString(KEY_SMS_ID, null);
        }

        noErrorIconDrawable = getResources().getDrawable(R.drawable.red_alert);
        if (noErrorIconDrawable != null)
        {
            noErrorIconDrawable.setBounds(0,0,0,0);
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_1, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(getActivity());

        userProfileCache.get(currentUserId.toUserBaseKey())
                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<UserProfileDTO>() {
            @Override
            public void call(UserProfileDTO userProfileDTO) {
                // maybe we need to have filter list for email host some where? iOS also don't have this - James
                if(userProfileDTO!=null && userProfileDTO.email!=null && email != null && !userProfileDTO.email.contains("facebook.com"))
                    email.setText(userProfileDTO.email, TextView.BufferType.EDITABLE);
            }
        }, new TimberOnErrorAction1("Live Step 1: user profile cache get() failed."));

        email.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT &&
                    (emailVerifybutton.getState() == VerifyButtonState.PENDING
                            || emailVerifybutton.getState() == VerifyButtonState.FINISH))
            {
                showEmailVerificationPopup();
            }

            return false;
        });

        termsCond.setOnClickListener(click->{
            Bundle args = new Bundle();
            BaseWebViewFragment.putUrl(args, providerUtil.getTermsPage(providerId));

            if (navigator != null)
            {
                navigator.get().pushFragment(BaseWebViewFragment.class, args);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        phoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && phoneVerifyButton.getState() == VerifyButtonState.PENDING)
            {
                offerToEnterCode();
            }

            return false;
        });

        providerIdInt = getProviderId(getArguments());

        if (providerIdInt != 0) {
            this.providerId = new ProviderId(providerIdInt);

            Subscription subscription = providerCache.get(providerId).subscribe(providerIdProviderDTOPair -> {
                ProviderDTO providerDTO = providerIdProviderDTOPair.second;

                if (providerDTO != null) {
                    if (btnNext != null)
                    {
                        if(providerDTO.isUserEnrolled){
                            btnNext.setVisibility(View.VISIBLE);
                            joinCompetitionButton.setVisibility(View.GONE);
                            termsCond.setVisibility(View.GONE);
                            tncCheckbox.setVisibility(View.GONE);
                        }else{
                            btnNext.setVisibility(View.GONE);
                            joinCompetitionButton.setVisibility(View.VISIBLE);
                            joinCompetitionButton.setEnabled(true);
                            termsCond.setVisibility(View.VISIBLE);
                            tncCheckbox.setVisibility(View.VISIBLE);
                        }
                    }

                    if (btnPrev != null)
                    {
                        btnPrev.setVisibility(View.GONE);
                    }

                    setupCompetitionCustomization(providerDTO);
                }
            }, new TimberOnErrorAction1("Live Step 1: provider cache get() failed."));
            onDestroySubscriptions.add(subscription);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingFieldProgressDialog = new ProgressDialog(getContext());
                loadingFieldProgressDialog.setMessage("Loading fields");
//                loadingFieldProgressDialog.setCancelable(false);
                loadingFieldProgressDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_KYC_1);
    }

    @Override protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            final Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {

        List<Subscription> subscriptions = new ArrayList<>();
        providerIdInt = getProviderId(getArguments());
        proQuesList = liveServiceWrapper.getAdditionalQuestionnaires(providerIdInt);
        proQuesList.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Action1<ArrayList<ProviderQuestionnaireDTO>>() {
            @Override
            public void call(ArrayList<ProviderQuestionnaireDTO> providerQuestionnaireDTOs) {
                providerQuestionnaireDTOz = providerQuestionnaireDTOs;
                if(providerQuestionnaireDTOs.size() > 0){
                    String cList = providerQuestionnaireDTOs.get(0).values;
                    String howUKnoTh = providerQuestionnaireDTOs.get(1).values;
                    String delimeter = "\\|";
                    cityLists = Arrays.asList(cList.split(delimeter));
                    howYouKnowThLists = Arrays.asList(howUKnoTh.split(delimeter));

                    if(howYouKnowThLists!=null){
                        howYouKnowTHAdapter = new LollipopArrayAdapter<>(getActivity(), howYouKnowThLists);
                        spinnerHowYouKnowTH.setAdapter(howYouKnowTHAdapter);
                        spinnerHowYouKnowTH.setEnabled(!howYouKnowTHAdapter.isEmpty());

                        if(howYouKnowTHAdapter.isEmpty()){
                            spinnerHowYouKnowTH.setVisibility(View.GONE);

                        }
                    }else{
                        tvSurvey2.setVisibility(View.GONE);
                        spinnerHowYouKnowTH.setVisibility(View.GONE);
                    }

                    if(cityLists!=null){
                        cityListAdapter = new LollipopArrayAdapter<>(getActivity(), cityLists);
                        spinnerResidenceState.setAdapter(cityListAdapter);
                        spinnerResidenceState.setEnabled(!cityListAdapter.isEmpty());

                        if(cityListAdapter.isEmpty()){
                            spinnerResidenceState.setVisibility(View.GONE);
                        }
                    }else{
                        tvSurvey1.setVisibility(View.GONE);
                        spinnerResidenceState.setVisibility(View.GONE);
                    }
                }
                else {
                    spinnerHowYouKnowTH.setVisibility(View.GONE);
                    spinnerResidenceState.setVisibility(View.GONE);
                    tvSurvey2.setVisibility(View.GONE);
                    tvSurvey1.setVisibility(View.GONE);

                }
            }
        }, new TimberOnErrorAction1("Live Step 1: provider questions list fetch failed."));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.live_title_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        title.setAdapter(adapter);

        subscriptions.add(
                WidgetObservable.text(firstName)
                        .withLatestFrom(liveBrokerSituationDTOObservable,
                                (onTextChangeEvent, liveBrokerSituationDTO) -> {
                                    KYCAyondoForm updated = KYCAyondoFormFactory.fromFirstNameEvent(onTextChangeEvent);

                                    return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                                })
                        .subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: first name text field observe failed.")));

        subscriptions.add(
                WidgetObservable.text(lastName).withLatestFrom(liveBrokerSituationDTOObservable,
                        (onTextChangeEvent, liveBrokerSituationDTO) -> {
                            KYCAyondoForm updated = KYCAyondoFormFactory.fromLastNameEvent(onTextChangeEvent);

                            return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                        }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: last name text field observe failed.")));

        subscriptions.add(
                WidgetObservable.text(nricNumber)
                        .doOnNext(onTextChangeEvent -> {
                            if (onTextChangeEvent.text().length() != 12) {
                                nricVerifyButton.setState(VerifyButtonState.BEGIN);
                            } else if (onTextChangeEvent.text().length() == 12) {
                                nricVerifyButton.setState(VerifyButtonState.PENDING);
                            }
                        })
                        .withLatestFrom(liveBrokerSituationDTOObservable,
                                (onTextChangeEvent, liveBrokerSituationDTO) -> {

                                    String currentVerifiedId = ((KYCAyondoForm)liveBrokerSituationDTO.kycForm).getVerifiedIdentificationNumber();
                                    if(onTextChangeEvent.text().toString().equals(currentVerifiedId)){
                                        nricVerifyButton.setState(VerifyButtonState.FINISH);
                                    }

                                    KYCAyondoForm updated = KYCAyondoFormFactory.fromIdentificationNumber(onTextChangeEvent);

                                    return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                                }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: nric text field observe failed.")));

        subscriptions.add(
                WidgetObservable.text(dob).withLatestFrom(liveBrokerSituationDTOObservable,
                        (onTextChangeEvent, liveBrokerSituationDTO) -> {
                            KYCAyondoForm updated = KYCAyondoFormFactory.fromDobEvent(onTextChangeEvent);

                            return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                        }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: DOB selected failed.")));

        subscriptions.add(
                AdapterViewObservable.selects(title).withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnSelectedEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>() {
                    public LiveBrokerSituationDTO call(OnSelectedEvent onSelectedEvent, LiveBrokerSituationDTO liveBrokerSituationDTO) {
                        KYCAyondoForm updated = KYCAyondoFormFactory.fromTitleEvent(onSelectedEvent);

                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                    }
                }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: gender selected failed.")));
        /*AdapterViewObservable.selects(spinnerHowYouKnowTH).withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnSelectedEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>() {
            public LiveBrokerSituationDTO call(OnSelectedEvent onSelectedEvent, LiveBrokerSituationDTO liveBrokerSituationDTO) {
                KYCAyondoForm updated = KYCAyondoFormFactory.fromHowYouKnowTHEvent(onSelectedEvent);
                return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
            }
        }).subscribe(this::onNext);*/

        subscriptions.add(
                AdapterViewObservable.selects(spinnerPhoneCountryCode).withLatestFrom(liveBrokerSituationDTOObservable,
                        new Func2<OnSelectedEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                        {
                            public LiveBrokerSituationDTO call(OnSelectedEvent onSelectedEvent, LiveBrokerSituationDTO liveBrokerSituationDTO) {
                                KYCAyondoForm updated = KYCAyondoFormFactory.fromPhoneCountryCode(onSelectedEvent);

                                return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                            }
                        }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: phone country selected failed.")));

        subscriptions.add(
                AdapterViewObservable.selects(spinnerHowYouKnowTH).withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnSelectedEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>() {
                    public LiveBrokerSituationDTO call(OnSelectedEvent onSelectedEvent, LiveBrokerSituationDTO liveBrokerSituationDTO) {
                        ProviderQuestionnaireDTO proQuesDto = providerQuestionnaireDTOz.get(1);
                        Object selectedResidency = onSelectedEvent.parent.getItemAtPosition(((OnItemSelectedEvent) onSelectedEvent).position);
                        proQueAns[1] = new ProviderQuestionnaireAnswerDto(selectedResidency.toString(),proQuesDto.id, proQuesDto.question );
                        KYCAyondoForm updated = KYCAyondoFormFactory.additionalDataEvent(proQueAns);
                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);

                    }
                }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: how you know th selected failed.")));

        subscriptions.add(
                AdapterViewObservable.selects(spinnerResidenceState).withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnSelectedEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>() {
                    public LiveBrokerSituationDTO call(OnSelectedEvent onSelectedEvent, LiveBrokerSituationDTO liveBrokerSituationDTO) {
                        ProviderQuestionnaireDTO proQuesDto = providerQuestionnaireDTOz.get(0);
                        Object selectedResidency = onSelectedEvent.parent.getItemAtPosition(((OnItemSelectedEvent) onSelectedEvent).position);
                        proQueAns[0] = new ProviderQuestionnaireAnswerDto(selectedResidency.toString(),proQuesDto.id, proQuesDto.question );
                        KYCAyondoForm updated = KYCAyondoFormFactory.additionalDataEvent(proQueAns);
                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);

                    }
                }).subscribe(this::onNext, new TimberOnErrorAction1("Live Step 1: residence state selected failed.")));


        subscriptions.add(
                // clicks observable
                ViewObservable.clicks(nricVerifyButton)
                        .withLatestFrom(liveBrokerSituationDTOObservable, (onClickEvent, liveBrokerSituationDTO) -> liveBrokerSituationDTO)
                        .doOnError(throwable1 -> {
                            Log.v(getTag(), "NRIC ERROR");
                        })
                        .subscribe((LiveBrokerSituationDTO liveBrokerSituationDTO) -> {
                            switch (nricVerifyButton.getState()) {
                                case BEGIN:
                                    nricNumber.setError("NRIC must be 12 digits.", noErrorIconDrawable);
                                    nricVerifyButton.setState(VerifyButtonState.ERROR);
                                    break;
                                case PENDING:
                                case VALIDATE:
                                    if (liveBrokerSituationDTO.kycForm instanceof KYCAyondoForm) {
                                        KYCAyondoForm form = (KYCAyondoForm)liveBrokerSituationDTO.kycForm;
                                        ProgressDialog progress = new ProgressDialog(getContext());
                                        progress.setMessage("Verifying NRIC...");
                                        progress.show();
                                        String str = "";
                                        if(form.getNationality()==null){
                                            str = "MY";
                                        }
                                        else {
                                            try {
                                                str = form.getNationality().getAlpha2();
                                            }
                                            catch (Exception e){

                                            }

                                        }

                                        liveServiceWrapper.documentsForCountry(str).subscribe(
                                                countryDocumentTypes -> {
                                                    // possible have multiple items, currently UI is hardcoded for NRIC, I(James) still not sure how to handle this things because the form is not totally dynamic...
                                                    // need to thing some ways to handle all the cases and make it dynamic.

                                                    for (CountryDocumentTypes countryDocumentType : countryDocumentTypes)
                                                    {
                                                        if (countryDocumentType.validation != null)
                                                        {
                                                            Map queryParameters = new HashMap<String, String>();
                                                            queryParameters.put(LiveServiceWrapper.PROVIDER_ID, Integer.toString(getProviderId(getArguments())));
                                                            queryParameters.put(LiveServiceWrapper.INPUT, nricNumber.getText().toString());
                                                            liveServiceWrapper.validateData(countryDocumentType.validation, queryParameters)
                                                                    .subscribeOn(Schedulers.newThread())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(aBoolean -> {
                                                                        if (aBoolean.equals(true)) {
                                                                            nricVerifyButton.setState(VerifyButtonState.FINISH);
                                                                            nricNumber.setError(null);

                                                                            if (verifiedPublishIdNumber != null) {
                                                                                //update KYC form
                                                                                verifiedPublishIdNumber.onNext(queryParameters.get(LiveServiceWrapper.INPUT).toString());
                                                                            }

                                                                            if (hasClickedJoinButton) {
                                                                                onClickedJoinButton();
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getActivity(), "NRIC verification failed", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        progress.dismiss();
                                                                    }, new Action1<Throwable>() {
                                                                        @Override
                                                                        public void call(Throwable throwable) {
                                                                            progress.dismiss();
                                                                            String errorMessage = "!!!";
                                                                            if(throwable!=null){
                                                                                errorMessage = ExceptionUtils.getStringElementFromThrowable(throwable, "Message");
                                                                            }

                                                                            nricVerifyButton.setState(VerifyButtonState.ERROR);
                                                                            nricNumber.setError(errorMessage, noErrorIconDrawable);
                                                                            LiveSignUpStep1AyondoFragment.this.requestFocusAndShowKeyboard(nricNumber);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }, throwable -> {
                                                    THToast.show(throwable.getMessage());
                                                    progress.dismiss();
                                                });
                                    }
                                    break;
                            }
                        }, new TimberOnErrorAction1("Live Step 1: nric verify button clicked failed.")));

        subscriptions.add(
                ViewObservable.clicks(phoneVerifyButton)
                        .subscribe(onClickEvent -> {
                            switch (phoneVerifyButton.getState()) {
                                case BEGIN:
                                case ERROR:
                                    phoneNumber.setError("Mobile number cannot less than 8 digits.", noErrorIconDrawable);
                                    phoneVerifyButton.setState(VerifyButtonState.ERROR);
                                    break;
                                case PENDING:
                                case VALIDATE:
                                    offerToEnterCode();
                            }
                        }, new TimberOnErrorAction1("Live Step 1: phone verify button clicked failed.")));


        //AdapterViewObservable.selects(spinnerNationality).subscribe(KYCAyondoFormFactory::fromNationalityEvent);
        //AdapterViewObservable.selects(spinnerResidency).subscribe(KYCAyondoFormFactory::fromResidencyEvent);

        subscriptions.add(Observable.combineLatest(
                liveBrokerSituationDTOObservable
                        .take(1)
                        .observeOn(AndroidSchedulers.mainThread()),
                kycAyondoFormOptionsDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<KYCAyondoFormOptionsDTO, CountryDTOForSpinner>()
                        {
                            @Override public CountryDTOForSpinner call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                            {
                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                String currentCountryCode = sharedPref.getString(getString(R.string.key_preference_country_code), "MY");
                                Country c = Country.AD;
                                //Doesnt matter which Country enum we use
                                return new CountryDTOForSpinner(getActivity(), kycAyondoFormOptionsDTO, c.getCountryByCode(currentCountryCode));
                            }
                        })
                        .distinctUntilChanged()
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
                                spinnerPhoneCountryCode.setSelection(0);
                                spinnerPhoneCountryCode.setPrompt("Country Code");

                                //CountrySpinnerAdapter residencyAdapter =
                                //        new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
                                //residencyAdapter.addAll(options.allowedResidencyCountryDTOs);
                                //spinnerResidency.setAdapter(residencyAdapter);
                                //spinnerResidency.setEnabled(options.allowedResidencyCountryDTOs.size() > 1);
                                //
                                //CountrySpinnerAdapter nationalityAdapter =
                                //        new CountrySpinnerAdapter(getActivity(), LAYOUT_COUNTRY_SELECTED_FLAG, LAYOUT_COUNTRY);
                                //nationalityAdapter.addAll(options.allowedNationalityCountryDTOs);
                                //spinnerNationality.setAdapter(nationalityAdapter);
                                //spinnerNationality.setEnabled(options.allowedNationalityCountryDTOs.size() > 1);

                                /*LollipopArrayAdapter<String> residenceStateAdapter = new LollipopArrayAdapter<>(
                                        getActivity(), options.residenceStateList);
                                spinnerResidenceState.setAdapter(residenceStateAdapter);
                                spinnerResidenceState.setEnabled(options.residenceStateList.size() > 1);*/

//                                liveServiceWrapper.getAdditionalQuestionnaires(providerIdInt).subscribe(new Action1<ArrayList<ProviderQuestionnaireDTO>>() {
//                                    @Override
//                                    public void call(ArrayList<ProviderQuestionnaireDTO> providerQuestionnaireDTO) {
//                                        for(ProviderQuestionnaireDTO dto: providerQuestionnaireDTO){
////TODO by Jeff
//                                        }
//
//                                    }
//                                });


                                /*LollipopArrayAdapter<String> howYouKnowTHAdapter = new LollipopArrayAdapter<>(
                                        getActivity(), options.howYouKnowTHList);
                                spinnerHowYouKnowTH.setAdapter(howYouKnowTHAdapter);
                                spinnerHowYouKnowTH.setEnabled(options.howYouKnowTHList.size() > 1);*/
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
                        LiveBrokerSituationDTO latestDTO = situation;

                        if (situation.kycForm instanceof EmptyKYCForm) {
                            KYCAyondoForm defaultForm = new KYCAyondoForm();
                            defaultForm.pickFromWithDefaultValues(currentUserProfile);

                            latestDTO = new LiveBrokerSituationDTO(situation.broker, defaultForm);
                        }

                        if (latestDTO.kycForm != null)
                        {
                            ProviderDTO providerDTO = providerCache.getCachedValue(new ProviderId(getProviderId(getArguments())));

                            if (providerDTO != null) {
                                if (providerDTO.isStrictlyForProviderCountry && providerDTO.providerCountries.length == 1) {
                                    KYCAyondoForm updated = new KYCAyondoForm();
                                    updated.setNationality(CountryCode.getByCode(providerDTO.providerCountries[0]));
                                    updated.setResidency(CountryCode.getByCode(providerDTO.providerCountries[0]));

                                    //latestDTO = new LiveBrokerSituationDTO(latestDTO.broker, updated);
                                    onNext(new LiveBrokerSituationDTO(latestDTO.broker, updated));
                                }
                            }

                            populate((KYCAyondoForm) latestDTO.kycForm);
                            populateGender((KYCAyondoForm) latestDTO.kycForm, options.genders);
                            populateMobileCountryCode((KYCAyondoForm) latestDTO.kycForm, currentUserProfile,
                                    options.allowedMobilePhoneCountryDTOs);
//                            populateNationality((KYCAyondoForm) latestDTO.kycForm, currentUserProfile, options.allowedNationalityCountryDTOs);
//                            populateResidency((KYCAyondoForm) latestDTO.kycForm, currentUserProfile, options.allowedResidencyCountryDTOs);

                        }

                        dismissLocalProgressDialog();
                        return latestDTO;
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
                            @Override public void call(PhoneNumberDTO phoneNumberDTO)
                            {
                                //buttonVerifyPhone.setText(R.string.verify);
                                //buttonVerifyPhone.setEnabled(false);
                                //if (isValidPhoneNumber(phoneNumberDTO))
                                //{
                                //    buttonVerifyPhone.setBackgroundResource(R.drawable.basic_green_selector);
                                //    buttonVerifyPhone.setEnabled(true);
                                //}

                                if (isValidPhoneNumber(phoneNumberDTO)) {
                                    phoneVerifyButton.setState(VerifyButtonState.PENDING);
                                } else {
                                    phoneVerifyButton.setState(VerifyButtonState.BEGIN);
                                }
                            }
                        })
                        .distinctUntilChanged()
                        .doOnNext(new Action1<PhoneNumberDTO>()
                        {
                            @Override public void call(PhoneNumberDTO phoneNumberDTO)
                            {
                                smsId = null;
                                expectedCode = null;
                            }
                        })
                        .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<PhoneNumberDTO, LiveBrokerSituationDTO, PhoneNumberDTO>()
                        {
                            @Override public PhoneNumberDTO call(PhoneNumberDTO phoneNumberDTO, LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                KYCAyondoForm update = new KYCAyondoForm();

                                String newNumber = phoneNumberDTO.typedNumber;

                                update.setPhonePrimaryCountryCode(phoneNumberDTO.dialingCountry);
                                update.setMobileNumber(newNumber);

                                onNext(new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, update));

                                return phoneNumberDTO;
                            }
                        })
                        .filter(new Func1<PhoneNumberDTO, Boolean>()
                        {
                            @Override public Boolean call(PhoneNumberDTO phoneNumberDTO)
                            {
                                return isValidPhoneNumber(phoneNumberDTO);
                            }
                        })
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
                                        populateVerifyMobile((KYCAyondoForm) liveBrokerSituationDTO.kycForm, phoneNumberAndVerifiedDTO);

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
                        setupDatePickerDialog(calendar, selected);
                    }
                }, new TimberOnErrorAction1("Failed to listen to DOB clicks")));

        //subscriptions.add(ViewObservable.clicks(buttonVerifyPhone)
        //        .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnClickEvent, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
        //        {
        //            @Override public LiveBrokerSituationDTO call(OnClickEvent onClickEvent, LiveBrokerSituationDTO liveBrokerSituationDTO)
        //            {
        //                return liveBrokerSituationDTO;
        //            }
        //        })
        //        .subscribe(new Action1<LiveBrokerSituationDTO>()
        //        {
        //            @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
        //            {
        //                offerToEnterCode();
        //            }
        //        }, new TimberOnErrorAction1("Failed to present verify phone dialog")));

        subscriptions.add(verifiedPublishMobileNumber.withLatestFrom(liveBrokerSituationDTOObservable,
                new Func2<Pair<Integer, String>, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(Pair<Integer, String> verifiedPhonePair, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        update.setVerifiedMobileNumberDialingPrefix(verifiedPhonePair.first);
                        update.setVerifiedMobileNumber(verifiedPhonePair.second);
                        //noinspection ConstantConditions

                        liveServiceWrapper.submitPhoneNumberVerifiedStatus(
                                VerifyPhoneDialogFragment.getFormattedPhoneNumber(verifiedPhonePair.first, verifiedPhonePair.second));
                        //buttonVerifyPhone.setEnabled(false);
                        //buttonVerifyPhone.setText(R.string.verified);
                        phoneVerifyButton.setState(VerifyButtonState.FINISH);
                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, update);
                    }
                }).subscribe(
                new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new TimberOnErrorAction1("Failed to update verified mobile number")));

        subscriptions.add(verifiedPublishIdNumber.withLatestFrom(liveBrokerSituationDTOObservable,
                new Func2<String, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(String idNumber, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        update.setVerifiedIdentificationNumber(idNumber);
                        //noinspection ConstantConditions

                        nricVerifyButton.setState(VerifyButtonState.FINISH);
                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, update);
                    }
                }).subscribe(
                new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new TimberOnErrorAction1("Failed to update verified mobile number")));

        subscriptions.add(verifiedPublishEmail.withLatestFrom(liveBrokerSituationDTOObservable,
                new Func2<String, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(String verifiedEmail, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        update.setVerifiedEmailAddress(verifiedEmail);
                        return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, update);
                    }
                }).subscribe(
                new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new TimberOnErrorAction1("Failed to update email address")));

        subscriptions.add(WidgetObservable.text(email)
                .doOnNext(onTextChangeEvent -> {
                    if (isValidEmail(onTextChangeEvent.text().toString())) {
                        emailVerifybutton.setState(VerifyButtonState.PENDING);
                    } else {
                        emailVerifybutton.setState(VerifyButtonState.BEGIN);
                    }
                })
                .withLatestFrom(liveBrokerSituationDTOObservable,
                        (onTextChangeEvent, liveBrokerSituationDTO) -> {

                            KYCAyondoForm updated = new KYCAyondoForm();

                            if (liveBrokerSituationDTO.kycForm instanceof KYCAyondoForm) {
                                String currentVerifiedEmail = ((KYCAyondoForm)liveBrokerSituationDTO.kycForm).getVerifiedEmailAddress();

                                if(currentVerifiedEmail!=null && currentVerifiedEmail.equals(onTextChangeEvent.text().toString())){
                                    emailVerifybutton.setState(VerifyButtonState.FINISH);
                                }

                                updated = KYCAyondoFormFactory.fromEmailEvent(onTextChangeEvent);
                            }

                            return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);

                        }).subscribe(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.d(throwable.getMessage());
                    }
                }));

        subscriptions.add(ViewObservable.clicks(emailVerifybutton)
                .subscribe(new Action1<OnClickEvent>() {
                    @Override
                    public void call(OnClickEvent onClickEvent) {switch (emailVerifybutton.getState()) {
                        case BEGIN:
                            email.setError(LiveSignUpStep1AyondoFragment.this.getString(R.string.validation_incorrect_pattern_email), noErrorIconDrawable);
                            emailVerifybutton.setState(VerifyButtonState.ERROR);
                            break;
                        case PENDING:
                        case VALIDATE:
                            validateEmail();
                            break;
                    }
                    }
                }, new TimberOnErrorAction1("Live Step 1: email verify button clicked failed.")));

        Log.v("ayondoStep1", "Subscriptions final "+subscriptions.size());
        return subscriptions;
    }

    private String getStringFromResponse(Response response) {
        TypedInput body = response.getBody();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }

            // Prints the correct String representation of body.
            return out.toString().replace("\"", "").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @MainThread
    protected void validateEmail()
    {
        final String email = this.email.getText().toString();

        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false);
        progress.show();

        liveServiceWrapper.verifyEmail(currentUserId.get(), email, providerIdInt)
                .subscribe(new Action1<Response>()
                {
                    @Override public void call(Response response)
                    {
                        progress.dismiss();

                        String responseString = getStringFromResponse(response);
                        if (responseString != null)
                        {
                            if (responseString.toLowerCase().equals(("Verified").toLowerCase())) {
                                verifiedPublishEmail.onNext(email);
                                updateEmailVerification(email, null, true);
                            } else if (responseString.toLowerCase().equals(("True").toLowerCase())) {
                                showEmailVerificationPopup();
                            }
                        }
                    }
                }, throwable -> {
                    THToast.show(throwable.getMessage());
                    progress.dismiss();
                });

        setupSignalR(email);
    }

    private boolean isValidEmail(String email)
    {
        return emailPattern.matcher(email).matches();
    }

    private void showEmailVerificationPopup() {
        vedf = VerifyEmailDialogFragment.show(REQUEST_VERIFY_EMAIL_CODE, this, currentUserId.get(), email.getText().toString(), this.providerIdInt);
    }

    public void setupSignalR(String emailAddress) {

        signalRManager = new SignalRManager(requestHeaders, currentUserId, LiveNetworkConstants.CLIENT_NOTIFICATION_HUB_NAME);
        signalRManager.getCurrentProxy().on("SetValidationStatus", new SubscriptionHandler1<EmailVerifiedDTO>() {
            @Override
            public void run(EmailVerifiedDTO emailVerifiedDTO) {
                if(emailVerifiedDTO.isValidated()){
                    updateEmailVerification(emailAddress, null, true);
                }
            }
        }, EmailVerifiedDTO.class);

        signalRManager.startConnectionNow();
    }

    @MainThread
    public void updateEmailVerification(String emailAddress, String errorMessage, boolean isSuccess){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(vedf != null && vedf.isVisible()){
                    try{
                        vedf.dismiss();
                    }catch (Exception e){
                        //might be closed or not in view
                    }
                }
                if(isSuccess){
                    emailVerifybutton.setState(VerifyButtonState.FINISH);
                    verifiedPublishEmail.onNext(emailAddress);

                    if(hasClickedJoinButton){
                        onClickedJoinButton();
                    }
                }else{
                    if(errorMessage!=null){
                        email.setError(errorMessage, noErrorIconDrawable);
                        requestFocusAndShowKeyboard(email);
                    }
                }
            }
        });
    }

    @MainThread
    private void dismissLocalProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(getTag(), "Jeff loading dialog dismiss");
                loadingFieldProgressDialog.dismiss();
            }
        });
    }

    private boolean isValidPhoneNumber(PhoneNumberDTO phoneNumberDTO)
    {
        return phoneNumberDTO.dialingPrefix > 0 && phoneNumberDTO.typedNumber.length() > PHONE_NUM_MIN_LENGTH;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        verifiedPublishMobileNumber = null;
        verifiedPublishIdNumber = null;
        verifiedPublishEmail = null;
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXPECTED_SMS_CODE, expectedCode);
        outState.putString(KEY_SMS_ID, smsId);
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

        String nricNumberText = kycForm.getIdentificationNumber();
        if (this.nricNumber != null && nricNumberText != null)
        {
            nricNumber.setText(nricNumberText);
            if(nricNumber.getText().toString().equals(kycForm.getVerifiedIdentificationNumber())){
                nricVerifyButton.setState(VerifyButtonState.FINISH);
            }
        }

        String emailText = kycForm.getEmail();
        // maybe we need to have filter list for email host some where? iOS also don't have this - James
        if (email != null && emailText != null && !emailText.contains("facebook.com") /* && !emailText.equals(email.getText().toString())*/)
        {
            email.setText(emailText);
            String currentVerifiedEmail = kycForm.getVerifiedEmailAddress();
            if(currentVerifiedEmail!=null && currentVerifiedEmail.equals(email.getText().toString())){
                emailVerifybutton.setState(VerifyButtonState.FINISH);
            }
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

        dismissLocalProgressDialog();
    }

    @MainThread
    protected void populateVerifyMobile(@NonNull KYCAyondoForm kycForm, PhoneNumberDTO phoneNumberDTO)
    {
        //if (buttonVerifyPhone != null)
        //{
        //    boolean verified = Integer.valueOf(phoneNumberDTO.dialingPrefix).equals(kycForm.getVerifiedMobileNumberDialingPrefix())
        //            && phoneNumberDTO.typedNumber.equals(kycForm.getVerifiedMobileNumber());
        //    buttonVerifyPhone.setEnabled(!verified && isValidPhoneNumber(phoneNumberDTO));
        //    buttonVerifyPhone.setText(verified ? R.string.verified : R.string.verify);
        //}

        boolean verified = Integer.valueOf(phoneNumberDTO.dialingPrefix).equals(kycForm.getVerifiedMobileNumberDialingPrefix())
                    && phoneNumberDTO.typedNumber.equals(kycForm.getVerifiedMobileNumber());

        if (verified) {
            phoneVerifyButton.setState(VerifyButtonState.FINISH);
        } else {
            if (isValidPhoneNumber(phoneNumberDTO)) {
                phoneVerifyButton.setState(VerifyButtonState.PENDING);
            } else {
                phoneVerifyButton.setState(VerifyButtonState.BEGIN);
            }
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
        //candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
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
            }
            catch (Exception e)
            {
                candidates = new ArrayList<>();
            }
        }
        else
        {
            candidates = new ArrayList<>();
        }

        KYCAyondoForm update = new KYCAyondoForm();
        //candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        //Integer index = setSpinnerOnFirst(spinnerNationality, candidates, liveCountryDTOs);
        //if (savedNationality == null)
        //{
        //    CountrySpinnerAdapter.DTO chosenDTO;
        //    if (index != null)
        //    {
        //        chosenDTO = liveCountryDTOs.get(index);
        //    }
        //    else
        //    {
        //        chosenDTO = (CountrySpinnerAdapter.DTO) spinnerNationality.getSelectedItem();
        //    }
        //
        //    if (chosenDTO != null)
        //    {
        //        update.setNationality(CountryCode.getByCode(chosenDTO.country.name()));
        //    }
        //}
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
            }
            catch (Exception e)
            {
                candidates = new ArrayList<>();
            }
        }
        else
        {
            candidates = new ArrayList<>();
        }

        KYCAyondoForm update = new KYCAyondoForm();
        //candidates.addAll(getFilteredByCountries(liveCountryDTOs, kycForm, currentUserProfile));
        //Integer index = setSpinnerOnFirst(spinnerResidency, candidates, liveCountryDTOs);
        //if (savedResidency == null)
        //{
        //    CountrySpinnerAdapter.DTO chosenDTO;
        //    if (index != null)
        //    {
        //        chosenDTO = liveCountryDTOs.get(index);
        //    }
        //    else
        //    {
        //        chosenDTO = (CountrySpinnerAdapter.DTO) spinnerResidency.getSelectedItem();
        //    }
        //
        //    if (chosenDTO != null)
        //    {
        //        update.setResidency(CountryCode.getByCode(chosenDTO.country.name()));
        //    }
        //}
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
    protected void offerToEnterCode()
    {
       final int phoneCountryCode =
                ((CountrySpinnerAdapter.DTO) spinnerPhoneCountryCode.getSelectedItem()).phoneCountryCode;
        final String phoneNumberInt = phoneNumber.getText().toString();

        if (phoneCountryCode > 0 && phoneNumberInt.length() > PHONE_NUM_MIN_LENGTH)
        {
            if (expectedCode == null)
            {
                expectedCode = String.format("%04d", Math.abs(new Random(System.nanoTime()).nextInt() % 10000));
            }

            VerifyPhoneDialogFragment.show(REQUEST_VERIFY_PHONE_NUMBER_CODE, this, phoneCountryCode, phoneNumberInt, expectedCode);

            //buttonVerifyPhone.setText(R.string.enter_code);
            //buttonVerifyPhone.setBackgroundResource(R.drawable.basic_red_selector);
        }
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
                verifiedPublishMobileNumber.onNext(verifiedPhoneNumberPair);
            }
            if(hasClickedJoinButton){
                onClickedJoinButton();
            }

        } else if (requestCode == REQUEST_VERIFY_EMAIL_CODE && resultCode == Activity.RESULT_OK) {
            String verifiedEmail = VerifyEmailDialogFragment.getVerifiedFromIntent(data);

            if(data.hasExtra("VerificationEmailError")){
                updateEmailVerification(data.getStringExtra("VerifiedEmailAddress"), data.getStringExtra("VerificationEmailError"), false);
            } else {
                if (verifiedEmail != null)
                {
                    verifiedPublishEmail.onNext(verifiedEmail);
                }
                updateEmailVerification(data.getStringExtra("VerifiedEmailAddress"), null, true);
            }
        }
    }

    public String getSmsId()
    {
        return smsId;
    }

    public void setSmsId(String smsId)
    {
        this.smsId = smsId;
    }

    private Boolean isAllInputValidated() {

        if (firstName.length() == 0) {
            firstName.setError("Must not be empty", noErrorIconDrawable);
            requestFocusAndShowKeyboard(firstName);
            return false;
        }else{
            firstName.setError(null);
        }
        if(lastName.length() == 0){
            lastName.setError("Must not be empty", noErrorIconDrawable);
            requestFocusAndShowKeyboard(lastName);
            return false;
        }else{
            lastName.setError(null);
        }
        if (nricNumber.length() != 12) {
            nricNumber.setError("NRIC must be 12 digits.", noErrorIconDrawable);
            requestFocusAndShowKeyboard(nricNumber);
            return false;
        }else{
            nricNumber.setError(null);
        }
        if(!isValidEmail(email.getText().toString())) {
            email.setError(LiveSignUpStep1AyondoFragment.this.getString(R.string.validation_incorrect_pattern_email), noErrorIconDrawable);
            requestFocusAndShowKeyboard(email);
            return false;
        }else{
            email.setError(null);
        }

        if(dob.length() == 0){
            Snackbar.make(dob, "Date of birth must not be empty", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(!tncCheckbox.isChecked()){
            Snackbar.make(termsCond, "Please agree to terms and conditions", Snackbar.LENGTH_LONG).show();
            return false;
        }

        //make this automatic
        if(nricVerifyButton.getState() != VerifyButtonState.FINISH){
            hasClickedJoinButton = true;
            nricVerifyButton.performClick();
            return false;
        }else{
            nricNumber.setError(null);
        }

        if(phoneVerifyButton.getState() != VerifyButtonState.FINISH){
            hasClickedJoinButton = true;
            phoneVerifyButton.performClick();
            return false;
        }else{
            phoneNumber.setError(null);
        }

        if (emailVerifybutton.getState() != VerifyButtonState.FINISH) {
            hasClickedJoinButton = true;
            emailVerifybutton.performClick();
            return false;
        }else{
            email.setError(null);
        }

        return true;

    }

    private void requestFocusAndShowKeyboard(EditText textView) {
        if(textView.requestFocus()){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }
    }



    @SuppressWarnings("unused")
    @OnClick(R.id.btn_join_competition)
    public void onClickedJoinButton() {

        if (!isAllInputValidated()) {
            updateDB(false, 1);
            return;
        }
        updateDB(true, 1);

        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false);
        progress.show();

        KYCForm kycForm = liveBrokerSituationPreference.get().kycForm;
        KYCAyondoForm ayondoForm = (KYCAyondoForm)kycForm;
        if (ayondoForm != null)
        {
            String email_text = email.getText().toString();
            String phoneNumber_text = phoneNumber.getText().toString();
            String firstName_text = firstName.getText().toString();
            String lastName_text = lastName.getText().toString();
            String nric_text = nricNumber.getText().toString();
            String dob_text = dob.getText().toString();
            Country phoneCountryCode = ((CountrySpinnerAdapter.DTO)spinnerPhoneCountryCode.getSelectedItem()).country;
            Gender gender = Gender.values()[title.getSelectedItemPosition()];

            ayondoForm.setEmail(email_text);
            ayondoForm.setVerifiedEmailAddress(email_text);
            ayondoForm.setFirstName(firstName_text);
            ayondoForm.setLastName(lastName_text);
            ayondoForm.setMobileNumber(phoneNumber_text);
            ayondoForm.setVerifiedMobileNumber(phoneNumber_text);
            ayondoForm.setPhonePrimaryCountryCode(phoneCountryCode);
            ayondoForm.setGender(gender);
            ayondoForm.setIdentificationNumber(nric_text);
            ayondoForm.setDob(dob_text);
        }

        liveServiceWrapper.createOrUpdateLead(getProviderId(getArguments()), ayondoForm)
                .subscribe(
                brokerApplicationDTO -> {
                    liveServiceWrapper.enrollCompetition(providerId.key, currentUserId.get())
                            .subscribe(aBoolean -> {
                                Timber.d("Boolean result " +aBoolean);

                                if (aBoolean) {
                                    ProviderListKey key = new ProviderListKey();
//                                    providerListCache.fetch(key).subscribe(new Action1<ProviderDTOList>() {
//                                        @Override
//                                        public void call(ProviderDTOList providerDTOs) {
//                                            Log.v(getTag(), "Dismissing for launchboard1");
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    //run on main thread to make sure, as loading takes too long for unknown reason -Jeff
//                                                    Log.v(getTag(), "Dismissing for launchboard");
//                                                    progress.dismiss();
//                                                    ActivityHelper.launchDashboard(LiveSignUpStep1AyondoFragment.this.getActivity(), Uri.parse("tradehero://providers/" + providerId.key));
//                                                    THAppsFlyer.sendTrackingWithEvent(LiveSignUpStep1AyondoFragment.this.getActivity(), AppsFlyerConstants.KYC_1_SUBMIT, null);
//                                                }
//                                            });
//                                        }
                                    //                                    }, throwable -> progress.dismiss());

                                    Timber.d("Result: "+key.toString());
                                    ProviderDTOList dtoList = providerListCache.getCachedValue(key);
                                    for (ProviderDTO dto : dtoList) {
                                        Timber.d("Searching "+dto.id);
                                        if (dto.id == providerId.key) {
                                            Timber.d("Found key: "+dto.id);
                                            dto.isUserEnrolled = true;
                                            break;
                                        }
                                    }
                                    progress.dismiss();
                                    ActivityHelper.launchDashboard(LiveSignUpStep1AyondoFragment.this.getActivity(), Uri.parse("tradehero://providers/" + providerId.key));
                                    THAppsFlyer.sendTrackingWithEvent(LiveSignUpStep1AyondoFragment.this.getActivity(), AppsFlyerConstants.KYC_1_SUBMIT, null);

                                }else{
                                    progress.dismiss();
                                    Toast.makeText(getActivity(), "Joining competition failed", Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> progress.dismiss());
                }, throwable -> {
                    THToast.show(throwable.getMessage());
                    progress.dismiss();
                });
    }

    private void setupCompetitionCustomization(ProviderDTO providerDTO){
        switch (providerDTO.id){
            case 55:
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(10);
                phoneNumber.setFilters(filterArray);
                break;
            default:
                break;
        }
    }

    private void setupDatePickerDialog(Calendar maxDate, Calendar selected){
        DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance(maxDate, selected);
        dpf.setTargetFragment(LiveSignUpStep1AyondoFragment.this, REQUEST_PICK_DATE);
        dpf.show(getChildFragmentManager(), dpf.getClass().getName());
    }

    @Override
    protected void onNextButtonEnabled(KYCAyondoForm kycForm) {
        if(kycForm.getFirstName() != null
                && kycForm.getLastName() != null
                && kycForm.getEmail() != null
                && kycForm.getMobileNumber() != null
                && kycForm.getIdentificationNumber() != null
                && !kycForm.getFirstName().isEmpty()
                && !kycForm.getLastName().isEmpty()
                && !kycForm.getEmail().isEmpty()
                && !kycForm.getMobileNumber().isEmpty()
                && !kycForm.getIdentificationNumber().isEmpty()){

            if (btnNext != null)
            {
                btnNext.setEnabled(true);
            }
        }else{
            if (btnNext != null)
            {
                btnNext.setEnabled(false);
            }
        }

    }
}
