package com.androidth.general.api.i18n;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;

public class LanguageCodePredicate implements Predicate<LanguageDTO>
{
    @NonNull private final String langCode;

    //<editor-fold desc="Constructors">
    public LanguageCodePredicate(@NonNull String langCode)
    {
        this.langCode = langCode;
    }
    //</editor-fold>

    @Override public boolean apply(@NonNull LanguageDTO languageDTO)
    {
        return langCode.equals(languageDTO.code);
    }
}
