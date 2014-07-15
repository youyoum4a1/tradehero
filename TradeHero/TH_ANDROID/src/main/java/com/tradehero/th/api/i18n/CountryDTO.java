package com.tradehero.th.api.i18n;

import org.jetbrains.annotations.NotNull;

public class CountryDTO extends LanguageDTO
{
    public CountryDTO(@NotNull String code, @NotNull String name)
    {
        super(code, name);
    }
}
