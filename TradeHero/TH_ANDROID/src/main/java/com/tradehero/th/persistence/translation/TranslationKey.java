package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.DTOKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslationKey implements DTOKey
{
    @NotNull public final String from;
    @NotNull public final String to;
    public final int textHashCode;

    // It does not participate to the key, but is still necessary to achieve translation
    @Nullable public String translatableText;

    //<editor-fold desc="Constructors">
    public TranslationKey(@NotNull String from, @NotNull String to, @NotNull String translatableText)
    {
        this(from, to, translatableText.hashCode(), translatableText);
    }

    public TranslationKey(@NotNull String from, @NotNull String to, int textHashCode, @Nullable String translatableText)
    {
        this.from = from;
        this.to = to;
        this.textHashCode = textHashCode;
        this.translatableText = translatableText;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return from.hashCode() ^
                to.hashCode() ^
                textHashCode;
    }

    @Override public boolean equals(@Nullable Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        return equalClass(obj) && equalFields((TranslationKey) obj);
    }

    protected boolean equalClass(@NotNull Object other)
    {
        return other.getClass().equals(getClass());
    }

    protected boolean equalFields(@NotNull TranslationKey other)
    {
        return from.equals(other.from) &&
                to.equals(other.to) &&
                textHashCode == other.textHashCode;
    }
}
