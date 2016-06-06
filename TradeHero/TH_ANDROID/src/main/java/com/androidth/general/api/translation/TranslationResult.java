package com.androidth.general.api.translation;

import android.support.annotation.DrawableRes;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;

public class TranslationResult implements DTO
{
    public String getContent()
    {
        return null;
    }

    @DrawableRes public int logoResId()
    {
        return R.drawable.default_image;
    }
}
