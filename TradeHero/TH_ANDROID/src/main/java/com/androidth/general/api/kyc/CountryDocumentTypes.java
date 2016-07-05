package com.androidth.general.api.kyc;

import com.androidth.general.common.persistence.DTO;

public class CountryDocumentTypes implements DTO
{
    public String displayName;
    public int documentTypeId;
    public String countryAbreviation;
    public String validation;

    public CountryDocumentTypes()
    {
        super();
    }
}
