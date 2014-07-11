package com.tradehero.th.api.i18n;

import com.android.internal.util.Predicate;
import org.jetbrains.annotations.NotNull;

public class LanguageCodePredicate implements Predicate<LanguageDTO>
{
    @NotNull private final String langCode;

    //<editor-fold desc="Constructors">
    public LanguageCodePredicate(@NotNull String langCode)
    {
        this.langCode = langCode;
    }
    //</editor-fold>

    @Override public boolean apply(@NotNull LanguageDTO languageDTO)
    {
        return langCode.equals(languageDTO.code);
    }
}
