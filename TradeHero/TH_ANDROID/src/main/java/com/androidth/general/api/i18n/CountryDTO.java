package com.androidth.general.api.i18n;

import android.support.annotation.NonNull;

public class CountryDTO extends LanguageDTO
{
    public CountryDTO(@NonNull String code, @NonNull String name)
    {
        super(code, name);
    }
}
