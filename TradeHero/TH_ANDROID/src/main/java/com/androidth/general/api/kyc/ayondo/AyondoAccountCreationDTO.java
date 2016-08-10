package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AyondoAccountCreationDTO extends AyondoLeadDTO
{
    @JsonProperty("AddressCheckGuid") @Nullable public final String addressCheckUid;
    @JsonProperty("IdentityCheckGuid") @Nullable public final String identityCheckUid;
    @JsonProperty("LeadGuid") @Nullable public final String leadGuid;

    @JsonProperty("ProofOfIdType") @Nullable public final String proofOfIdType;
    @JsonProperty("ProofOfIdImageUrl") @Nullable public final String proofOfIdImageUrl;
    @JsonProperty("ProofOfAddressType") @Nullable public final String proofOfAddressType;
    @JsonProperty("ProofOfAddressImageUrl") @Nullable public final String proofOfAddressImageUrl;

    public AyondoAccountCreationDTO(KYCAyondoForm kycAyondoForm)
    {
        super(kycAyondoForm);
        this.addressCheckUid = kycAyondoForm.getAddressCheckUid();
        this.identityCheckUid = kycAyondoForm.getIdentityCheckUid();
        this.leadGuid = kycAyondoForm.getLeadGuid();
        this.proofOfAddressImageUrl = kycAyondoForm.getProofOfAddressImageUrl();
        this.proofOfAddressType = kycAyondoForm.getProofOfAddressType();
        this.proofOfIdType = kycAyondoForm.getProofOfIdType();
        this.proofOfIdImageUrl = kycAyondoForm.getProofOfIdImageUrl();
    }
}
