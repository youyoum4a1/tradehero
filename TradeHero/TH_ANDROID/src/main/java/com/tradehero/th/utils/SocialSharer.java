package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.common.persistence.DTOKey;

/**
 * Created by alex on 14-4-8.
 */
public interface SocialSharer
{
    void share(Context context, DTOKey shareDtoKey);
}
