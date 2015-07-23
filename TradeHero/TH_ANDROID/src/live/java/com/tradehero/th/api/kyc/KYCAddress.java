package com.tradehero.th.api.kyc;

public class KYCAddress
{
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String postalCode;
    public boolean lessThanAYear;

    public KYCAddress(String addressLine1, String addressLine2, String city, String postalCode)
    {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.lessThanAYear = false;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof KYCAddress)) return false;

        KYCAddress that = (KYCAddress) o;

        if (lessThanAYear != that.lessThanAYear) return false;
        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return !(postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null);
    }

    @Override public String toString()
    {
        return "KYCAddress{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", lessThanAYear=" + lessThanAYear +
                '}';
    }
}
