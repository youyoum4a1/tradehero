package com.androidth.general.fragments.live.ayondo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.androidth.general.R;
import com.androidth.general.api.competition.EmailVerifiedDTO;
import com.androidth.general.api.competition.JumioVerifyBodyDTO;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.BrokerDocumentUploadResponseDTO;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.exception.THException;
import com.androidth.general.fragments.base.LollipopArrayAdapter;
import com.androidth.general.fragments.live.VerifyEmailDialogFragment;
import com.androidth.general.fragments.settings.ImageRequesterUtil;
import com.androidth.general.models.fastfill.FastFillExceptionUtil;
import com.androidth.general.models.fastfill.FastFillUtil;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ResidenceScannedDocumentType;
import com.androidth.general.models.fastfill.ScanReference;
import com.androidth.general.models.fastfill.ScannedDocument;
import com.androidth.general.models.fastfill.jumio.NetverifyFastFillUtil;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.rx.view.adapter.AdapterViewObservable;
import com.androidth.general.rx.view.adapter.OnSelectedEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.androidth.general.utils.ProgressDialogUtil;
import com.androidth.general.widget.DocumentActionWidget;
import com.androidth.general.widget.DocumentActionWidgetAction;
import com.androidth.general.widget.DocumentActionWidgetActionType;
import com.androidth.general.widget.DocumentActionWidgetObservable;
import com.androidth.general.widget.validation.KYCVerifyButton;
import com.androidth.general.widget.validation.VerifyButtonState;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.data.document.NVDocumentType;
import com.neovisionaries.i18n.CountryCode;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.regex.Pattern;
import javax.inject.Inject;

import butterknife.Bind;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class LiveSignUpStep5AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int INDEX_CHOICE_FROM_CAMERA = 0;
    private static final int INDEX_CHOICE_FROM_LIBRARY = 1;
    private static final int INDEX_VIEW_CREATE_BUTTON = 0;
    private static final int INDEX_VIEW_SUBMIT_BUTTON = 1;
    private static final int REQUEST_VERIFY_EMAIL_CODE = 2809;

    @Bind(R.id.identity_document_type) Spinner identityDocumentTypeSpinner;
    @Bind(R.id.residence_document_type) Spinner residenceDocumentTypeSpinner;
//    @Bind(R.id.currency_spinner) Spinner currencySpinner;
    @Bind(R.id.info_identity_container) ViewGroup identityContainer;
    @Bind(R.id.info_residency_container) ViewGroup residencyContainer;
//    @Bind(R.id.document_action_identity) DocumentActionWidget documentActionIdentity;
    @Bind(R.id.document_action_residence) DocumentActionWidget documentActionResidence;
    @Bind(R.id.cb_agree_terms_conditions) CheckBox termsConditionsCheckBox;
    @Bind(R.id.agree_terms_conditions) View termsConditions;
    @Bind(R.id.cb_agree_risk_warning) CheckBox riskWarningCheckBox;
    @Bind(R.id.agree_risk_warning) View riskWarning;
    @Bind(R.id.cb_agree_data_sharing) CheckBox dataSharingCheckBox;
    @Bind(R.id.agree_data_sharing) View dataSharing;
    @Bind(R.id.cb_subscribe_offers) CheckBox subscribeOffersCheckBox;
    @Bind(R.id.cb_subscribe_trade_notifications) CheckBox subscribeTradeNotificationsCheckBox;
    @Bind(R.id.step_5_scan_id_button) ImageButton scanButton;
//    @Bind(R.id.btn_create) Button btnCreate;
//    @Bind(R.id.create_switcher) ViewSwitcher createSwitcher;
    @Bind(R.id.btn_submit) View btnSubmit;

    @Bind(R.id.sign_up_email) EditText email;
    @Bind(R.id.email_verify_button) KYCVerifyButton emailVerifybutton;


    @Inject Picasso picasso;
    @Inject ProviderCacheRx providerCacheRx;
    @Inject FastFillUtil fastFillUtil;
    @Inject CurrentUserId currentUserId;
    @Inject protected RequestHeaders requestHeaders;

    private ImageRequesterUtil imageRequesterUtil;
    private ProgressDialog progressDialog;
    private boolean hasUploadedJumio = false;
    private int providerId = 0;

    private Pattern emailPattern;
    private VerifyEmailDialogFragment vedf;
    private Drawable noErrorIconDrawable;
    private boolean hasClickedSubmitButton = false;
    SignalRManager signalRManager;

    private static PublishSubject<String> verifiedPublishEmail;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        verifiedPublishEmail = PublishSubject.create();
        emailPattern = Pattern.compile(getString(R.string.regex_email_validator));

        noErrorIconDrawable = getResources().getDrawable(R.drawable.red_alert);
        if (noErrorIconDrawable != null)
        {
            noErrorIconDrawable.setBounds(0,0,0,0);
        }

    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_5, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        updateDB(true, 5);
        email.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT &&
                    (emailVerifybutton.getState() == VerifyButtonState.PENDING
                            || emailVerifybutton.getState() == VerifyButtonState.FINISH))
            {
                showEmailVerificationPopup();
            }

            return false;
        });
    }

    @Override protected List<Subscription> onInitAyondoSubscription(final Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        ProviderDTO providerDTO = providerCacheRx.getCachedValue(new ProviderId(getProviderId(getArguments())));
        providerId = providerDTO.id;

        List<Subscription> subscriptions = new ArrayList<>();
        final Observable<ScannedDocument> documentObservable =
                fastFillUtil.getScannedDocumentObservable().throttleLast(300, TimeUnit.MILLISECONDS); //HACK

        if(providerDTO.getTermsConditionsUrl()==null)
        {
            termsConditions.setVisibility(View.GONE);
            termsConditionsCheckBox.setVisibility(View.GONE);
            termsConditionsCheckBox.setChecked(true);
        }
        if(providerDTO.getDataSharingUrl()==null)
        {
            dataSharing.setVisibility(View.GONE);
            dataSharingCheckBox.setVisibility(View.GONE);
            dataSharingCheckBox.setChecked(true);
        }
        if(providerDTO.getRiskDisclosureUrl()==null){
            riskWarning.setVisibility(View.GONE);
            riskWarningCheckBox.setVisibility(View.GONE);
            riskWarningCheckBox.setChecked(true);
        }
        subscriptions.add(
                Observable.zip(
                        kycAyondoFormOptionsDTOObservable
                                .take(1)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<KYCAyondoFormOptionsDTO>()
                                {
                                    @Override public void call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                                    {

                                        LollipopArrayAdapter<IdentityDocumentDTO> identityDocumentTypeAdapter = new LollipopArrayAdapter<>(
                                                getActivity(),
                                                IdentityDocumentDTO.createList(getResources(), kycAyondoFormOptionsDTO.getIdentityDocumentTypes()));

                                        identityDocumentTypeSpinner.setAdapter(identityDocumentTypeAdapter);

                                        LollipopArrayAdapter<ResidenceDocumentDTO> residenceDocumentTypeAdapter = new LollipopArrayAdapter<>(
                                                getActivity(),
                                                ResidenceDocumentDTO.createList(getResources(), kycAyondoFormOptionsDTO.residenceDocumentTypes));
                                        residenceDocumentTypeSpinner.setAdapter(residenceDocumentTypeAdapter);

//                                        LollipopArrayAdapter<CurrencyDTO> currencyAdapter = new LollipopArrayAdapter<>(
//                                                getActivity(),
//                                                CurrencyDTO.createList(getResources(), kycAyondoFormOptionsDTO.currencies));
//                                        currencySpinner.setAdapter(currencyAdapter);
                                    }
                                }),
                        liveBrokerSituationDTOObservable.observeOn(AndroidSchedulers.mainThread()),
                        new Func2<KYCAyondoFormOptionsDTO, LiveBrokerSituationDTO, Object>()
                        {
                            @Override
                            public LiveBrokerSituationDTO call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO, LiveBrokerSituationDTO situationDTO)
                            {
                                KYCAyondoForm update = new KYCAyondoForm();
                                //noinspection ConstantConditions
                                update.pickFrom(populateIdentityDocumentType((KYCAyondoForm) situationDTO.kycForm,
                                        kycAyondoFormOptionsDTO.getIdentityDocumentTypes()));
                                update.pickFrom(populateResidenceDocumentType((KYCAyondoForm) situationDTO.kycForm,
                                        kycAyondoFormOptionsDTO.residenceDocumentTypes));
//                                update.pickFrom(populateCurrency((KYCAyondoForm) situationDTO.kycForm.,
//                                        kycAyondoFormOptionsDTO.currencies));
                                onNext(new LiveBrokerSituationDTO(situationDTO.broker, update));
                                return null;
                            }
                        })
                        .subscribe(
                                new EmptyAction1<>(),
                                new TimberOnErrorAction1("Failed to populate identity document type spinner")));

        subscriptions.add(
                Observable.merge(
                        AdapterViewObservable.selects(identityDocumentTypeSpinner)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override
                                    public KYCAyondoForm call(OnSelectedEvent identityTypeSelected)
                                    {
                                        return KYCAyondoFormFactory.fromIdentityDocumentTypeEvent(identityTypeSelected);
                                    }
                                }),
                        AdapterViewObservable.selects(residenceDocumentTypeSpinner)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override
                                    public KYCAyondoForm call(OnSelectedEvent residenceTypeSelected)
                                    {
                                        return KYCAyondoFormFactory.fromResidenceDocumentTypeEvent(residenceTypeSelected);
                                    }
                                }),
                        WidgetObservable.input(termsConditionsCheckBox)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override
                                    public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromAgreeTermsConditionsEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(riskWarningCheckBox)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override
                                    public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromAgreeRiskWarningEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(dataSharingCheckBox)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override
                                    public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromAgreeDataSharingEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(subscribeOffersCheckBox)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromSubscribeOffers(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(subscribeTradeNotificationsCheckBox)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromSubscribeTradeNotifications(onCheckedChangeEvent);
                                    }
                                }))
//                        AdapterViewObservable.selects(currencySpinner)
//                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
//                                {
//                                    @Override public KYCAyondoForm call(OnSelectedEvent currencySelectedEvent)
//                                    {
//                                        return KYCAyondoFormFactory.fromCurrencySpinnerEvent(currencySelectedEvent);
//                                    }
//                                }))
                        .withLatestFrom(brokerDTOObservable, new Func2<KYCAyondoForm, LiveBrokerDTO, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(KYCAyondoForm update, LiveBrokerDTO brokerDTO)
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
                                new TimberOnErrorAction1("Failed to listen to spinner updates and agreement checkboxes"))
        );

        subscriptions.add(liveBrokerSituationDTOObservable
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return liveBrokerSituationDTO.kycForm != null && liveBrokerSituationDTO.kycForm instanceof KYCAyondoForm;
                    }
                })
                .map(new Func1<LiveBrokerSituationDTO, KYCAyondoForm>()
                {
                    @Override public KYCAyondoForm call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return (KYCAyondoForm) liveBrokerSituationDTO.kycForm;
                    }
                })
                .distinctUntilChanged(new Func1<KYCAyondoForm, ScanReference>()
                {
                    @Override public ScanReference call(KYCAyondoForm kycAyondoForm)
                    {
                        return kycAyondoForm.getScanReference();
                    }
                })
                .map(new Func1<KYCAyondoForm, ScanReference>()
                {
                    @Override public ScanReference call(KYCAyondoForm kycAyondoForm)
                    {
                        return kycAyondoForm.getScanReference();
                    }
                })
                .subscribe(new Action1<ScanReference>()
                {
                    @Override public void call(ScanReference scanReference)
                    {
                        //Update the documents needed
                        identityContainer.setVisibility(scanReference != null ? View.GONE : View.VISIBLE);
                    }
                }, new TimberOnErrorAction1("Failed to update identity document visibility")));

        subscriptions.add(liveBrokerSituationDTOObservable
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return liveBrokerSituationDTO.kycForm != null && liveBrokerSituationDTO.kycForm instanceof KYCAyondoForm;
                    }
                })
                .map(new Func1<LiveBrokerSituationDTO, KYCAyondoForm>()
                {
                    @Override public KYCAyondoForm call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return (KYCAyondoForm) liveBrokerSituationDTO.kycForm;
                    }
                })
                .distinctUntilChanged(new Func1<KYCAyondoForm, Boolean>()
                {
                    @Override public Boolean call(KYCAyondoForm kycAyondoForm)
                    {
                        return kycAyondoForm.getNeedResidencyDocument();
                    }
                })
                .map(new Func1<KYCAyondoForm, Boolean>()
                {
                    @Override public Boolean call(KYCAyondoForm kycAyondoForm)
                    {
                        return kycAyondoForm.getNeedResidencyDocument();
                    }
                })
                .subscribe(new Action1<Boolean>()
                {
                    @Override public void call(Boolean needResidencyDocument)
                    {
                        //Update the documents needed
                        if (needResidencyDocument != null)
                        {
                            residencyContainer.setVisibility(
                                    needResidencyDocument ? View.VISIBLE : View.GONE);
                        }
                    }
                }, new TimberOnErrorAction1("Failed to update residency document visibility")));

        subscriptions.add(liveBrokerSituationDTOObservable
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                //noinspection ConstantConditions
                                populate((KYCAyondoForm) situationDTO.kycForm);
//                                populate(documentActionIdentity, ((KYCAyondoForm) situationDTO.kycForm).getIdentityDocumentUrl());
                                populate(documentActionResidence, ((KYCAyondoForm) situationDTO.kycForm).getResidenceDocumentUrl());
                                checkFormToEnableButton();

                            }
                        },
                        new TimberOnErrorAction1("Failed to prepare files from KYC")));

        subscriptions.add(liveBrokerSituationDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>() {
                    @Override
                    public Observable<LiveBrokerSituationDTO> call(LiveBrokerSituationDTO liveBrokerSituationDTO) {
                        return ViewObservable.clicks(scanButton)
                                .map(new ReplaceWithFunc1<OnClickEvent, IdentityScannedDocumentType>(
                                        IdentityScannedDocumentType.IDENTITY_CARD))
                                .flatMap(
                                        new Func1<IdentityScannedDocumentType, Observable<LiveBrokerSituationDTO>>()
                                        {
                                            @Override
                                            public Observable<LiveBrokerSituationDTO> call(IdentityScannedDocumentType identityScannedDocumentType)
                                            {
                                                CountryCode code = null;
                                                if (identityScannedDocumentType.equals(IdentityScannedDocumentType.IDENTITY_CARD)
                                                        && liveBrokerSituationDTO.kycForm.getCountry() != null)
                                                {
                                                    code = CountryCode.getByCode(liveBrokerSituationDTO.kycForm.getCountry().toString());
                                                }

                                                //jumio
                                                fastFillUtil.fastFill(getActivity(), identityScannedDocumentType, code);

                                                return Observable.just(liveBrokerSituationDTO);
                                            }
                                        })
//                                .map(new Func1<ScannedDocument, LiveBrokerSituationDTO>()
//                                {
//                                    @Override
//                                    public LiveBrokerSituationDTO call(ScannedDocument scannedDocument)
//                                    {
//                                        //noinspection ConstantConditions
//                                        liveBrokerSituationDTO.kycForm.pickFrom(scannedDocument);
//                                        liveBrokerSituationPreference.set(liveBrokerSituationDTO);
//                                        return liveBrokerSituationDTO;
//                                    }
//                                })
                                ;
                    }
                }).subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override
                            public void call(@NonNull LiveBrokerSituationDTO situationToUse)
                            {
                                Log.v(getTag(), "Subscription call finish");
//                                goToSignUp();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override
                            public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Error when FastFill "+throwable.getMessage());
                                if (!FastFillExceptionUtil.canRetry(throwable))
                                {
//                                    THToast.show(R.string.unable_to_capture_value_from_image);
//                                    goToSignUp();
                                }
                            }
                        }));



        subscriptions.add(
                DocumentActionWidgetObservable.actions(documentActionResidence)
                        .withLatestFrom(brokerDTOObservable, new Func2<DocumentActionWidgetAction, LiveBrokerDTO, DocumentActionWidgetAction>()
                        {
                            @Override
                            public DocumentActionWidgetAction call(DocumentActionWidgetAction documentActionWidgetAction, LiveBrokerDTO brokerDTO)
                            {
                                if (documentActionWidgetAction.actionType.equals(DocumentActionWidgetActionType.CLEAR))
                                {
                                    KYCAyondoForm update = new KYCAyondoForm();
                                    update.setClearResidenceDocumentUrl(true);
                                    onNext(new LiveBrokerSituationDTO(brokerDTO, update));
                                }
                                return documentActionWidgetAction;
                            }
                        })
                        .filter(new Func1<DocumentActionWidgetAction, Boolean>()
                        {
                            @Override public Boolean call(DocumentActionWidgetAction documentActionWidgetAction)
                            {
                                return documentActionWidgetAction.actionType.equals(DocumentActionWidgetActionType.ACTION);
                            }
                        })
                        .withLatestFrom(brokerDTOObservable, new Func2<DocumentActionWidgetAction, LiveBrokerDTO, LiveBrokerDTO>()
                        {
                            @Override public LiveBrokerDTO call(DocumentActionWidgetAction documentActionWidgetAction, LiveBrokerDTO brokerDTO)
                            {
                                LiveSignUpStep5AyondoFragment.this.imageRequesterUtil = new ImageRequesterUtil(null, null, null, null);
                                return brokerDTO;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<LiveBrokerDTO, Observable<LiveBrokerSituationDTO>>()
                        {
                            @Override public Observable<LiveBrokerSituationDTO> call(final LiveBrokerDTO liveBrokerDTO)
                            {
                                return pickDocument(R.string.residence_document_pick_title, imageRequesterUtil)
                                        .take(1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnNext(new Action1<Bitmap>()
                                        {
                                            @Override public void call(Bitmap bitmap)
                                            {
                                                documentActionResidence.setLoading(true);
                                                documentActionResidence.setPreviewBitmap(bitmap);
                                            }
                                        })
                                        .observeOn(Schedulers.io())
                                        .map(new Func1<Bitmap, File>()
                                        {
                                            @Override public File call(Bitmap bitmap)
                                            {
                                                return imageRequesterUtil.getCroppedPhotoFile();
                                            }
                                        })
                                        .flatMap(new Func1<File, Observable<BrokerDocumentUploadResponseDTO>>()
                                        {
                                            @Override public Observable<BrokerDocumentUploadResponseDTO> call(File file)
                                            {
                                                return liveServiceWrapper.uploadDocument(file);
                                            }
                                        })
                                        .map(new Func1<BrokerDocumentUploadResponseDTO, LiveBrokerSituationDTO>()
                                        {
                                            @Override
                                            public LiveBrokerSituationDTO call(BrokerDocumentUploadResponseDTO brokerDocumentUploadResponseDTO)
                                            {
                                                KYCAyondoForm update = new KYCAyondoForm();
                                                update.setResidenceDocumentUrl(brokerDocumentUploadResponseDTO.url);
                                                return new LiveBrokerSituationDTO(liveBrokerDTO, update);
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnError(new Action1<Throwable>()
                                        {
                                            @Override public void call(Throwable throwable)
                                            {
                                                THToast.show(new THException(throwable));
                                            }
                                        })
                                        .finallyDo(new Action0()
                                        {
                                            @Override public void call()
                                            {
                                                documentActionResidence.setLoading(false);
                                            }
                                        });
                            }
                        })
                        .subscribe(
                                new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                        onNext(situationDTO);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to ask for and get residence document bitmap")));

        subscriptions.add(kycAyondoFormOptionsDTOObservable
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<KYCAyondoFormOptionsDTO, Observable<String>>()
                {
                    @Override public Observable<String> call(KYCAyondoFormOptionsDTO optionsDTO)
                    {
                        return Observable.merge(
                                ViewObservable.clicks(termsConditions)
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(providerDTO.getTermsConditionsUrl())),
                                ViewObservable.clicks(riskWarning)
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(providerDTO.getRiskDisclosureUrl())),
                                ViewObservable.clicks(dataSharing)
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(providerDTO.getDataSharingUrl()))
                        );
                    }
                })
                .subscribe(
                        new Action1<String>()
                        {
                            @Override public void call(String url)
                            {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to url clicks")));

        subscriptions.add(ViewObservable.clicks(btnSubmit)
                .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnClickEvent, LiveBrokerSituationDTO, KYCAyondoForm>()
                {
                    @Override public KYCAyondoForm call(OnClickEvent onClickEvent, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return (KYCAyondoForm) liveBrokerSituationDTO.kycForm;
                    }
                })
                .doOnNext(new Action1<KYCAyondoForm>()
                {
                    @Override public void call(KYCAyondoForm kycAyondoForm)
                    {
                        if (emailVerifybutton.state != VerifyButtonState.FINISH) {
                            emailVerifybutton.performClick();
                            hasClickedSubmitButton = true;
                        }
                    }
                })
                .filter(new Func1<KYCAyondoForm, Boolean>()
                {
                    @Override public Boolean call(KYCAyondoForm kycAyondoForm)
                    {
                        return emailVerifybutton.state == VerifyButtonState.FINISH;
                    }
                })
                .doOnNext(new Action1<KYCAyondoForm>()
                {
                    @Override public void call(KYCAyondoForm kycAyondoForm)
                    {
                        //Progress dialog
                        hasClickedSubmitButton = false;
                        progressDialog = ProgressDialogUtil.create(getActivity(), R.string.processing);
                    }
                })
                .flatMap(new Func1<KYCAyondoForm, Observable<BrokerApplicationDTO>>()
                {
                    @Override public Observable<BrokerApplicationDTO> call(KYCAyondoForm kycAyondoForm)
                    {
                        return liveServiceWrapper.submitApplication(kycAyondoForm, providerDTO.id);
                    }
                })
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        //Dismiss dialog
                        progressDialog.dismiss();
                    }
                })
                .subscribe(new Action1<BrokerApplicationDTO>()
                {
                    @Override public void call(BrokerApplicationDTO brokerApplicationDTO)
                    {
                        THToast.show("success");
                        progressDialog.dismiss();
                        getActivity().finish();
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        THToast.show(new THException(throwable));
                    }
                }));

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
                            KYCAyondoForm updated = KYCAyondoFormFactory.fromEmailEvent(onTextChangeEvent);

                            return new LiveBrokerSituationDTO(liveBrokerSituationDTO.broker, updated);
                        }).subscribe(this::onNext));

        subscriptions.add(ViewObservable.clicks(emailVerifybutton)
                .subscribe(new Action1<OnClickEvent>() {
                    @Override
                    public void call(OnClickEvent onClickEvent) {switch (emailVerifybutton.getState()) {
                        case BEGIN:
                            email.setError(LiveSignUpStep5AyondoFragment.this.getString(R.string.validation_incorrect_pattern_email), noErrorIconDrawable);
                            emailVerifybutton.setState(VerifyButtonState.ERROR);
                            break;
                        case PENDING:
                        case VALIDATE:
                            validateEmail();
                            break;
                    }
                    }
                }));

        return subscriptions;
    }

    @Override protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        checkFormToEnableButton();
//        StepStatus fifthStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(4);
//        if (btnCreate != null)
//        {
//            if (fifthStatus != null && StepStatus.COMPLETE.equals(fifthStatus))
//            {
//                if (createSwitcher.getDisplayedChild() != INDEX_VIEW_SUBMIT_BUTTON)
//                {
//                    createSwitcher.setDisplayedChild(INDEX_VIEW_SUBMIT_BUTTON);
//                }
//            }
//            else
//            {
//                if (createSwitcher.getDisplayedChild() != INDEX_VIEW_CREATE_BUTTON)
//                {
//                    createSwitcher.setDisplayedChild(INDEX_VIEW_CREATE_BUTTON);
//                }
//            }
//        }
    }

    @MainThread
    private void checkFormToEnableButton(){
        if(documentActionResidence.hasImageUploaded()
//                && documentActionIdentity.hasImageUploaded()
                && hasUploadedJumio
                && termsConditionsCheckBox.isChecked()
                && riskWarningCheckBox.isChecked()
                && dataSharingCheckBox.isChecked()
                && emailPattern.matcher(email.getText()).matches()){

//                createSwitcher.showNext();
            btnSubmit.setBackground(getResources().getDrawable(R.drawable.basic_green_selector));
            btnSubmit.setEnabled(true);


        }else{
            btnSubmit.setBackground(null);
            btnSubmit.setBackgroundColor(Color.LTGRAY);
            btnSubmit.setEnabled(false);

            //if (emailVerifybutton.getState() != VerifyButtonState.FINISH) {
            //    if (!emailPattern.matcher(email.getText()).matches()) {
            //        email.setError("Invalid email address", noErrorIconDrawable);
            //        requestFocusAndShowKeyboard(email);
            //    } else {
            //        //hasClickedJoinButton = true;
            //        emailVerifybutton.performClick();
            //    }
            //}else{
            //    email.setError(null);
            //}
        }
    }

    @NonNull private Observable<Bitmap> pickDocument(@StringRes int dialogTitle, @NonNull final ImageRequesterUtil imageRequesterUtil)
    {
        String[] choices = new String[2];
        choices[INDEX_CHOICE_FROM_CAMERA] = getActivity().getString(R.string.user_profile_choose_image_from_camera);
        choices[INDEX_CHOICE_FROM_LIBRARY] = getActivity().getString(R.string.user_profile_choose_image_from_library);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.image_picker_item,
                choices);
        return AlertDialogRxUtil.build(getActivity())
                .setTitle(dialogTitle)
                .setNegativeButton(R.string.cancel)
                .setSingleChoiceItems(adapter, -1)
                .setCanceledOnTouchOutside(true)
                .build()
                .flatMap(new Func1<OnDialogClickEvent, Observable<Bitmap>>()
                {
                    @Override public Observable<Bitmap> call(OnDialogClickEvent event)
                    {
                        event.dialog.dismiss();
                        switch (event.which)
                        {
                            case INDEX_CHOICE_FROM_CAMERA:
                                imageRequesterUtil.onImageFromCameraRequested(getActivity(), ImageRequesterUtil.REQUEST_CAMERA);
                                break;
                            case INDEX_CHOICE_FROM_LIBRARY:
                                imageRequesterUtil.onImageFromLibraryRequested(getActivity(), ImageRequesterUtil.REQUEST_GALLERY);
                                break;
                        }
                        return imageRequesterUtil.getBitmapObservable();
                    }
                });
    }

    @Override public void onDestroyView()
    {
        progressDialog = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        verifiedPublishEmail = null;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ImageRequesterUtil.REQUEST_CAMERA:
            case ImageRequesterUtil.REQUEST_GALLERY:
            case ImageRequesterUtil.REQUEST_PHOTO_ZOOM:
                if (imageRequesterUtil != null)
                {
                    imageRequesterUtil.onActivityResult(getActivity(), requestCode, resultCode, data);
                }
                break;

            case NetverifyFastFillUtil.NET_VERIFY_REQUEST_CODE:
//                NetverifyScanReference scanReference = new NetverifyScanReference(data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE));
//                scannedDocumentSubject.onNext(new NetverifyScannedDocument(
//                        scanReference,
//                        data.<NetverifyDocumentData>getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA)));

//                fastFillUtil.onActivityResult(getActivity(), requestCode, resultCode, data);
                String scanRef = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
                String dataType = NVDocumentType.IDENTITY_CARD.toString();

                updateLayoutFromJumio(dataType, scanRef);

            default:break;
        }

        if (requestCode == REQUEST_VERIFY_EMAIL_CODE && resultCode == Activity.RESULT_OK) {
            Log.v(getTag(), "Jeff email ok");
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

    @MainThread
    private void populate(@NonNull KYCAyondoForm kycForm)
    {
        String emailText = kycForm.getEmail();
        if (email != null && emailText != null && !emailText.equals(email.getText().toString()))
        {
            email.setText(emailText);
            String currentVerifiedEmail = kycForm.getVerifiedEmailAddress();
            if(currentVerifiedEmail!=null && currentVerifiedEmail.equals(emailText)){
                emailVerifybutton.setState(VerifyButtonState.FINISH);
            }
        }

        Boolean agreeTerms = kycForm.isAgreeTermsConditions();
        if (agreeTerms != null)
        {
            termsConditionsCheckBox.setChecked(agreeTerms);
        }

        Boolean riskWarning = kycForm.isAgreeRisksWarnings();
        if (riskWarning != null)
        {
            riskWarningCheckBox.setChecked(riskWarning);
        }

        Boolean dataSharing = kycForm.isAgreeDataSharing();
        if (dataSharing != null)
        {
            dataSharingCheckBox.setChecked(dataSharing);
        }

        Boolean subscribeOffers = kycForm.isSubscribeOffers();
        if (subscribeOffers != null)
        {
            subscribeOffersCheckBox.setChecked(subscribeOffers);
        }
        else
        {
            subscribeOffersCheckBox.setChecked(true);
        }

        Boolean subscribeTradeNotifications = kycForm.isSubscribeTradeNotifications();
        if (subscribeTradeNotifications != null)
        {
            subscribeTradeNotificationsCheckBox.setChecked(subscribeTradeNotifications);
        }
        else
        {
            subscribeTradeNotificationsCheckBox.setChecked(true);
        }
    }

    @MainThread
    @NonNull private KYCAyondoForm populateIdentityDocumentType(@NonNull KYCAyondoForm kycForm,
            @NonNull List<IdentityScannedDocumentType> identityScannedDocumentTypes)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        IdentityScannedDocumentType type = kycForm.getIdentityDocumentType();
        Integer index = populateSpinner(identityDocumentTypeSpinner,
                type,
                identityScannedDocumentTypes);
        if (type == null)
        {
            IdentityScannedDocumentType chosen;
            if (index != null)
            {
                chosen = identityScannedDocumentTypes.get(index);
            }
            else
            {
                chosen = ((IdentityDocumentDTO) identityDocumentTypeSpinner.getSelectedItem()).identityScannedDocumentType;
            }

            if (chosen != null)
            {
                update.setIdentityDocumentType(chosen);
            }
        }
        return update;
    }

    @MainThread
    @NonNull private KYCAyondoForm populateResidenceDocumentType(@NonNull KYCAyondoForm kycForm,
            @NonNull List<ResidenceScannedDocumentType> residenceScannedDocumentTypes)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        ResidenceScannedDocumentType type = kycForm.getResidenceDocumentType();
        Integer index = populateSpinner(residenceDocumentTypeSpinner,
                type,
                residenceScannedDocumentTypes);
        if (type == null)
        {
            ResidenceScannedDocumentType chosen;
            if (index != null)
            {
                chosen = residenceScannedDocumentTypes.get(index);
            }
            else
            {
                chosen = ((ResidenceDocumentDTO) residenceDocumentTypeSpinner.getSelectedItem()).residenceScannedDocumentType;
            }

            if (chosen != null)
            {
                update.setResidenceDocumentType(chosen);
            }
        }
        return update;
    }


    @MainThread
    private void populate(@Nullable DocumentActionWidget widget, @Nullable String imageUrl)
    {
        if (widget != null && imageUrl != null)
        {
            picasso.load(imageUrl).into(widget);
        }
    }

    @MainThread
    private void updateLayoutFromJumio(String dataType, String scanRef){
        JumioVerifyBodyDTO jumioDTO = new JumioVerifyBodyDTO(dataType, scanRef);

Log.v(getTag(), "Provider id: "+providerId);
        liveServiceWrapper.uploadScanReference(jumioDTO, providerId).subscribe(new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                Log.v(getTag(), "Scan ref: "+dataType+":"+scanRef);
            }

            @Override
            public void onError(Throwable e) {
                Log.v(getTag(), "Onerror Scan ref: "+dataType+":"+scanRef);
                THToast.show("Upload document failed");
            }

            @Override
            public void onNext(Response response) {
                Log.v(getTag(), "On next Scan ref: "+dataType+":"+scanRef);
                if(response.getStatus() == 200){
                    Log.v(getTag(), "On next Scan ref: "+response.getStatus());
//                    Observable.just(updateUILayout()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hasUploadedJumio = true;
                            scanButton.setImageResource(R.drawable.green_tick);
                            scanButton.setClickable(false);
                            identityDocumentTypeSpinner.setEnabled(false);
                            checkFormToEnableButton();
                        }
                    });
                }

            }
        });
    }

    private boolean isValidEmail(String email)
    {
        return emailPattern.matcher(email).matches();
    }

    @MainThread
    protected void validateEmail()
    {
        final String email = this.email.getText().toString();

        //        liveServiceWrapper.verifyEmail(currentUserId.get(), email).subscribe();

        showEmailVerificationPopup();
        setupSignalR(email);
    }

    private void showEmailVerificationPopup() {
        vedf = VerifyEmailDialogFragment.show(REQUEST_VERIFY_EMAIL_CODE, this, currentUserId.get(), email.getText().toString(), this.providerId);
    }

    public void setupSignalR(String emailAddress) {

        signalRManager = new SignalRManager(requestHeaders, currentUserId);
        signalRManager.initWithEvent(LiveNetworkConstants.HUB_NAME,
                "SetValidationStatus",
                new String[]{emailAddress},
                emailVerifybutton, emailVerifiedDTO ->{
                    if(((EmailVerifiedDTO)emailVerifiedDTO).isValidated()){
                        updateEmailVerification(emailAddress, null, true);
                    }
                }, EmailVerifiedDTO.class);
    }

    @MainThread
    public void updateEmailVerification(String emailAddress, String errorMessage, boolean isSuccess){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(vedf.isVisible()){
                    try{
                        vedf.dismiss();
                    }catch (Exception e){
                        //might be closed or not in view
                    }
                }
                if(isSuccess){
                    emailVerifybutton.setState(VerifyButtonState.FINISH);
                    verifiedPublishEmail.onNext(emailAddress);

                    if(hasClickedSubmitButton){
                        btnSubmit.performClick();
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

    private void requestFocusAndShowKeyboard(EditText textView) {
        if(textView.requestFocus()){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}