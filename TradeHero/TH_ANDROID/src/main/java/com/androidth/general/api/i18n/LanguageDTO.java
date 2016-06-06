package com.androidth.general.api.i18n;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Locale;

public class LanguageDTO
{
    @NonNull public final String code;
    @NonNull public final String name;
    @Nullable public String nameInOwnLang;

    //<editor-fold desc="Constructors">
    public LanguageDTO(@NonNull String code, @NonNull Locale locale)
    {
        this(code, locale.getDisplayName(), locale.getDisplayName(locale));
    }

    public LanguageDTO(@NonNull String code, @NonNull String name)
    {
        this(code, name, null);
    }

    public LanguageDTO(@NonNull String code, @NonNull String name, @Nullable String nameInOwnLang)
    {
        this.code = code;
        this.name = name;
        this.nameInOwnLang = nameInOwnLang;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return code.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(@Nullable Object other)
    {
        return other != null
                && equalClass(other)
                && equalFields((LanguageDTO) other);
    }

    protected boolean equalClass(@NonNull Object other)
    {
        return other.getClass().equals(getClass());
    }

    protected boolean equalFields(@NonNull LanguageDTO other)
    {
        return other.code.equals(code);
    }

    @Override public String toString()
    {
        return "[LanguageDTO " +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", nameInOwnLang='" + nameInOwnLang + '\'' +
                ']';
    }
}
