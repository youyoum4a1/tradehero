package com.androidth.general.fragments.kyc;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.market.Country;
import com.androidth.general.fragments.kyc.adapter.CountrySpinnerAdapter;
import com.androidth.general.models.fastfill.Gender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CountryDTOForSpinner
{
    @NonNull public final List<Gender> genders;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedMobilePhoneCountryDTOs;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedResidencyCountryDTOs;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedNationalityCountryDTOs;
    @NonNull public final List<String> residenceStateList;
    @NonNull public final List<String> howYouKnowTHList;

    public CountryDTOForSpinner(@NonNull Context context, @NonNull KYCAyondoFormOptionsDTO options)
    {
        this(context, options, null);
    }
    public CountryDTOForSpinner(@NonNull Context context, @NonNull KYCAyondoFormOptionsDTO options, Country country){

        genders = Collections.unmodifiableList(options.getGenders());
        Comparator<CountrySpinnerAdapter.DTO> dtoComparator;

        if(country != null) {
            dtoComparator = new CountrySpinnerAdapter.DTOCountryNameComparator(context, country);
        }
        else{
            dtoComparator = new CountrySpinnerAdapter.DTOCountryNameComparator(context);
        }

        List<CountrySpinnerAdapter.DTO> allowedMobilePhoneCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.getAllowedMobilePhoneCountries(), null);
        Collections.sort(allowedMobilePhoneCountryDTOs, dtoComparator);

        this.allowedMobilePhoneCountryDTOs = allowedMobilePhoneCountryDTOs;

        List<CountrySpinnerAdapter.DTO> allowedResidencyCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.getAllowedResidencyCountries(), null);
        Collections.sort(allowedResidencyCountryDTOs, dtoComparator);
        this.allowedResidencyCountryDTOs = Collections.unmodifiableList(allowedResidencyCountryDTOs);

        List<CountrySpinnerAdapter.DTO> allowedNationalityCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.getAllowedNationalityCountries(), null);
        Collections.sort(allowedNationalityCountryDTOs, dtoComparator);
        this.allowedNationalityCountryDTOs = Collections.unmodifiableList(allowedNationalityCountryDTOs);

        this.residenceStateList = options.getResidenceState();
        this.howYouKnowTHList = options.getHowYouKnowTH();
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof CountryDTOForSpinner)) return false;

        CountryDTOForSpinner that = (CountryDTOForSpinner) o;

        if (!genders.equals(that.genders)) return false;
        if (!allowedMobilePhoneCountryDTOs.equals(that.allowedMobilePhoneCountryDTOs)) return false;
        if (!allowedResidencyCountryDTOs.equals(that.allowedResidencyCountryDTOs)) return false;
        return allowedNationalityCountryDTOs.equals(that.allowedNationalityCountryDTOs);
    }
}
