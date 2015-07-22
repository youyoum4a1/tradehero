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
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.tradehero.th.R;
import java.io.IOException;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LiveSignUpStep4AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    private static final int PICK_LOCATION_REQUEST = 2513;
    private Geocoder mGeocoder;

    @Bind(R.id.info_address_line1) EditText txtLine1;
    @Bind(R.id.info_address_line2) EditText txtLine2;
    @Bind(R.id.info_city) EditText txtCity;
    @Bind(R.id.info_postal_code) EditText txtPostalCode;

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
    }

    @OnClick(R.id.info_pick_location)
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
                    .subscribe(new Action1<List<Address>>()
                    {
                        @Override public void call(List<Address> addresses)
                        {
                            Timber.d("addresses %s", addresses);
                            if (addresses.get(0) != null)
                            {
                                Address addr = addresses.get(0);

                                String fName = addr.getFeatureName();
                                String add1 = addr.getAddressLine(0);
                                String add2 = addr.getAddressLine(1);

                                txtLine1.setText(fName != null ? fName : add1);
                                txtLine2.setText(fName != null ? add1 : add2);

                                if (addr.getAdminArea() != null)
                                {
                                    txtCity.setText(addr.getAdminArea());
                                }

                                if (addr.getPostalCode() != null)
                                {
                                    txtPostalCode.setText(addr.getPostalCode());
                                }
                            }
                        }
                    });
            Timber.d("Place selected %s", place.getAddress());
        }
    }
}
