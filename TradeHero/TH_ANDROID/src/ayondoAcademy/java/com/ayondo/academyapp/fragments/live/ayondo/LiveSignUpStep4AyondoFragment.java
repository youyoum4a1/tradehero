package com.ayondo.academyapp.fragments.live.ayondo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.neovisionaries.i18n.CountryCode;
import com.androidth.general.common.utils.THToast;
import com.tradehero.th.R;
import com.androidth.general.api.kyc.KYCAddress;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.ayondo.academyapp.api.kyc.ayondo.DummyKYCAyondoUtil;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.market.Country;
import com.androidth.general.fragments.live.CountrySpinnerAdapter;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.widget.KYCAddressWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class LiveSignUpStep4AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int PICK_LOCATION_REQUEST_PRIMARY = 2513;
    private static final int PICK_LOCATION_REQUEST_SECONDARY = 2514;
    private Geocoder mGeocoder;

    @Bind(R.id.info_address_pri) KYCAddressWidget primaryWidget;
    @Bind(R.id.info_address_sec) KYCAddressWidget secondaryWidget;
    @Bind(R.id.info_address_sec_title) TextView secondaryAddressWidget;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_4, container, false);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mGeocoder = new Geocoder(getActivity());
    }

    @Override protected List<Subscription> onInitAyondoSubscription(final Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(primaryWidget.getPickLocationClickedObservable()
                .subscribe(new Action1<OnClickEvent>()
                {
                    @Override public void call(OnClickEvent onClickEvent)
                    {
                        pickLocation(PICK_LOCATION_REQUEST_PRIMARY);
                    }
                }));

        subscriptions.add(secondaryWidget.getPickLocationClickedObservable()
                .subscribe(new Action1<OnClickEvent>()
                {
                    @Override public void call(OnClickEvent onClickEvent)
                    {
                        pickLocation(PICK_LOCATION_REQUEST_SECONDARY);
                    }
                }));

        subscriptions.add(
                Observable.combineLatest(
                        liveBrokerSituationDTOObservable
                                .take(1)
                                .observeOn(Schedulers.computation())
                                .map(new Func1<LiveBrokerSituationDTO, List<CountrySpinnerAdapter.DTO>>()
                                {
                                    @Override public List<CountrySpinnerAdapter.DTO> call(LiveBrokerSituationDTO brokerSituationDTO)
                                    {
                                        return CountrySpinnerAdapter.createDTOs(
                                                Collections.singletonList(
                                                        Country.valueOf(((KYCAyondoForm) brokerSituationDTO.kycForm).getResidency().getAlpha2())),
                                                null);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread()),
                        Observable.just(Country.values())
                                .map(new Func1<Country[], List<Country>>()
                                {
                                    @Override public List<Country> call(Country[] countries)
                                    {
                                        return Arrays.asList(countries);
                                    }
                                })
                                .map(new Func1<List<Country>, List<CountrySpinnerAdapter.DTO>>()
                                {
                                    @Override public List<CountrySpinnerAdapter.DTO> call(List<Country> countries)
                                    {
                                        return CountrySpinnerAdapter.createDTOs(
                                                countries, null);
                                    }
                                })
                                .doOnNext(new Action1<List<CountrySpinnerAdapter.DTO>>()
                                {
                                    @Override public void call(List<CountrySpinnerAdapter.DTO> dtos)
                                    {
                                        Collections.sort(dtos, new CountrySpinnerAdapter.DTOCountryNameComparator(getActivity()));
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread()),
                        new Func2<List<CountrySpinnerAdapter.DTO>, List<CountrySpinnerAdapter.DTO>, Object>()
                        {
                            @Override
                            public Object call(List<CountrySpinnerAdapter.DTO> primaryCountries, List<CountrySpinnerAdapter.DTO> secondaryCountries)
                            {
                                primaryWidget.setCountries(primaryCountries, null);
                                secondaryWidget.setCountries(secondaryCountries, CountryCode.getByCode(primaryCountries.get(0).country.name()));
                                return null;
                            }
                        })
                        .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<Object, LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(Object o, LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                //noinspection ConstantConditions
                                List<KYCAddress> addresses = ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getAddresses();
                                if (addresses != null)
                                {
                                    if (addresses.size() > 0)
                                    {
                                        KYCAddress address = addresses.get(0);
                                        primaryWidget.setKYCAddress(address);
                                        if (addresses.size() > 1)
                                        {
                                            KYCAddress address1 = addresses.get(1);
                                            secondaryWidget.setKYCAddress(address1);
                                        }
                                    }
                                }
                                return null;
                            }
                        })
                        .subscribe(new EmptyAction1<>(), new TimberOnErrorAction1("Failed to load countries"))

        );

        subscriptions.add(
                Observable.combineLatest(
                        primaryWidget.getKYCAddressObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<KYCAddress>()
                                {
                                    @Override public void call(KYCAddress kycAddress)
                                    {
                                        int visibility = kycAddress.lessThanAYear ? View.VISIBLE : View.GONE;
                                        secondaryAddressWidget.setVisibility(visibility);
                                        secondaryWidget.setVisibility(visibility);
                                    }
                                }),
                        secondaryWidget.getKYCAddressObservable(),
                        new Func2<KYCAddress, KYCAddress, List<KYCAddress>>()
                        {
                            @Override public List<KYCAddress> call(KYCAddress primaryAddress, KYCAddress secondaryAddress)
                            {
                                ArrayList<KYCAddress> addresses = new ArrayList<>(2);
                                addresses.add(primaryAddress);
                                if (primaryAddress.lessThanAYear)
                                {
                                    addresses.add(secondaryAddress);
                                }
                                return addresses;
                            }
                        })
                        .withLatestFrom(liveBrokerSituationDTOObservable.map(new Func1<LiveBrokerSituationDTO, LiveBrokerDTO>()
                                {
                                    @Override public LiveBrokerDTO call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                                    {
                                        return liveBrokerSituationDTO.broker;
                                    }
                                }),
                                new Func2<List<KYCAddress>, LiveBrokerDTO, LiveBrokerSituationDTO>()
                                {
                                    @Override
                                    public LiveBrokerSituationDTO call(List<KYCAddress> kycAddresses, LiveBrokerDTO brokerDTO)
                                    {
                                        KYCAyondoForm update = new KYCAyondoForm();
                                        update.setAddresses(kycAddresses);
                                        return new LiveBrokerSituationDTO(brokerDTO, update);
                                    }
                                })
                        .subscribe(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                onNext(liveBrokerSituationDTO);
                            }
                        }, new TimberOnErrorAction1("Failed in saving updated address"))
        );

        subscriptions.add(liveBrokerSituationDTOObservable
                .map(new Func1<LiveBrokerSituationDTO, KYCAyondoForm>()
                {
                    @Override public KYCAyondoForm call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return (KYCAyondoForm) liveBrokerSituationDTO.kycForm;
                    }
                })
                .throttleLast(3, TimeUnit.SECONDS)
                .filter(new Func1<KYCAyondoForm, Boolean>()
                {
                    @Override public Boolean call(KYCAyondoForm kycAyondoForm)
                    {
                        return DummyKYCAyondoUtil.getStep4(kycAyondoForm).equals(StepStatus.COMPLETE);
                    }
                })
                .map(new Func1<KYCAyondoForm, AyondoLeadAddressDTO>()
                {
                    @Override public AyondoLeadAddressDTO call(KYCAyondoForm kycAyondoForm)
                    {
                        return new AyondoLeadAddressDTO(kycAyondoForm);
                    }
                })
                .distinctUntilChanged()
                .flatMap(new Func1<AyondoLeadAddressDTO, Observable<AyondoAddressCheckDTO>>()
                {
                    @Override public Observable<AyondoAddressCheckDTO> call(AyondoLeadAddressDTO ayondoLeadAddressDTO)
                    {
                        return liveServiceWrapper.checkNeedResidencyDocument(ayondoLeadAddressDTO);
                    }
                })
                .withLatestFrom(brokerDTOObservable, new Func2<AyondoAddressCheckDTO, LiveBrokerDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(AyondoAddressCheckDTO ayondoAddressCheckDTO, LiveBrokerDTO brokerDTO)
                    {
                        KYCAyondoForm update = new KYCAyondoForm();
                        update.setNeedResidencyDocument(ayondoAddressCheckDTO.isProofOfAddressRequired);
                        update.setAddressCheckUid(ayondoAddressCheckDTO.guid);
                        return new LiveBrokerSituationDTO(brokerDTO, update);
                    }
                })
                .subscribe(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        onNext(liveBrokerSituationDTO);
                    }
                }, new TimberOnErrorAction1("Error on checking proof of residency is required")));

        return subscriptions;
    }

    @Override protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        StepStatus fourthStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(3);
        if (btnNext != null)
        {
            btnNext.setEnabled((fourthStatus != null && StepStatus.COMPLETE.equals(fourthStatus)));
        }
    }

    @MainThread
    public void pickLocation(int requestCode)
    {
        try
        {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Context context = getActivity().getApplicationContext();
            getParentFragment().startActivityForResult(builder.build(getActivity()), requestCode);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            e.printStackTrace();
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_LOCATION_REQUEST_PRIMARY || requestCode == PICK_LOCATION_REQUEST_SECONDARY) && resultCode == Activity.RESULT_OK)
        {
            LatLng latLng = PlacePicker.getPlace(data, getActivity()).getLatLng();
            Observable.combineLatest(
                    Observable.just(latLng)
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<LatLng, List<Address>>()
                            {
                                @Override public List<Address> call(LatLng latLng)
                                {
                                    try
                                    {
                                        return mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                    }
                                    catch (IOException e)
                                    {
                                        throw new RuntimeException(e);
                                    }
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .filter(new Func1<List<Address>, Boolean>()
                            {
                                @Override public Boolean call(List<Address> addresses)
                                {
                                    return addresses.size() == 1;
                                }
                            })
                            .map(new Func1<List<Address>, KYCAddress>()
                            {
                                @Override public KYCAddress call(List<Address> addresses)
                                {
                                    Address addr = addresses.get(0);

                                    String add1 = addr.getAddressLine(0);
                                    String add2 = addr.getAddressLine(1);

                                    if (add2 != null) {
                                        if (addr.getPostalCode() != null)
                                        {
                                            add2 = add2.replace(addr.getPostalCode(), "");
                                        }

                                        if (addr.getCountryName() != null)
                                        {
                                            add2 = add2.replace(addr.getCountryName(), "");
                                        }
                                    }

                                    String city = (addr.getAdminArea() != null) ? addr.getAdminArea() : addr.getCountryName();
                                    CountryCode countryCode = CountryCode.getByCode(addr.getCountryCode());
                                    String postal = addr.getPostalCode();

                                    return new KYCAddress(add1, add2, city, countryCode, postal);
                                }
                            }),
                    Observable.just(requestCode == PICK_LOCATION_REQUEST_PRIMARY ? primaryWidget : secondaryWidget)
                            .doOnNext(new Action1<KYCAddressWidget>()
                            {
                                @Override public void call(KYCAddressWidget kycAddressWidget)
                                {
                                    kycAddressWidget.setLoading(true);
                                }
                            }),
                    Observable.just(liveBrokerSituationPreference.get()),
                    new Func3<KYCAddress, KYCAddressWidget, LiveBrokerSituationDTO, Object>()
                    {
                        @Override
                        public Object call(KYCAddress kycAddress, KYCAddressWidget kycAddressWidget, LiveBrokerSituationDTO liveBrokerSituationDTO)
                        {
                            if (((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getResidency() == kycAddress.country)
                            {
                                kycAddressWidget.setKYCAddress(kycAddress);
                            }
                            else
                            {
                                THToast.show(R.string.pick_location_within_country);
                            }

                            kycAddressWidget.setLoading(false);

                            return null;
                        }
                    }
            ).subscribe(
                    new EmptyAction1<>(),
                    new EmptyAction1<Throwable>());
        }
    }
}
