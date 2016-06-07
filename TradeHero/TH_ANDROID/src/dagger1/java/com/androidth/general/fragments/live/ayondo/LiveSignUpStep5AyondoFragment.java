package com.androidth.general.fragments.live.ayondo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.BrokerDocumentUploadResponseDTO;
import com.androidth.general.api.kyc.Currency;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.exception.THException;
import com.androidth.general.fragments.base.LollipopArrayAdapter;
import com.androidth.general.fragments.settings.ImageRequesterUtil;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ResidenceScannedDocumentType;
import com.androidth.general.models.fastfill.ScanReference;
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
import com.squareup.picasso.Picasso;
import com.androidth.general.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Observable;
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

public class LiveSignUpStep5AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int INDEX_CHOICE_FROM_CAMERA = 0;
    private static final int INDEX_CHOICE_FROM_LIBRARY = 1;
    private static final int INDEX_VIEW_CREATE_BUTTON = 0;
    private static final int INDEX_VIEW_SUBMIT_BUTTON = 1;

    @Bind(R.id.identity_document_type) Spinner identityDocumentTypeSpinner;
    @Bind(R.id.residence_document_type) Spinner residenceDocumentTypeSpinner;
    @Bind(R.id.currency_spinner) Spinner currencySpinner;
    @Bind(R.id.info_identity_container) ViewGroup identityContainer;
    @Bind(R.id.info_residency_container) ViewGroup residencyContainer;
    @Bind(R.id.document_action_identity) DocumentActionWidget documentActionIdentity;
    @Bind(R.id.document_action_residence) DocumentActionWidget documentActionResidence;
    @Bind(R.id.cb_agree_terms_conditions) CheckBox termsConditionsCheckBox;
    @Bind(R.id.agree_terms_conditions) View termsConditions;
    @Bind(R.id.cb_agree_risk_warning) CheckBox riskWarningCheckBox;
    @Bind(R.id.agree_risk_warning) View riskWarning;
    @Bind(R.id.cb_agree_data_sharing) CheckBox dataSharingCheckBox;
    @Bind(R.id.agree_data_sharing) View dataSharing;
    @Bind(R.id.cb_subscribe_offers) CheckBox subscribeOffersCheckBox;
    @Bind(R.id.cb_subscribe_trade_notifications) CheckBox subscribeTradeNotificationsCheckBox;
    @Bind(R.id.btn_create) Button btnCreate;
    @Bind(R.id.create_switcher) ViewSwitcher createSwitcher;
    @Bind(R.id.btn_submit) View btnSubmit;

    private ImageRequesterUtil imageRequesterUtil;

    @Inject Picasso picasso;
    private ProgressDialog progressDialog;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_5, container, false);
    }

    @Override protected List<Subscription> onInitAyondoSubscription(final Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

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

                                        LollipopArrayAdapter<CurrencyDTO> currencyAdapter = new LollipopArrayAdapter<>(
                                                getActivity(),
                                                CurrencyDTO.createList(getResources(), kycAyondoFormOptionsDTO.currencies));
                                        currencySpinner.setAdapter(currencyAdapter);
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
                                update.pickFrom(populateCurrency((KYCAyondoForm) situationDTO.kycForm,
                                        kycAyondoFormOptionsDTO.currencies));
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
                                }),
                        AdapterViewObservable.selects(currencySpinner)
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent currencySelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromCurrencySpinnerEvent(currencySelectedEvent);
                                    }
                                }))
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
                                populate(documentActionIdentity, ((KYCAyondoForm) situationDTO.kycForm).getIdentityDocumentUrl());
                                populate(documentActionResidence, ((KYCAyondoForm) situationDTO.kycForm).getResidenceDocumentUrl());
                            }
                        },
                        new TimberOnErrorAction1("Failed to prepare files from KYC")));

        subscriptions.add(
                DocumentActionWidgetObservable.actions(documentActionIdentity)
                        .withLatestFrom(brokerDTOObservable, new Func2<DocumentActionWidgetAction, LiveBrokerDTO, DocumentActionWidgetAction>()
                        {
                            @Override
                            public DocumentActionWidgetAction call(DocumentActionWidgetAction documentActionWidgetAction, LiveBrokerDTO brokerDTO)
                            {
                                if (documentActionWidgetAction.actionType.equals(DocumentActionWidgetActionType.CLEAR))
                                {
                                    KYCAyondoForm update = new KYCAyondoForm();
                                    update.setClearIdentityDocumentUrl(true);
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
                        .flatMap(new Func1<LiveBrokerDTO, Observable<LiveBrokerSituationDTO>>()
                        {
                            @Override public Observable<LiveBrokerSituationDTO> call(final LiveBrokerDTO liveBrokerDTO)
                            {
                                return pickDocument(R.string.identity_document_pick_title, imageRequesterUtil)
                                        .take(1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnNext(new Action1<Bitmap>()
                                        {
                                            @Override public void call(Bitmap bitmap)
                                            {
                                                documentActionIdentity.setLoading(true);
                                                documentActionIdentity.setPreviewBitmap(bitmap);
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
                                                update.setIdentityDocumentUrl(brokerDocumentUploadResponseDTO.url);
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
                                                documentActionIdentity.setLoading(false);
                                            }
                                        });
                            }
                        })
                        .observeOn(Schedulers.io())
                        .subscribe(
                                new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                        onNext(situationDTO);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to ask for and get identity document bitmap")));

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
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(optionsDTO.termsConditionsUrl)),
                                ViewObservable.clicks(riskWarning)
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(optionsDTO.riskWarningDisclaimerUrl)),
                                ViewObservable.clicks(dataSharing)
                                        .map(new ReplaceWithFunc1<OnClickEvent, String>(optionsDTO.dataSharingAgreementUrl))
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
                        //Progress dialog
                        progressDialog = ProgressDialogUtil.create(getActivity(), R.string.processing);
                    }
                })
                .flatMap(new Func1<KYCAyondoForm, Observable<BrokerApplicationDTO>>()
                {
                    @Override public Observable<BrokerApplicationDTO> call(KYCAyondoForm kycAyondoForm)
                    {
                        return liveServiceWrapper.submitApplication(kycAyondoForm);
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
                        getActivity().finish();
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        THToast.show(new THException(throwable));
                    }
                }));

        return subscriptions;
    }

    @Override protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        StepStatus fifthStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(4);
        if (btnCreate != null)
        {
            if (fifthStatus != null && StepStatus.COMPLETE.equals(fifthStatus))
            {
                if (createSwitcher.getDisplayedChild() != INDEX_VIEW_SUBMIT_BUTTON)
                {
                    createSwitcher.setDisplayedChild(INDEX_VIEW_SUBMIT_BUTTON);
                }
            }
            else
            {
                if (createSwitcher.getDisplayedChild() != INDEX_VIEW_CREATE_BUTTON)
                {
                    createSwitcher.setDisplayedChild(INDEX_VIEW_CREATE_BUTTON);
                }
            }
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
                                imageRequesterUtil.onImageFromCameraRequested(getActivity());
                                break;
                            case INDEX_CHOICE_FROM_LIBRARY:
                                imageRequesterUtil.onImageFromLibraryRequested(getActivity());
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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageRequesterUtil != null)
        {
            imageRequesterUtil.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
    }

    @MainThread
    private void populate(@NonNull KYCAyondoForm kycForm)
    {
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
    @NonNull private KYCAyondoForm populateCurrency(@NonNull KYCAyondoForm kycForm,
            @NonNull List<Currency> currencies)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        Currency currency = kycForm.getCurrency();
        Integer index = populateSpinner(currencySpinner, currency, currencies);

        if (currency == null)
        {
            Currency chosen;
            if (index != null)
            {
                chosen = currencies.get(index);
            }
            else
            {
                chosen = ((CurrencyDTO) currencySpinner.getSelectedItem()).currency;
            }

            if (chosen != null)
            {
                update.setCurrency(chosen);
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
}