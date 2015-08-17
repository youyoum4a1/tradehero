package com.tradehero.th.fragments.live.ayondo;

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
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.BrokerDocumentUploadResponseDTO;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.ayondo.DummyKYCAyondoUtil;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.settings.ImageRequesterUtil;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ResidenceScannedDocumentType;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWithFunc1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.widget.DocumentActionWidget;
import com.tradehero.th.widget.DocumentActionWidgetAction;
import com.tradehero.th.widget.DocumentActionWidgetActionType;
import com.tradehero.th.widget.DocumentActionWidgetObservable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
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

    @Bind(R.id.identity_document_type) Spinner identityDocumentTypeSpinner;
    @Bind(R.id.residence_document_type) Spinner residenceDocumentTypeSpinner;
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

    private ImageRequesterUtil imageRequesterUtil;

    @Inject Picasso picasso;

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
                Observable.combineLatest(
                        kycAyondoFormOptionsDTOObservable.observeOn(AndroidSchedulers.mainThread())
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
                                onNext(new LiveBrokerSituationDTO(situationDTO.broker, update));
                                return null;
                            }
                        })
                        .subscribe(
                                new EmptyAction1<>(),
                                new TimberOnErrorAction1("Failed to populate identity document type spinner")));

        subscriptions.add(Observable.combineLatest(
                brokerDTOObservable,
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
                        new TimberOnErrorAction1("Failed to listen to spinner updates and agreement checkboxes")));

        subscriptions.add(liveBrokerSituationDTOObservable
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                KYCAyondoForm update = new KYCAyondoForm();
                                //noinspection ConstantConditions
                                update.pickFrom(populate((KYCAyondoForm) situationDTO.kycForm));
                                populate(documentActionIdentity, ((KYCAyondoForm) situationDTO.kycForm).getIdentityDocumentUrl());
                                populate(documentActionResidence, ((KYCAyondoForm) situationDTO.kycForm).getResidenceDocumentUrl());
                            }
                        },
                        new TimberOnErrorAction1("Failed to prepare files from KYC")));

        subscriptions.add(
                Observable.combineLatest(
                        brokerDTOObservable,
                        DocumentActionWidgetObservable.actions(documentActionIdentity),
                        new Func2<LiveBrokerDTO, DocumentActionWidgetAction, DocumentActionWidgetAction>()
                        {
                            @Override public DocumentActionWidgetAction call(LiveBrokerDTO brokerDTO,
                                    DocumentActionWidgetAction documentActionWidgetAction)
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
                Observable.combineLatest(
                        brokerDTOObservable,
                        DocumentActionWidgetObservable.actions(documentActionResidence),
                        new Func2<LiveBrokerDTO, DocumentActionWidgetAction, DocumentActionWidgetAction>()
                        {
                            @Override public DocumentActionWidgetAction call(LiveBrokerDTO brokerDTO,
                                    DocumentActionWidgetAction documentActionWidgetAction)
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

        subscriptions.add(liveBrokerSituationDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<LiveBrokerSituationDTO, KYCAyondoForm>()
                        {
                            @Override public KYCAyondoForm call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                return ((KYCAyondoForm) liveBrokerSituationDTO.kycForm);
                            }
                        })
                        .subscribe(new Action1<KYCAyondoForm>()
                        {
                            @Override public void call(KYCAyondoForm kycAyondoForm)
                            {
                                boolean enabled = DummyKYCAyondoUtil.getStep5(kycAyondoForm).equals(StepStatus.COMPLETE);

                                btnCreate.setEnabled(enabled);
                            }
                        }, new TimberOnErrorAction1("Failed to update create button"))
        );
        return subscriptions;
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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageRequesterUtil != null)
        {
            imageRequesterUtil.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
    }

    @MainThread
    @NonNull private KYCAyondoForm populate(@NonNull KYCAyondoForm kycForm)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        Boolean agreeTerms = kycForm.isAgreeTermsConditions();
        if (agreeTerms != null)
        {
            termsConditionsCheckBox.setChecked(agreeTerms);
        }
        else
        {
            update.setAgreeTermsConditions(termsConditionsCheckBox.isChecked());
        }

        Boolean riskWarning = kycForm.isAgreeRisksWarnings();
        if (riskWarning != null)
        {
            riskWarningCheckBox.setChecked(riskWarning);
        }
        else
        {
            update.setAgreeRisksWarnings(riskWarningCheckBox.isChecked());
        }

        Boolean dataSharing = kycForm.isAgreeDataSharing();
        if (dataSharing != null)
        {
            dataSharingCheckBox.setChecked(dataSharing);
        }
        else
        {
            update.setAgreeDataSharing(dataSharingCheckBox.isChecked());
        }

        Boolean subscribeOffers = kycForm.isSubscribeOffers();
        if (subscribeOffers != null)
        {
            subscribeOffersCheckBox.setChecked(subscribeOffers);
        }
        else
        {
            update.setSubscribeOffers(subscribeOffersCheckBox.isChecked());
        }

        Boolean subscribeTradeNotifications = kycForm.isSubscribeTradeNotifications();
        if (subscribeTradeNotifications != null)
        {
            subscribeTradeNotificationsCheckBox.setChecked(subscribeTradeNotifications);
        }
        else
        {
            update.setSubscribeTradeNotifications(subscribeTradeNotificationsCheckBox.isChecked());
        }

        Boolean needIdentityDocument = kycForm.getNeedIdentityDocument();
        if (needIdentityDocument != null)
        {
            identityContainer.setVisibility(needIdentityDocument ? View.VISIBLE : View.GONE);
        }

        Boolean needResidencyDocument = kycForm.getNeedResidencyDocument();
        if (needResidencyDocument != null)
        {
            residencyContainer.setVisibility(
                    needResidencyDocument ? View.VISIBLE : View.GONE);
        }

        return update;
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
}
