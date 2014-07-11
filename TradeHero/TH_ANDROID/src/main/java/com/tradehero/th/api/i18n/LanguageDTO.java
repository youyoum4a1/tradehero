package com.tradehero.th.api.i18n;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LanguageDTO
{
    @NotNull public final String code;
    @NotNull public final String name;
    @Nullable public String nameInOwnLang;

    //<editor-fold desc="Constructors">
    public LanguageDTO(@NotNull String code)
    {
        this(code, Locale.forLanguageTag(code));
    }

    public LanguageDTO(@NotNull String code, @NotNull Locale locale)
    {
        this(code, locale.getDisplayName(), locale.getDisplayName(locale));
    }

    public LanguageDTO(@NotNull String code, @NotNull String name)
    {
        this(code, name, null);
    }

    public LanguageDTO(@NotNull String code, @NotNull String name, @Nullable String nameInOwnLang)
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

    protected boolean equalClass(@NotNull Object other)
    {
        return other.getClass().equals(getClass());
    }

    protected boolean equalFields(@NotNull LanguageDTO other)
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
