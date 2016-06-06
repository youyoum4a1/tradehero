package com.androidth.general.api.translation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.translation.bing.BingTranslationToken;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = TranslationToken.class,
        property = "type"
)
@JsonSubTypes(
        @JsonSubTypes.Type(value = BingTranslationToken.class, name = BingTranslationToken.TOKEN_TYPE)
)
public class TranslationToken implements DTO
{
    public boolean isValid()
    {
        throw new IllegalStateException("Needs to be implemented");
    }

    public int logoResId()
    {
        return R.drawable.default_image;
    }
}
