package com.androidth.general.persistence.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOKey;

public class TranslationKey implements DTOKey
{
    @NonNull public final String from;
    @NonNull public final String to;
    public final int textHashCode;

    // It does not participate to the key, but is still necessary to achieve translation
    @Nullable public String translatableText;

    //<editor-fold desc="Constructors">
    public TranslationKey(@NonNull String from, @NonNull String to, @NonNull String translatableText)
    {
        this.from = from;
        this.to = to;
        this.textHashCode = translatableText.hashCode();
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
        return obj instanceof TranslationKey
                && equalFields((TranslationKey) obj);
    }

    protected boolean equalFields(@NonNull TranslationKey other)
    {
        return from.equals(other.from) &&
                to.equals(other.to) &&
                textHashCode == other.textHashCode;
    }
}
