package com.tradehero.th.api.news;

public class CountryLanguagePairDTO
{
    public static final String BUNDLE_KEY_COUNTRY_CODE = CountryLanguagePairDTO.class.getName() + ".countryCode";
    public static final String BUNDLE_KEY_LANGUAGE_CODE = CountryLanguagePairDTO.class.getName() + ".languageCode";

    public String name;
    public String countryCode;
    public String languageCode;

    public CountryLanguagePairDTO(String name, String countryCode, String languageCode)
    {
        this.name = name;
        this.countryCode = countryCode;
        this.languageCode = languageCode;
    }

    /** Naked constructor for deserialization */
    public CountryLanguagePairDTO() { }

    @Override public String toString()
    {
        return this.name;
    }
}
