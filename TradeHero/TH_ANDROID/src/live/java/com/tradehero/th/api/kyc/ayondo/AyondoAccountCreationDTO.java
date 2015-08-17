package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AyondoAccountCreationDTO extends AyondoLeadDTO
{
    @JsonProperty("AddressCheckGuid") @Nullable private String addressCheckUid;
    @JsonProperty("IdentityCheckGuid") @Nullable private String identityCheckUid;
    @JsonProperty("ProofOfIdType") @Nullable private String proofOfIdType;
    @JsonProperty("ProofOfIdImageUrl") @Nullable private String proofOfIdImageUrl;
    @JsonProperty("ProofOfAddressType") @Nullable private String proofOfAddressType;
    @JsonProperty("ProofOfAddressImageUrl") @Nullable private String proofOfAddressImageUrl;

    public AyondoAccountCreationDTO()
    {
    }

    @Nullable public String getAddressCheckUid()
    {
        return addressCheckUid;
    }

    public void setAddressCheckUid(@Nullable String addressCheckUid)
    {
        this.addressCheckUid = addressCheckUid;
    }

    @Nullable public String getIdentityCheckUid()
    {
        return identityCheckUid;
    }

    public void setIdentityCheckUid(@Nullable String identityCheckUid)
    {
        this.identityCheckUid = identityCheckUid;
    }

    @Nullable public String getProofOfIdType()
    {
        return proofOfIdType;
    }

    public void setProofOfIdType(@Nullable String proofOfIdType)
    {
        this.proofOfIdType = proofOfIdType;
    }

    @Nullable public String getProofOfIdImageUrl()
    {
        return proofOfIdImageUrl;
    }

    public void setProofOfIdImageUrl(@Nullable String proofOfIdImageUrl)
    {
        this.proofOfIdImageUrl = proofOfIdImageUrl;
    }

    @Nullable public String getProofOfAddressType()
    {
        return proofOfAddressType;
    }

    public void setProofOfAddressType(@Nullable String proofOfAddressType)
    {
        this.proofOfAddressType = proofOfAddressType;
    }

    @Nullable public String getProofOfAddressImageUrl()
    {
        return proofOfAddressImageUrl;
    }

    public void setProofOfAddressImageUrl(@Nullable String proofOfAddressImageUrl)
    {
        this.proofOfAddressImageUrl = proofOfAddressImageUrl;
    }

    @Override public boolean isValidToCreateAccount()
    {
        return super.isValidToCreateAccount()
                && addressCheckUid != null
                && identityCheckUid != null
                && proofOfIdType != null
                && proofOfIdImageUrl != null
                && proofOfAddressType != null
                && proofOfAddressImageUrl != null;
    }
}
