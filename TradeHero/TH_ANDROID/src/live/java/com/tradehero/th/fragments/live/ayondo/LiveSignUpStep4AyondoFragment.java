package com.tradehero.th.fragments.live.ayondo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.KYCAddress;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.widget.KYCAddressWidget;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LiveSignUpStep4AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int PICK_LOCATION_REQUEST = 2513;
    private Geocoder mGeocoder;

    @Bind(R.id.info_address_pri) KYCAddressWidget primaryWidget;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_4, container, false);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mGeocoder = new Geocoder(getActivity());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        onDestroyViewSubscriptions.add(primaryWidget.getPickLocationClickedObservable()
                        .subscribe(new Action1<OnClickEvent>()
                        {
                            @Override public void call(OnClickEvent onClickEvent)
                            {
                                pickLocation();
                            }
                        })
        );

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable()
                                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                                    {
                                        List<KYCAddress> addresses = ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getAddresses();
                                        if (addresses != null)
                                        {
                                            if (addresses.size() > 0)
                                            {
                                                KYCAddress address = addresses.get(0);
                                                primaryWidget.setKYCAddress(address);
                                            }
                                        }
                                    }
                                }),
                        primaryWidget.getKYCAddressObservable()
                                .map(new Func1<KYCAddress, List<KYCAddress>>()
                                {
                                    @Override public List<KYCAddress> call(KYCAddress kycAddress)
                                    {
                                        return Collections.singletonList(kycAddress);
                                    }
                                }),
                        new Func2<LiveBrokerSituationDTO, List<KYCAddress>, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO liveBrokerSituationDTO, List<KYCAddress> kycAddresses)
                            {
                                ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).setAddresses(kycAddresses);
                                return liveBrokerSituationDTO;
                            }
                        })
                        .subscribe(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {
                                onNext(liveBrokerSituationDTO);
                            }
                        }));
    }

    public void pickLocation()
    {
        try
        {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Context context = getActivity().getApplicationContext();
            getParentFragment().startActivityForResult(builder.build(context), PICK_LOCATION_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
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
        if (requestCode == PICK_LOCATION_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Place place = PlacePicker.getPlace(data, getActivity());
            Observable.just(place)
                    .map(new Func1<Place, LatLng>()
                    {
                        @Override public LatLng call(Place place)
                        {
                            return place.getLatLng();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<LatLng, List<Address>>()
                    {
                        @Override public List<Address> call(LatLng latLng)
                        {
                            try
                            {
                                return mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            } catch (IOException e)
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
                            String city = addr.getAdminArea();
                            String postal = addr.getPostalCode();

                            return new KYCAddress(add1, add2, city, postal);
                        }
                    })
                    .subscribe(new Action1<KYCAddress>()
                    {
                        @Override public void call(KYCAddress kycAddress)
                        {
                            primaryWidget.setKYCAddress(kycAddress);
                        }
                    });
            Timber.d("Place selected %s", place.getAddress());
        }
    }
}
