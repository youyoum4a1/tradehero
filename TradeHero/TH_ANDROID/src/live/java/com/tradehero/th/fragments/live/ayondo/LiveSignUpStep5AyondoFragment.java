package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.widget.DocumentActionWidget;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;

public class LiveSignUpStep5AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @Bind(R.id.identify_document_type) Spinner identifyDocumentTypeSpinner;
    @Bind(R.id.residence_document_type) Spinner residenceDocumentTypeSpinner;
    @Bind(R.id.document_action_identity) DocumentActionWidget documentActionIdentity;
    @Bind(R.id.document_action_residence) DocumentActionWidget documentActionResidence;
    @Bind(R.id.document_action_signature) DocumentActionWidget documentActionSignature;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_5, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

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
                                        identifyDocumentTypeSpinner.setAdapter(identityDocumentTypeAdapter);

                                        LollipopArrayAdapter<ResidenceDocumentDTO> residenceDocumentTypeAdapter = new LollipopArrayAdapter<>(
                                                getActivity(),
                                                ResidenceDocumentDTO.createList(getResources(), kycAyondoFormOptionsDTO.residenceDocumentTypes));
                                        residenceDocumentTypeSpinner.setAdapter(residenceDocumentTypeAdapter);
                                    }
                                }),
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        new Func2<KYCAyondoFormOptionsDTO, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                        {
                            @Override
                            public LiveBrokerSituationDTO call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO, LiveBrokerSituationDTO situationDTO)
                            {
                                //noinspection ConstantConditions
                                populateSpinner(identifyDocumentTypeSpinner,
                                        ((KYCAyondoForm) situationDTO.kycForm).getIdentifyDocumentType(),
                                        kycAyondoFormOptionsDTO.getIdentityDocumentTypes());
                                populateSpinner(residenceDocumentTypeSpinner,
                                        ((KYCAyondoForm) situationDTO.kycForm).getResidenceDocumentType(),
                                        kycAyondoFormOptionsDTO.residenceDocumentTypes);
                                return situationDTO;
                            }
                        })
                        .subscribe(
                                new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to populate identity document type spinner")));

        onDestroyViewSubscriptions.add(
                Observable.merge(
                        Observable.combineLatest(
                                getBrokerSituationObservable(),
                                AdapterViewObservable.selects(identifyDocumentTypeSpinner),
                                new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                                {
                                    @Override
                                    public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent identityTypeSelected)
                                    {
                                        if (identityTypeSelected instanceof OnItemSelectedEvent)
                                        {
                                            //noinspection ConstantConditions
                                            ((KYCAyondoForm) situationDTO.kycForm).setIdentifyDocumentType(
                                                    ((IdentityDocumentDTO) identityTypeSelected.parent.getItemAtPosition(
                                                            ((OnItemSelectedEvent) identityTypeSelected).position)).identityScannedDocumentType);
                                        }
                                        return situationDTO;
                                    }
                                }),
                        Observable.combineLatest(
                                getBrokerSituationObservable(),
                                AdapterViewObservable.selects(residenceDocumentTypeSpinner),
                                new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                                {
                                    @Override
                                    public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent residenceTypeSelected)
                                    {
                                        if (residenceTypeSelected instanceof OnItemSelectedEvent)
                                        {
                                            //noinspection ConstantConditions
                                            ((KYCAyondoForm) situationDTO.kycForm).setResidenceDocumentType(
                                                    ((ResidenceDocumentDTO) residenceTypeSelected.parent.getItemAtPosition(
                                                            ((OnItemSelectedEvent) residenceTypeSelected).position)).residenceScannedDocumentType);
                                        }
                                        return situationDTO;
                                    }
                                }))
                        .subscribe(
                                new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                        onNext(situationDTO);
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to spinner updates")));
    }
}
