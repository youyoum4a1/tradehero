package com.tradehero.th.fragments.live.ayondo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.settings.ImageRequesterUtil;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class LiveSignUpStep5AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int INDEX_CHOICE_FROM_CAMERA = 0;
    private static final int INDEX_CHOICE_FROM_LIBRARY = 1;

    @Bind(R.id.identity_document_type) Spinner identityDocumentTypeSpinner;
    @Bind(R.id.residence_document_type) Spinner residenceDocumentTypeSpinner;
    @Bind(R.id.document_action_identity) DocumentActionWidget documentActionIdentity;
    @Bind(R.id.document_action_residence) DocumentActionWidget documentActionResidence;
    @Bind(R.id.cb_agree_terms_conditions) CheckBox termsConditionsCheckBox;
    @Bind(R.id.agree_terms_conditions) View termsConditions;
    @Bind(R.id.cb_agree_risk_warning) CheckBox riskWarningCheckBox;
    @Bind(R.id.agree_risk_warning) View riskWarning;
    @Bind(R.id.cb_agree_data_sharing) CheckBox dataSharingCheckBox;
    @Bind(R.id.agree_data_sharing) View dataSharing;

    private ImageRequesterUtil imageRequesterUtil;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_5, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final Observable<LiveBrokerDTO> brokerDTOObservable = createBrokerObservable();

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getKYCAyondoFormOptionsObservable().observeOn(AndroidSchedulers.mainThread())
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
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
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

        onDestroyViewSubscriptions.add(Observable.combineLatest(
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

        onDestroyViewSubscriptions.add(getBrokerSituationObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                KYCAyondoForm update = new KYCAyondoForm();
                                //noinspection ConstantConditions
                                update.pickFrom(populate((KYCAyondoForm) situationDTO.kycForm));
                                populate(documentActionIdentity, ((KYCAyondoForm) situationDTO.kycForm).getIdentityDocumentFile());
                                populate(documentActionResidence, ((KYCAyondoForm) situationDTO.kycForm).getResidenceDocumentFile());
                                onNext(new LiveBrokerSituationDTO(situationDTO.broker, update));
                            }
                        },
                        new TimberOnErrorAction1("Failed to prepare files from KYC")));

        onDestroyViewSubscriptions.add(
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
                                    update.setClearIdentityDocumentFile(true);
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
                        .flatMap(new Func1<DocumentActionWidgetAction, Observable<Bitmap>>()
                        {
                            @Override public Observable<Bitmap> call(DocumentActionWidgetAction ignored)
                            {
                                final ImageRequesterUtil imageRequesterUtil = new ImageRequesterUtil(null, null, null, null);
                                LiveSignUpStep5AyondoFragment.this.imageRequesterUtil = imageRequesterUtil;
                                return Observable.combineLatest(
                                        brokerDTOObservable,
                                        pickDocument(R.string.identity_document_pick_title, imageRequesterUtil)
                                                .take(1),
                                        new Func2<LiveBrokerDTO, Bitmap, Bitmap>()
                                        {
                                            @Override public Bitmap call(LiveBrokerDTO brokerDTO, Bitmap bitmap)
                                            {
                                                KYCAyondoForm update = new KYCAyondoForm();
                                                update.setIdentityDocumentFile(imageRequesterUtil.getCroppedPhotoFile());
                                                onNext(new LiveBrokerSituationDTO(brokerDTO, update));
                                                return bitmap;
                                            }
                                        });
                            }
                        })
                        .subscribe(
                                new Action1<Bitmap>()
                                {
                                    @Override public void call(Bitmap bitmap)
                                    {
                                        documentActionIdentity.setPreviewBitmap(bitmap);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to ask for and get identity document bitmap")));

        onDestroyViewSubscriptions.add(
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
                                    update.setClearResidenceDocumentFile(true);
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
                        .flatMap(new Func1<DocumentActionWidgetAction, Observable<Bitmap>>()
                        {
                            @Override public Observable<Bitmap> call(DocumentActionWidgetAction ignored)
                            {
                                final ImageRequesterUtil imageRequesterUtil = new ImageRequesterUtil(null, null, null, null);
                                LiveSignUpStep5AyondoFragment.this.imageRequesterUtil = imageRequesterUtil;
                                return Observable.combineLatest(
                                        brokerDTOObservable,
                                        pickDocument(R.string.residence_document_pick_title, imageRequesterUtil)
                                                .take(1),
                                        new Func2<LiveBrokerDTO, Bitmap, Bitmap>()
                                        {
                                            @Override public Bitmap call(LiveBrokerDTO brokerDTO, Bitmap bitmap)
                                            {
                                                KYCAyondoForm update = new KYCAyondoForm();
                                                update.setResidenceDocumentFile(imageRequesterUtil.getCroppedPhotoFile());
                                                onNext(new LiveBrokerSituationDTO(brokerDTO, update));
                                                return bitmap;
                                            }
                                        });
                            }
                        })
                        .subscribe(
                                new Action1<Bitmap>()
                                {
                                    @Override public void call(Bitmap bitmap)
                                    {
                                        documentActionResidence.setPreviewBitmap(bitmap);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to ask for and get residence document bitmap")));

        onDestroyViewSubscriptions.add(getKYCAyondoFormOptionsObservable()
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

        getKYCAyondoFormOptionsObservable().connect();
        getKYCFormOptionsObservable().connect();
        getBrokerSituationObservable().connect();
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

        return update;
    }

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

    private void populate(@Nullable DocumentActionWidget widget, @Nullable File imageFile)
    {
        if (widget != null && imageFile != null)
        {
            FileInputStream inputStream = null;
            try
            {
                inputStream = new FileInputStream(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                widget.setPreviewBitmap(bitmap);
            } catch (Exception e)
            {
                Timber.e(e, "Failed to load " + imageFile);
            } finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    } catch (IOException e)
                    {
                        Timber.e(e, "When closing inputStream of " + imageFile);
                    }
                }
            }
        }
    }
}
