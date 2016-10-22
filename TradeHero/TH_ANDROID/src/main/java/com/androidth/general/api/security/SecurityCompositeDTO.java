package com.androidth.general.api.security;

import android.support.annotation.NonNull;


import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

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
