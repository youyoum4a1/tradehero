package com.androidth.general.api.competition.key;

import android.support.annotation.NonNull;

import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.security.key.SecurityListType;

public class BasicProviderSecurityV2ListType extends ProviderSecurityListType
{
    //<editor-fold desc="Constructors">
    public BasicProviderSecurityV2ListType(ProviderSecurityListType other)
    {
        super(other);
    }

//    public BasicProviderSecurityV2ListType(ProviderId providerId, Integer page, Integer perPage)
//    {
//        super(providerId, page, perPage);
//    }
//
//    public BasicProviderSecurityV2ListType(ProviderId providerId, Integer page)
//    {
//        super(providerId, page);
//    }

    public BasicProviderSecurityV2ListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull SecurityListType other)
    {
        return super.equalFields(other)
            && other instanceof BasicProviderSecurityV2ListType;
    }
}
