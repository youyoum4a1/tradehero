package com.androidth.general.api.security;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = SecurityCompositeDTO.class
    )

public class SecurityCompositeDTO implements DTO
{
    public ArrayList<ExchangeCompactDTO> Exchanges;
    public ArrayList<SecurityTypeDTO> SecurityTypes;
    public ArrayList<SecurityCompactDTO> Securities;
    public ArrayList<ProviderSortCategoryDTO> SortCategories;

    //<editor-fold desc="Constructors">
    public SecurityCompositeDTO()
    {
        super();
    }

    public SecurityCompositeDTO(@NonNull SecurityCompositeDTO other)
    {
        super();
        this.Exchanges = other.Exchanges;
        this.SecurityTypes = other.SecurityTypes;
        this.Securities = other.Securities;
        this.SortCategories = other.SortCategories;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SecurityCompositeDTO{" +
                "Exchanges=" + Exchanges.size() +
                ", SecurityTypes='" + SecurityTypes.size() + '\'' +
                ", Securities='" + Securities.size() + '\'' +
                ", SortCategories='" + SortCategories.size() + '\'' +
                '}';
    }

}
