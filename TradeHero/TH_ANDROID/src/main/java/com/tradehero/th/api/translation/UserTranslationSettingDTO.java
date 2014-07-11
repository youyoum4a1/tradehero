package com.tradehero.th.api.translation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = UserTranslationSettingDTO.class,
        property = "translatorType"
)
@JsonSubTypes(
        @JsonSubTypes.Type(value = BingUserTranslationSettingDTO.class, name = BingTranslationToken.TOKEN_TYPE)
)
public class UserTranslationSettingDTO implements DTO
{
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final boolean DEFAULT_AUTO_TRANSLATE = true;

    @NotNull public final String languageCode;
    public final boolean autoTranslate;

    //<editor-fold desc="Constructors">
    protected UserTranslationSettingDTO()
    {
        this(DEFAULT_LANGUAGE_CODE);
    }

    public UserTranslationSettingDTO(
            @NotNull String languageCode)
    {
        this(languageCode, DEFAULT_AUTO_TRANSLATE);
    }

    public UserTranslationSettingDTO(
            @NotNull String languageCode,
            boolean autoTranslate)
    {
        this.languageCode = languageCode;
        this.autoTranslate = autoTranslate;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other != null && other.getClass().equals(getClass());
    }
}
