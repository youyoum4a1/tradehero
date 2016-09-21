package com.androidth.general.api.security;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CompositeExchangeSecurityDTO implements DTO
{
    @JsonProperty("Exchanges") List<ExchangeCompactDTO> exchanges;
    @JsonProperty("SecurityTypes") List<SecurityTypeDTO> securityTypes;

    public List<ExchangeCompactDTO> getExchanges() {
        return exchanges;
    }

    public List<SecurityTypeDTO> getSecurityTypes() {
        return securityTypes;
    }

    @Override
    public String toString() {
        return "CompositeExchangeSecurityDTO{" +
                "exchanges=" + exchanges +
                ", securityTypes=" + securityTypes +
                '}';
    }
}
