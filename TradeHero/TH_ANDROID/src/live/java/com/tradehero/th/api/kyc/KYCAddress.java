package com.ayondo.academy.api.kyc;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class KYCAddress
{
    private static final boolean DEFAULT_LESS_THAN_A_YEAR = false;

    @Nullable public final String addressLine1;
    @Nullable public final String addressLine2;
    @Nullable public final String city;
    @Nullable public final CountryCode country;
    @Nullable public final String postalCode;
    public final boolean lessThanAYear;

    public KYCAddress(
            @Nullable String addressLine1,
            @Nullable String addressLine2,
            @Nullable String city,
            @Nullable CountryCode country,
            @Nullable String postalCode)
    {
        this(addressLine1, addressLine2, city, country, postalCode, DEFAULT_LESS_THAN_A_YEAR);
    }

    public KYCAddress(
            @JsonProperty("addressLine1") @Nullable String addressLine1,
            @JsonProperty("addressLine2") @Nullable String addressLine2,
            @JsonProperty("city") @Nullable String city,
            @JsonProperty("country") @Nullable CountryCode country,
            @JsonProperty("postalCode") @Nullable String postalCode,
            @JsonProperty("lessThanAYear") boolean lessThanAYear)
    {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.lessThanAYear = lessThanAYear;
    }

    @Override public boolean equals(@Nullable Object o)
    {
        if (this == o) return true;
        if (!(o instanceof KYCAddress)) return false;

        KYCAddress that = (KYCAddress) o;

        if (lessThanAYear != that.lessThanAYear) return false;
        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        return !(postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null);
    }

    @Override public int hashCode()
    {
        return (lessThanAYear ? 0 : 1)
                ^ (addressLine1 == null ? 0 : addressLine1.hashCode())
                ^ (addressLine2 == null ? 0 : addressLine2.hashCode())
                ^ (city == null ? 0 : city.hashCode())
                ^ (country == null ? 0 : country.hashCode())
                ^ (postalCode == null ? 0 : postalCode.hashCode());
    }

    @Override public String toString()
    {
        return "KYCAddress{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", lessThanAYear=" + lessThanAYear +
                '}';
    }
}
