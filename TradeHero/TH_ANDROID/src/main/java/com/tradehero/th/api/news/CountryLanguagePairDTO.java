package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:00 PM Copyright (c) TradeHero
 */
public class CountryLanguagePairDTO
        implements DTO
{
    public String name;
    public String countryCode;
    public String languageCode;

    public CountryLanguagePairDTO(String name, String countryCode, String languageCode)
    {
        this.name = name;
        this.countryCode = countryCode;
        this.languageCode = languageCode;
    }

    public CountryLanguagePairDTO()
    {
    }
}
