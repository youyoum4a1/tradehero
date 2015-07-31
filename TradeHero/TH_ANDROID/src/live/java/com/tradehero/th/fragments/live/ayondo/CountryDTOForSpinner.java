package com.tradehero.th.fragments.live.ayondo;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.fragments.live.CountrySpinnerAdapter;
import com.tradehero.th.models.fastfill.Gender;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CountryDTOForSpinner
{
    @NonNull public final List<Gender> genders;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedMobilePhoneCountryDTOs;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedResidencyCountryDTOs;
    @NonNull public final List<CountrySpinnerAdapter.DTO> allowedNationalityCountryDTOs;

    public CountryDTOForSpinner(@NonNull Context context, @NonNull KYCAyondoFormOptionsDTO options)
    {
        genders = Collections.unmodifiableList(options.genders);

        Comparator<CountrySpinnerAdapter.DTO> dtoComparator = new CountrySpinnerAdapter.DTOCountryNameComparator(context);

        List<CountrySpinnerAdapter.DTO> allowedMobilePhoneCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.allowedMobilePhoneCountries, null);
        Collections.sort(allowedMobilePhoneCountryDTOs, dtoComparator);

        this.allowedMobilePhoneCountryDTOs = Collections.unmodifiableList(allowedMobilePhoneCountryDTOs);

        List<CountrySpinnerAdapter.DTO> allowedResidencyCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.allowedResidencyCountries, null);
        Collections.sort(allowedResidencyCountryDTOs, dtoComparator);
        this.allowedResidencyCountryDTOs = Collections.unmodifiableList(allowedResidencyCountryDTOs);

        List<CountrySpinnerAdapter.DTO> allowedNationalityCountryDTOs = CountrySpinnerAdapter.createDTOs(
                options.allowedNationalityCountries, null);
        Collections.sort(allowedNationalityCountryDTOs, dtoComparator);
        this.allowedNationalityCountryDTOs = Collections.unmodifiableList(allowedNationalityCountryDTOs);
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
