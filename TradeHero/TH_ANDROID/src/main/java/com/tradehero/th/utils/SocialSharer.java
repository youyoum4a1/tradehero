package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by alex on 14-4-8.
 */
public interface SocialSharer
{
    void share(Context context, DTOKey shareDtoKey);
    void share(Context context, SecurityCompactDTO securityCompactDTO);
}
